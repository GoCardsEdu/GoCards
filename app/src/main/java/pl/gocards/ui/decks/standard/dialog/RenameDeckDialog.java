package pl.gocards.ui.decks.standard.dialog;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.nio.file.Path;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.room.util.DbUtil;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.MainActivity;

/**
 * D_U_07 Rename the deck
 * @author Grzegorz Ziemski
 */
public class RenameDeckDialog extends BaseDialogFragment {

    private static final String TAG = "RenameDeckDialog";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Path deckDbPath;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private View layout;

    private final boolean isRotated;

    public RenameDeckDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public RenameDeckDialog(@NonNull AppCompatActivity activity, @NonNull Path deckDbPath) {
        setParentActivity(activity);
        this.deckDbPath = deckDbPath;
        isRotated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        layout = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_deck, null);
        getDeckNameEditText().setText(DbUtil.removeDbExtension(deckDbPath.getFileName().toString()));
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_deck_rename_dialog_title)
                .setMessage(R.string.decks_list_deck_rename_dialog_message)
                .setView(layout)
                .setPositiveButton(R.string.ok, onClickOk())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create();
    }

    @NonNull
    @SuppressLint("CheckResult")
    protected DialogInterface.OnClickListener onClickOk() {
        return (dialog, which) -> getExceptionHandler().tryRun(() -> {
            String newDeckName = getDeckNameEditText().getText().toString();
            if (!newDeckName.isEmpty()) {
                if (newDeckName.equals(AppDeckDbUtil.getDeckName(deckDbPath))) {
                    showToastDeckRenamed(newDeckName);
                    return;
                }
                try {
                    Disposable disposable = getDeckDbUtil().renameDatabase(getApplicationContext(), deckDbPath, newDeckName)
                            .subscribeOn(Schedulers.io())
                            .doOnSuccess(deckName -> {
                                requireParentActivity().refreshItems();
                                showToastDeckRenamed(deckName);
                            })
                            .ignoreElement()
                            .subscribe(EMPTY_ACTION, this::onError);
                    requireParentActivity().addToDisposable(disposable);
                } catch (DatabaseException e) {
                    throw new RuntimeException(e);
                }
            }
        }, this::onError);
    }

    protected void showToastDeckRenamed(String deckName) {
        runOnUiThread(() -> showShortToastMessage(String.format(getStringHelper(R.string.decks_list_deck_rename_dialog_toast_renamed), deckName)));
    }

    @Override
    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(), TAG,
                "Error while renaming the deck."
        );
    }

    protected EditText getDeckNameEditText() {
        return layout.findViewById(R.id.deckNameEditText);
    }

    @NonNull
    @Override
    protected MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }
}
