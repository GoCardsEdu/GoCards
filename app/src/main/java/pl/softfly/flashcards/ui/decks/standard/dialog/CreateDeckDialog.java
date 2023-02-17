package pl.softfly.flashcards.ui.decks.standard.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.nio.file.Path;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

public class CreateDeckDialog extends BaseDialogFragment {

    private final Path currentFolder;

    private MainActivity activity;

    private View layout;

    public CreateDeckDialog(Path currentFolder) {
        this.currentFolder = currentFolder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = (MainActivity) requireActivity();
        layout = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_deck, null);
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Create a new deck")
                .setMessage("Please enter the name:")
                .setView(layout)
                .setPositiveButton("OK", onClickOk())
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .create();
    }

    @SuppressLint("CheckResult")
    protected DialogInterface.OnClickListener onClickOk() {
        return (dialog, which) -> getExceptionHandler().tryRun(() -> {
            String deckName = getDeckNameEditText().getText().toString();
            if (!deckName.isEmpty()) {
                String deckDbPath = currentFolder.toString() + "/" + deckName;
                DeckDatabase deckDb;
                try {
                    deckDb = createDatabase(deckDbPath);
                } catch (DatabaseException e) {
                    throw new RuntimeException(e);
                }

                // This is used to force the creation of a DB.
                deckDb.cardDaoAsync().deleteAll()
                        .andThen(getAppDatabase().deckDaoAsync().refreshLastUpdatedAt(deckDbPath))
                        .subscribeOn(Schedulers.io())
                        .subscribe(deck -> onComplete(deck.getName()), this::onError);
            }
        }, this::onError);
    }

    protected void onComplete(String deckName) {
        runOnUiThread(
                () -> {
                    getMainActivity().refreshItems();
                    Toast.makeText(
                            getMainActivity(),
                            "\"" + deckName + "\" deck created.",
                            Toast.LENGTH_SHORT
                    ).show();
                }, this::onError);
    }

    @NonNull
    protected void onError(Throwable e) {
        getExceptionHandler().handleException(
                e, getMainActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while creating new deck."
        );
    }
    protected EditText getDeckNameEditText() {
        return layout.findViewById(R.id.deckNameEditText);
    }

    protected DeckDatabase createDatabase(String dbPath) throws DatabaseException {
        return DeckDatabaseUtil
                .getInstance(getContext())
                .createDatabase(dbPath);
    }

    protected MainActivity getMainActivity() {
        return activity;
    }

    @Override
    protected void runOnUiThread(Runnable action) {
        getMainActivity().runOnUiThread(action);
    }
}
