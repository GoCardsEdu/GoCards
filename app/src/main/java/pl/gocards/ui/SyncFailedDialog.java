package pl.gocards.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

/**
 * FS_PRO_S.2. If synchronization failed last time, show the Resume Synchronization dialog.
 */
public class SyncFailedDialog extends DialogFragment {

    protected Runnable onClickNewSynchronization;

    protected Runnable onClickRetryFailedSynchronization;

    protected Runnable onClickSaveBackupFile;

    private final boolean isRotated;

    public SyncFailedDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public SyncFailedDialog(
            Runnable onClickNewSynchronization,
            Runnable onClickRetryFailedSynchronization,
            Runnable onClickSaveBackupFile
    ) {
        this.onClickNewSynchronization = onClickNewSynchronization;
        this.onClickRetryFailedSynchronization = onClickRetryFailedSynchronization;
        this.onClickSaveBackupFile = onClickSaveBackupFile;
        this.isRotated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.filesyncpro_dialog_sync_failed_title)
                .setPositiveButton("Start new synchronization", onClickNewSynchronization())
                .setNegativeButton("Resume previous synchronization", onClickOnClickRetryFailedSynchronization())
                .create();
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Save backup file to disk", onClickSaveBackupFile());
        return dialog;
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickNewSynchronization() {
        return (dialog, which) -> onClickNewSynchronization.run();
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickOnClickRetryFailedSynchronization() {
        return (dialog, which) -> onClickRetryFailedSynchronization.run();
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickSaveBackupFile() {
        return (dialog, which) -> onClickSaveBackupFile.run();
    }
}
