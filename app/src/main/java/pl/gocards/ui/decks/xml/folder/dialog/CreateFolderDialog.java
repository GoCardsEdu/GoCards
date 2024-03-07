package pl.gocards.ui.decks.xml.folder.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.nio.file.Path;

import pl.gocards.R;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.xml.MainActivity;

/**
 * F_C_03 Create a folder
 * @author Grzegorz Ziemski
 */
public class CreateFolderDialog extends BaseDialogFragment {

    private static final String TAG = "CreateFolderDialog";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Path currentFolder;

    private final boolean isRotated;

    public CreateFolderDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public CreateFolderDialog(@NonNull FragmentActivity activity, @NonNull Path currentFolder) {
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = requireActivity().getLayoutInflater().inflate(R.layout.dialog_create_folder, null);
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_folder_create_dialog_title)
                .setMessage(R.string.decks_list_folder_create_dialog_message)
                .setView(root)
                .setPositiveButton(R.string.ok, onClickOk(root))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .create();
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickOk(@NonNull View root) {
        return (dialog, which) -> getExceptionHandler().tryRun(() -> {
            EditText deckNameEditText = root.findViewById(R.id.folderName);
            String newFolderName = deckNameEditText.getText().toString();
            if (!newFolderName.isEmpty()) {
                File folder = new File(currentFolder + "/" + newFolderName);
                if (!folder.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    folder.mkdir();
                    requireParentActivity().refreshItems();
                    showToastFolderCreated(newFolderName);
                } else {
                    showToastFolderExists(newFolderName);
                }
            }
        }, this::onError);
    }

    @UiThread
    protected void showToastFolderCreated(String newFolderName) {
        showShortToastMessage(String.format(getStringHelper(R.string.decks_list_folder_create_dialog_toast_created), newFolderName));
    }

    @UiThread
    protected void showToastFolderExists(String newFolderName) {
        showShortToastMessage(String.format(getStringHelper(R.string.decks_list_folder_create_dialog_toast_exists), newFolderName));
    }

    @Override
    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(), TAG,
                "Error while creating new folder.");
    }

    @NonNull
    @Override
    protected MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }
}
