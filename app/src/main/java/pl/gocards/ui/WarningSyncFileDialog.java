package pl.gocards.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

public class WarningSyncFileDialog extends DialogFragment {

    protected Runnable andThen;

    private final boolean isRotated;

    public WarningSyncFileDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public WarningSyncFileDialog(
            AppCompatActivity activity,
            Runnable andThen
    ) {
        this.andThen = andThen;
        isRotated = false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.filesyncpro_dialog_warning_title)
                .setMessage(R.string.filesyncpro_dialog_warning_message)
                .setPositiveButton(R.string.ok, onClickPositiveButton())
                .setNegativeButton(R.string.cancel, onClickNegativeButton())
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickPositiveButton() {
        return (dialog, which) -> andThen.run();
    }

    @NonNull
    protected DialogInterface.OnClickListener onClickNegativeButton() {
        return (dialog, which) -> {};
    }

}
