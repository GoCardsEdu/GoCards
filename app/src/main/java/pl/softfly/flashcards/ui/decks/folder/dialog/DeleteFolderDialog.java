package pl.softfly.flashcards.ui.decks.folder.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.decks.standard.DeckViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

public class DeleteFolderDialog extends BaseDialogFragment {

    private final Path currentFolder;

    private final DeckViewAdapter adapter;

    public DeleteFolderDialog(Path currentFolder, DeckViewAdapter adapter) {
        this.adapter = adapter;
        this.currentFolder = currentFolder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Remove a deck of cards")
                .setMessage("Are you sure you want to delete the folder with all decks?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        deleteFolderWithAllFiles(currentFolder);
                        deleteDecksDb(currentFolder);
                    } catch (IOException e) {
                        onError(e);
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {})
                .create();
    }

    private void deleteDecksDb(@NonNull Path folder) {
        getAppDatabase().deckDaoAsync()
                .deleteByStartWithPath(folder.toString())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    adapter.refreshItems();
                    runOnUiThread(
                            () -> Toast.makeText(
                                    requireContext(),
                                    "The folder has been deleted.",
                                    Toast.LENGTH_SHORT
                            ).show(), this::onError);
                });
    }

    private void deleteFolderWithAllFiles(@NonNull Path folder) throws IOException {
        Files.walk(folder)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @NonNull
    protected void onError(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while deleting folder."
        );
    }

    public void runOnUiThread(Runnable action, Consumer<? super Throwable> onError) {
        ((MainActivity) getActivity()).runOnUiThread(action, onError);
    }
}
