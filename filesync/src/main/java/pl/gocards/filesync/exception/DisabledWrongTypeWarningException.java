package pl.gocards.filesync.exception;

import android.content.Context;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.util.WarningException;

/**
 * @author Grzegorz Ziemski
 */
public class DisabledWrongTypeWarningException extends WarningException {

    public DisabledWrongTypeWarningException(@NonNull Context context, int rowNum, Throwable cause) {
        super(String.format(context.getString(R.string.filesyncpro_warning_disabled_wrong_type), rowNum), cause);
    }
}
