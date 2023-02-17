package pl.softfly.flashcards.ui.decks.standard.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.reactivex.rxjava3.core.Completable;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.decks.standard.DeckViewAdapter;

public class RemoveDeckDialog extends BaseDialogFragment {

    private final String path;

    private final DeckViewAdapter adapter;

    public RemoveDeckDialog(String path, DeckViewAdapter adapter) {
        this.adapter = adapter;
        this.path = path;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Remove a deck of cards")
                .setMessage("Are you sure you want to delete the deck with all cards?")
                .setPositiveButton("Yes", (dialog, which) ->
                        getExceptionHandler().tryRun(
                                () -> deleteDatabase(path).subscribe(this::onSuccess), this::onError)
                )
                .setNegativeButton("No", (dialog, which) -> {})
                .create();
    }

    protected void onSuccess() {
        adapter.refreshItems();
        runOnUiThread(() ->
                Toast.makeText(
                        getContext(),
                        "The deck has been deleted.",
                        Toast.LENGTH_SHORT
                ).show(), this::onError);
    }

    @NonNull
    protected void onError(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while removing deck."
        );
    }

    protected Completable deleteDatabase(String dbPath) {
        return DeckDatabaseUtil
                .getInstance(getContext())
                .deleteDatabase(dbPath);
    }
}
