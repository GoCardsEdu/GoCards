package pl.gocards.filesync.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

/**
 * 1.1 If Yes, display a message that deck editing is blocked and end use case.
 * @author Grzegorz Ziemski
 */
public class EditingDeckLockedDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.filesync_editing_deck_locked_dialog_title)
                .setMessage(R.string.filesync_editing_deck_locked_dialog_message)
                .setPositiveButton(R.string.ok, null)
                .create();
    }
}
