package pl.gocards.ui.decks.xml.standard.dialog;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.nio.file.Path;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.xml.MainActivity;
import pl.gocards.util.FirebaseAnalyticsHelper;

/**
 * D_C_06 Create a new deck
 * @author Grzegorz Ziemski
 */
public class CreateDeckDialog extends BaseDialogFragment {

    private static final String TAG = "CreateDeckDialog";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Path currentFolder;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private View layout;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;

    private final boolean isRotated;

    public CreateDeckDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public CreateDeckDialog(@NonNull FragmentActivity activity, @NonNull Path currentFolder) {
        setParentActivity(activity);
        this.currentFolder = currentFolder;
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
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_deck_create_dialog_title)
                .setMessage(R.string.decks_list_deck_create_dialog_message)
                .setView(layout)
                .setPositiveButton(R.string.ok, onClickOk())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create();
    }

    @NonNull
    @SuppressLint("CheckResult")
    protected DialogInterface.OnClickListener onClickOk() {
        return (dialog, which) -> getExceptionHandler().tryRun(() -> {
            String deckName = getDeckNameEditText().getText().toString();
            if (!deckName.isEmpty()) {
                String deckDbPath = currentFolder + "/" + deckName;
                try {
                    deckDb = createDatabase(deckDbPath);
                    forceCreateDb(deckName);
                } catch (DatabaseException e) {
                    showDatabaseExistsToast(deckName);
                }
            }
        }, this::onError);
    }

    @SuppressLint("CheckResult")
    protected void forceCreateDb(String deckName) {
        Disposable disposable = deckDb.cardRxDao().deleteAll()
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> onComplete(deckName))
                .subscribe(EMPTY_ACTION, this::onError);
        addToDisposable(disposable);
    }

    protected void onComplete(String deckName) {
        requireParentActivity().refreshItems();
        runOnUiThread(() -> showDatabaseCreatedToast(deckName), this::onError);

        FirebaseAnalyticsHelper
                .getInstance(getApplicationContext())
                .createDeck();
    }

    @UiThread
    protected void showDatabaseCreatedToast(String deckName) {
        showShortToastMessage(String.format(getStringHelper(R.string.decks_list_deck_create_dialog_toast_created), deckName));
    }

    @UiThread
    protected void showDatabaseExistsToast(String deckName) {
        showShortToastMessage(String.format(getStringHelper(R.string.decks_list_deck_create_dialog_toast_exists), deckName));
    }

    @Override
    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(), TAG,
                "Error while creating new deck."
        );
    }

    protected EditText getDeckNameEditText() {
        return layout.findViewById(R.id.deckNameEditText);
    }

    @NonNull
    protected DeckDatabase createDatabase(@NonNull String dbPath) throws DatabaseException {
        return AppDeckDbUtil
                .getInstance(getApplicationContext())
                .createDatabase(getApplicationContext(), dbPath);
    }
    @NonNull
    protected MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean addToDisposable(@NonNull Disposable disposable) {
        return requireParentActivity().addToDisposable(disposable);
    }
}
