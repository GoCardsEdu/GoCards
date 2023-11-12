package pl.gocards.ui.decks.folder.dialog;

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

import java.io.IOException;
import java.nio.file.Path;

import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.MainActivity;

/**
 * F_U_04 Rename the folder
 * @author Grzegorz Ziemski
 */
public class RenameFolderDialog extends BaseDialogFragment {

    private static final String TAG = "RenameFolderDialog";

    private Path folderPath;

    private View layout;

    private final boolean isRotated;

    public RenameFolderDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public RenameFolderDialog(@NonNull AppCompatActivity activity, Path folderPath) {
        setParentActivity(activity);
        this.folderPath = folderPath;
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
        getDeckNameEditText().setText(folderPath.getFileName().toString());

        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_folder_rename_dialog_title)
                .setMessage(R.string.decks_list_folder_rename_dialog_message)
                .setView(layout)
                .setPositiveButton(R.string.ok, onClickOk())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create();
    }

    @NonNull
    @SuppressLint("CheckResult")
    protected DialogInterface.OnClickListener onClickOk() {
        return (dialog, which) -> {
            try {
                String newFolderName = getDeckNameEditText().getText().toString();
                if (!newFolderName.isEmpty()) {
                    Disposable disposable = getDeckDbUtil().renameFolder(requireContext(), folderPath, newFolderName)
                            .doOnSuccess(merged -> {
                                requireParentActivity().refreshItems();
                                showToastFolderRenamed(newFolderName, merged);
                            })
                            .ignoreElement()
                            .subscribe(EMPTY_ACTION, this::onError);
                    requireParentActivity().addToDisposable(disposable);
                }
            } catch (IOException e) {
                onError(e);
            }
        };
    }

    protected void showToastFolderRenamed(String folderName, boolean merged) {
        int message;
        if (merged) {
            message = R.string.decks_list_folder_rename_dialog_toast_merged;
        } else {
            message = R.string.decks_list_folder_rename_dialog_toast_renamed;
        }
        runOnUiThread(
                () -> showShortToastMessage(String.format(getStringHelper(message), folderName)),
                this::onError
        );
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
    public MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }
}
