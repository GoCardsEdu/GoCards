package pl.softfly.flashcards.ui.decks.folder.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.nio.file.Path;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.decks.standard.DeckViewAdapter;

public class CreateFolderDialog extends BaseDialogFragment {

    private final Path currentFolder;

    private final DeckViewAdapter adapter;

    public CreateFolderDialog(Path currentFolder, DeckViewAdapter adapter) {
        this.adapter = adapter;
        this.currentFolder = currentFolder;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = getActivity().getLayoutInflater().inflate(R.layout.dialog_create_folder, null);
        return new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Create a new folder")
                .setMessage("Please enter the name:")
                .setView(root)
                .setPositiveButton("OK", onClickOk(root))
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .create();
    }

    protected DialogInterface.OnClickListener onClickOk(View root) {
        return (dialog, which) -> getExceptionHandler().tryRun(() -> {
            EditText deckNameEditText = root.findViewById(R.id.folderName);
            String newFolderName = deckNameEditText.getText().toString();
            if (!newFolderName.isEmpty()) {
                File folder = new File(currentFolder.toString() + "/" + newFolderName);
                if (!folder.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    folder.mkdir();
                    adapter.refreshItems();
                    showToastFolderCreated(newFolderName);
                } else {
                    showToastFolderExists(newFolderName);
                }
            }
        }, this::onError);
    }

    protected void showToastFolderCreated(String newFolderName) {
        Toast.makeText(
                requireContext(),
                "\"" + newFolderName + "\" folder created.",
                Toast.LENGTH_SHORT
        ).show();
    }

    protected void showToastFolderExists(String newFolderName) {
        Toast.makeText(
                requireContext(),
                "\"" + newFolderName + "\" folder already exists.",
                Toast.LENGTH_SHORT
        ).show();
    }

    @NonNull
    protected void onError(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while creating new folder."
        );
    }
}
