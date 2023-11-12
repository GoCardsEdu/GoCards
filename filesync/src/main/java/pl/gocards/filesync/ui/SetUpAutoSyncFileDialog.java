package pl.gocards.filesync.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.ui.base.BaseDialogFragment;

/**
 * @author Grzegorz Ziemski
 */
public class SetUpAutoSyncFileDialog extends BaseDialogFragment {

    protected FileSynced fileSynced;
    protected Runnable andThen;

    private final boolean isRotated;

    public SetUpAutoSyncFileDialog() {
        // Dismiss on screen rotation
        this.isRotated = true;
    }

    public SetUpAutoSyncFileDialog(
            FileSynced fileSynced,
            @NonNull AppCompatActivity activity,
            Runnable andThen
    ) {
        setParentActivity(activity);
        this.fileSynced = fileSynced;
        this.andThen = andThen;
        this.isRotated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }

    /**
     * 2. Ask if the deck should auto-sync with this file.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.filesyncpro_dialog_auto_sync_setup_title)
                .setMessage(R.string.filesyncpro_dialog_auto_sync_setup_message)
                .setPositiveButton(R.string.yes, onClickPositiveButton())
                .setNegativeButton(R.string.no, onClickNegativeButton())
                .create();
    }

    /**
     * 3. Set up the file to auto-sync in the future.
     */
    @NonNull
    protected DialogInterface.OnClickListener onClickPositiveButton() {
        return (dialog, which) -> {
            fileSynced.setAutoSync(true);
            showShortToastMessage(R.string.filesyncpro_dialog_auto_sync_setup_toast);
            andThen.run();
        };
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickNegativeButton() {
        return (dialog, which) -> andThen.run();
    }
}