package pl.gocards.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pl.gocards.R;
import pl.gocards.util.Config;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionDialog extends DialogFragment {

    @Nullable
    private Throwable exception;

    @Nullable
    private String message;
    @Nullable
    private  DialogInterface.OnClickListener positiveListener;
    private boolean isWarning;

    @NotNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private AlertDialog alertDialog;

    private final boolean isRotated;

    public ExceptionDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public ExceptionDialog(Throwable e) {
        this(e, null, null, false);
    }

    public ExceptionDialog(Throwable e, DialogInterface.OnClickListener positiveListener) {
        this(e, null, positiveListener, false);
    }

    public ExceptionDialog(Throwable e, String message) {
        this(e, message, null, false);
    }

    public ExceptionDialog(
            @Nullable Throwable e,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        this(e, message, positiveListener, false);
    }

    public ExceptionDialog(
            @Nullable Throwable e,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener,
            boolean isWarning
    ) {
        this.exception = e;
        this.message = message;
        this.positiveListener = positiveListener;
        this.isWarning = isWarning;
        this.isRotated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
         AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(isWarning ? "Warning" : "Error")
                .setMessage(getMessage())
                .setPositiveButton(android.R.string.ok, positiveListener)
                .setIcon(android.R.drawable.ic_dialog_alert);

        if (exception != null && isShowExceptionEnabled()) {
            builder.setNegativeButton("Show Exception", (dialog, which) -> {});
        }
        alertDialog = builder.create();

        if (exception != null) {
            alertDialog.setOnShowListener(dialog -> {
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(v -> this.showException(v, exception));
            });
        }

        return alertDialog;
    }

    /**
     * The message cannot be empty,
     * otherwise android.R.id.message will not be created
     * and an exception will not be displayed.
     */
    @NotNull
    protected String getMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        } else if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            return exception.getMessage();
        } else {
            return "Unrecognized error.";
        }
    }

    protected boolean isShowExceptionEnabled() {
        return Config.getInstance(requireContext()).showExceptionInDialogException(requireContext());
    }

    @SuppressWarnings("unused")
    protected void showException(@NonNull View view, @NonNull Throwable throwable) {
        TextView messageTextView = getMessageView();
        messageTextView.setText(ExceptionUtils.getStackTrace(throwable));
        Button negativeButton = getButtonNegative();
        negativeButton.setText(R.string.exception_dialog_hide);
        negativeButton.setOnClickListener(this::hideException);
    }

    @SuppressWarnings("unused")
    protected void hideException(View view) {
        TextView messageTextView = getMessageView();
        messageTextView.setText(getMessage());
        if (exception != null) {
            Button negativeButton = getButtonNegative();
            negativeButton.setText(R.string.exception_dialog_show);
            negativeButton.setOnClickListener(v -> this.showException(v, exception));
        }
    }

    @NonNull
    protected TextView getMessageView() {
        return Objects.requireNonNull(alertDialog.findViewById(android.R.id.message));
    }

    @NonNull
    protected Button getButtonNegative() {
        return alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
    }

}
