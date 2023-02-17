package pl.softfly.flashcards.ui.decks.folder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.decks.folder.dialog.CreateFolderDialog;
import pl.softfly.flashcards.ui.main.MainActivity;
import pl.softfly.flashcards.ui.decks.standard.ListDecksFragment;

/**
 * @author Grzegorz Ziemski
 */
public class ListFoldersDecksFragment extends ListDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initHandleOnBackPressed();
        ((MainActivity) requireActivity()).hideBackArrow();
        initCutDeckPathObserver();
    }

    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new FolderDeckViewAdapter((MainActivity) getActivity(), this);
    }

    protected void initHandleOnBackPressed() {
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getAdapter().goFolderUp();
                    }
                });
    }

    protected void initCutDeckPathObserver() {
        getAdapter().getCutPathLiveData().observe(this, cutPath ->
                getExceptionHandler().tryRun(() -> {
                    if (cutPath != null) {
                        if (Files.isDirectory(Paths.get(cutPath))) {
                            getBinding().pasteButton.setText("Paste the folder here");
                        } else {
                            getBinding().pasteButton.setText("Paste the deck here");
                        }
                        getBinding().pasteMenuBottom.setVisibility(View.VISIBLE);
                        getBinding().cancelButton.setOnClickListener(v -> getAdapter().getCutPathLiveData().postValue(null));
                        getBinding().pasteButton.setOnClickListener(v -> onClickPaste(cutPath));
                    } else {
                        getBinding().pasteMenuBottom.setVisibility(View.GONE);
                    }
                }, this::onErrorCutDeckPath));
    }

    protected void onErrorCutDeckPath(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while pasting folder / deck."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Fragment methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_folder: {
                newFolder();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void newFolder() {
        DialogFragment dialog = new CreateFolderDialog(getAdapter().getCurrentFolder(), getAdapter());
        dialog.show(getActivity().getSupportFragmentManager(), "CreateFolder");
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

    @SuppressLint("CheckResult")
    protected void onClickPasteFolder(String from) throws IOException {
        Path fromPath = Paths.get(from);

        Path toPath = Paths.get(
                getAdapter().getCurrentFolder().toString(),
                fromPath.getFileName().toString()
        );

        getDeckDatabaseUtil().moveFolder(fromPath, toPath)
                .subscribeOn(Schedulers.io())
                .subscribe(merged -> {
                    getAdapter().refreshItems();
                    getAdapter().getCutPathLiveData().postValue(null);

                    showToastFolderMoved(fromPath.getFileName().toString(), merged);
                }, this::onErrorCutDeckPath);
    }

    protected void showToastFolderMoved(String fileName, boolean merged) {
        String message;
        if (merged) {
            message = "The \"" + fileName + "\" has been merged with another folder.";
        } else {
            message = "The \"" + fileName + "\" has been moved.";
        }
        runOnUiThread(
                () -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show(),
                this::onErrorCutDeckPath
        );
    }

    @SuppressLint("CheckResult")
    protected void onClickPasteDeck(String cutPath) {
        getDeckDatabaseUtil().moveDatabase(cutPath, getAdapter().getCurrentFolder().toString())
                .subscribe(newDeckName -> {
                    getAdapter().refreshItems();
                    getAdapter().getCutPathLiveData().postValue(null);
                    showToastDeckMoved(newDeckName);
                }, this::onErrorCutDeckPath);
    }

    protected void showToastDeckMoved(String deckName) {
        runOnUiThread(
                () -> Toast.makeText(
                        getActivity(),
                        String.format("The \"%s\" has been moved.", deckName),
                        Toast.LENGTH_SHORT
                ).show(), this::onErrorCutDeckPath);
    }

    public boolean onSupportNavigateUp() {
        getAdapter().goFolderUp();
        return true;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @Override
    public FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }


}