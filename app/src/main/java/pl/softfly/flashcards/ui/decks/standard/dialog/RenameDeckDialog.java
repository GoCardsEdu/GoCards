package pl.softfly.flashcards.ui.decks.standard.dialog;

import static pl.softfly.flashcards.db.DeckDatabaseUtil.removeDbExtension;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

public class RenameDeckDialog extends BaseDialogFragment {

    private final Path dbPath;

    private MainActivity activity;

    private View layout;

    public RenameDeckDialog(Path dbPath) {
        this.dbPath = dbPath;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = (MainActivity) requireActivity();
        layout = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_deck, null);
        getDeckNameEditText().setText(removeDbExtension(dbPath.getFileName().toString()));
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Rename the deck")
                .setMessage("Please enter a new name:")
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
                getDeckDatabaseUtil().renameDatabase(dbPath, deckName)
                        .subscribeOn(Schedulers.io())
                        .subscribe(finalDeckName -> {
                            getMainActivity().refreshItems();
                            showToastDeckRenamed(finalDeckName);
                        }, this::onError);
            }
        }, this::onError);
    }

    protected void showToastDeckRenamed(String deckName) {
        runOnUiThread(
                () -> Toast.makeText(
                        getMainActivity(),
                        String.format("Renamed to \"%s\".", deckName),
                        Toast.LENGTH_SHORT
                ).show(), this::onError);
    }

    @NonNull
    protected void onError(Throwable e) {
        getExceptionHandler().handleException(
                e, getMainActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while renaming the deck."
        );
    }

    protected EditText getDeckNameEditText() {
        return layout.findViewById(R.id.deckNameEditText);
    }

    protected MainActivity getMainActivity() {
        return activity;
    }

    @Override
    protected void runOnUiThread(Runnable action) {
        getMainActivity().runOnUiThread(action);
    }
}
