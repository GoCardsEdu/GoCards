package pl.softfly.flashcards.ui.decks.folder.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.nio.file.Path;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.base.BaseDialogFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

public class RenameFolderDialog extends BaseDialogFragment {

    private final Path folderPath;

    private MainActivity activity;

    private View layout;

    public RenameFolderDialog(Path folderPath) {
        this.folderPath = folderPath;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = (MainActivity) requireActivity();
        layout = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_deck, null);
        getDeckNameEditText().setText(folderPath.getFileName().toString());

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Rename the folder")
                .setMessage("Please enter a new name:")
                .setView(layout)
                .setPositiveButton("OK", onClickOk())
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .create();
    }

    @SuppressLint("CheckResult")
    protected DialogInterface.OnClickListener onClickOk() {
        return (dialog, which) -> {
            try {
                String newFolderName = getDeckNameEditText().getText().toString();
                if (!newFolderName.isEmpty()) {
                    getDeckDatabaseUtil().renameFolder(folderPath, newFolderName)
                            .subscribe(merged -> {
                                getMainActivity().refreshItems();
                                showToastFolderRenamed(newFolderName, merged);
                            }, this::onError);
                }
            } catch (IOException e) {
                onError(e);
            }
        };
    }

    protected void showToastFolderRenamed(String folderName, boolean merged) {
        String message;
        if (merged) {
            message = String.format("The folder \"%s\" has been merged with another folder.", folderName);
        } else {
            message = String.format("The folder has been renamed to \"%s\".", folderName);
        }
        runOnUiThread(
                () -> Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show(),
                this::onError
        );
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
