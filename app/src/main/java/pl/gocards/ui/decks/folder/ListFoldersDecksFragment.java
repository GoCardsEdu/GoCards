package pl.gocards.ui.decks.folder;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.ui.decks.folder.dialog.CreateFolderDialog;
import pl.gocards.ui.decks.standard.ListDecksFragment;
import pl.gocards.ui.main.MainActivity;

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
public class ListFoldersDecksFragment extends ListDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).hideBackArrow();
        initCutPathObserver();
    }

    @NonNull
    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new FolderDeckViewAdapter(this);
    }

    /**
     * D_U_10 Paste the deck
     * F_C_07 Paste the folder
     */
    protected void initCutPathObserver() {
        getAdapter().getCutPathLiveData().observe(this, cutPath ->
                getExceptionHandler().tryRun(() -> {
                    if (cutPath != null) {
                        if (Files.isDirectory(Paths.get(cutPath))) {
                            getPasteButton().setText(R.string.decks_list_paste_folder_here);
                        } else {
                            getPasteButton().setText(R.string.decks_list_paste_deck_here);
                        }
                        getPasteMenuBottom().setVisibility(View.VISIBLE);
                        getCancelButton().setOnClickListener(v -> getAdapter().getCutPathLiveData().postValue(null));
                        getPasteButton().setOnClickListener(v -> onClickPaste(cutPath));
                    } else {
                        getPasteMenuBottom().setVisibility(View.GONE);
                    }
                }, this::onErrorCutDeckPath)
        );
    }

    protected void onErrorCutDeckPath(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(),
                "Error while pasting folder / deck."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Fragment methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_folder) {
            newFolder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * F_C_03 Create a folder
     */
    protected void newFolder() {
        DialogFragment dialog = new CreateFolderDialog(requireActivity(), getAdapter().getCurrentFolder());
        dialog.show(requireActivity().getSupportFragmentManager(), "CreateFolder");
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    protected void onClickPaste(String cutPath) {
        try {
            if (Files.isDirectory(Paths.get(cutPath))) {
                onClickPasteFolder(cutPath);
            } else {
                onClickPasteDeck(cutPath);
            }
        } catch (IOException e) {
            onErrorCutDeckPath(e);
        }
    }

    /**
     * F_C_07 Paste the folder
     */

    @SuppressLint("CheckResult")
    protected void onClickPasteFolder(String from) throws IOException {
        Path fromPath = Paths.get(from);

        Path toPath = Paths.get(
                getAdapter().getCurrentFolder().toString(),
                fromPath.getFileName().toString()
        );

        Disposable disposable = getDeckDbUtil().moveFolder(requireContext(), fromPath, toPath)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(merged -> {
                    getAdapter().loadItems();
                    getAdapter().getCutPathLiveData().postValue(null);
                    showToastFolderMoved(fromPath.getFileName().toString(), merged);
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorCutDeckPath);
        addToDisposable(disposable);
    }

    protected void showToastFolderMoved(String folderName, boolean merged) {
        int resId;
        if (merged) {
            resId = R.string.decks_list_paste_folder_toast_merged;
        } else {
            resId = R.string.decks_list_paste_folder_toast_moved;
        }
        runOnUiThread(
                () -> showShortToastMessage(String.format(getString(resId), folderName)),
                this::onErrorCutDeckPath
        );
    }

    /**
     * D_U_10 Paste the deck
     */
    @SuppressLint("CheckResult")
    protected void onClickPasteDeck(String cutPath) {
        Disposable disposable = getDeckDbUtil().moveDatabase(
                        requireContext(),
                        Paths.get(cutPath),
                        getAdapter().getCurrentFolder()
                )
                .doOnSuccess(newDeckName -> {
                    getAdapter().loadItems();
                    getAdapter().getCutPathLiveData().postValue(null);
                    showToastDeckMoved(newDeckName);
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorCutDeckPath);
        addToDisposable(disposable);
    }

    protected void showToastDeckMoved(String deckName) {
        runOnUiThread(
                () ->  showShortToastMessage(String.format(getString(R.string.decks_list_paste_deck_toast), deckName)),
                this::onErrorCutDeckPath
        );
    }

    public void onSupportNavigateUp() {
        getAdapter().goFolderUp();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }

    @NonNull
    protected Button getPasteButton() {
        return getBinding().pasteButton;
    }

    @NonNull
    protected Button getCancelButton() {
        return getBinding().cancelButton;
    }

    @NonNull
    protected ConstraintLayout getPasteMenuBottom() {
        return getBinding().pasteMenuBottom;
    }
}