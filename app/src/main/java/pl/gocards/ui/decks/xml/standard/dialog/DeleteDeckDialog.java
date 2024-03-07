package pl.gocards.ui.decks.xml.standard.dialog;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.nio.file.Path;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.xml.MainActivity;

/**
 * D_R_08 Delete the deck
 */
public class DeleteDeckDialog extends BaseDialogFragment {

    private static final String TAG = "DeleteDeckDialog";

    private final static DialogInterface.OnClickListener EMPTY_ONCLICK_LISTENER = (dialog, which) -> {};

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Path deckDbPath;

    private final boolean isRotated;

    public DeleteDeckDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public DeleteDeckDialog(@NonNull AppCompatActivity activity, @NonNull Path deckDbPath) {
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_deck_delete_dialog_title)
                .setMessage(R.string.decks_list_deck_delete_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                            Disposable disposable = deleteDatabase(deckDbPath)
                                    .doOnComplete(this::onSuccess)
                                    .subscribe(EMPTY_ACTION, this::onError);
                            requireParentActivity().addToDisposable(disposable);
                        }
                )
                .setNegativeButton(R.string.no, EMPTY_ONCLICK_LISTENER)
                .create();
    }

    protected void onSuccess() {
        requireParentActivity().refreshItems();
        runOnUiThread(() -> showShortToastMessage(R.string.decks_list_deck_delete_dialog_toast));
    }

    @Override
    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(), TAG,
                "Error while deleting deck."
        );
    }

    protected Completable deleteDatabase(@NonNull Path dbPath) {
        return getDeckDbUtil().deleteDatabaseCompletable(getApplicationContext(), dbPath);
    }

    @NonNull
    @Override
    protected MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }
}
