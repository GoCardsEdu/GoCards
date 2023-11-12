package pl.gocards.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.ui.ExceptionDialog;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings({"unused", "JavadocReference"})
public class ExceptionHandler {

    private static ExceptionHandler INSTANCE;

    @NonNull
    private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    @NonNull
    public static synchronized ExceptionHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExceptionHandler();
        }
        return INSTANCE;
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable AppCompatActivity activity,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        tryRun(r, activity, null, null, positiveListener);
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable FragmentActivity activity,
            @Nullable String message
    ) {
        String tag = null;
        if (activity != null) {
            tag = activity.getClass().getSimpleName();
        }
        tryRun(r, activity, tag, message, null);
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable AppCompatActivity activity,
            @Nullable String message
    ) {
        tryRun(r, activity, null, message, null);
    }


    public void tryRun(
            @NonNull Runnable r,
            @Nullable FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message
    ) {
        tryRun(r, activity, tag, message, null);
    }


    public void tryRun(
            @NonNull Runnable r,
            @Nullable AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message
    ) {
        tryRun(r, activity, tag, message, null);
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable FragmentActivity activity,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        tryRun(r, activity, null, message, positiveListener);
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable AppCompatActivity activity,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        tryRun(r, activity, null, message, positiveListener);
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        try {
            r.run();
        } catch (Exception e) {
            handleException(e, activity, tag, message, positiveListener);
        }
    }

    public void tryRun(
            @NonNull Runnable r,
            @Nullable AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        try {
            r.run();
        } catch (Exception e) {
            handleException(e, activity, tag, message, positiveListener);
        }
    }

    public void tryRun(
            @NonNull Runnable r,
            @NonNull Consumer<? super Throwable> onError
    ) {
        try {
            r.run();
        } catch (Exception e) {
            try {
                onError.accept(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable FragmentActivity activity
    ) {
        handleException(e, activity, null, null, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity
    ) {
        handleException(e, activity, null, null, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable FragmentActivity activity,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        handleException(e, activity, null, message, positiveListener);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        handleException(e, activity, null, message, positiveListener);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        handleException(e, activity, null, null, positiveListener);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable FragmentActivity activity,
            @Nullable String message
    ) {
        handleException(e, activity, null, message, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity,
            @Nullable String message
    ) {
        handleException(e, activity, null, message, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message
    ) {
        handleException(e, activity, tag, message, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message,
            boolean forceDisplay
    ) {
        handleException(e, activity, tag, message, null);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        if (activity == null) {
            throw new RuntimeException("There is no attached activity where an error dialog may be displayed.", e);
        }
        if (tag == null) {
            tag = activity.getClass().getSimpleName();
        }

        e.printStackTrace();
        saveException(activity, e, tag, message);
        showExceptionDialog(e, activity, tag, message, positiveListener);
    }

    public void handleException(
            @NonNull Throwable e,
            @Nullable AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        if (activity == null) {
            throw new RuntimeException("There is no attached activity where an error dialog may be displayed.", e);
        }
        if (tag == null) {
            tag = activity.getClass().getSimpleName();
        }

        e.printStackTrace();
        if (!(e instanceof WarningException)) {
            saveException(activity, e, tag, message);
        }
        showExceptionDialog(e, activity, tag, message, positiveListener);
    }

    protected void showExceptionDialog(
            @Nullable Throwable e,
            @NonNull AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        showExceptionDialog(
                e,
                activity.getLifecycle(),
                activity.getSupportFragmentManager(),
                activity.getApplicationContext(),
                tag,
                message,
                positiveListener,
                false
        );
    }

    public void showExceptionDialog(
            @Nullable Throwable e,
            @NonNull FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        showExceptionDialog(
                e,
                activity.getLifecycle(),
                activity.getSupportFragmentManager(),
                activity.getApplicationContext(),
                tag,
                message,
                positiveListener,
                false
        );
    }
    protected void showWarningDialog(
            @Nullable Throwable e,
            @NonNull AppCompatActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        showExceptionDialog(
                e,
                activity.getLifecycle(),
                activity.getSupportFragmentManager(),
                activity.getApplicationContext(),
                tag,
                message,
                positiveListener,
                true
        );
    }

    public void showWarningDialog(
            @NonNull Throwable e,
            @NonNull FragmentActivity activity,
            @Nullable String tag
    ) {
        showWarningDialog(
                e,
                activity.getLifecycle(),
                activity.getSupportFragmentManager(),
                activity.getApplicationContext(),
                tag,
                e.getMessage(),
                null
        );
    }

    public void showWarningDialog(
            @Nullable Throwable e,
            @NonNull FragmentActivity activity,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        showExceptionDialog(
                e,
                activity.getLifecycle(),
                activity.getSupportFragmentManager(),
                activity.getApplicationContext(),
                tag,
                message,
                positiveListener,
                true
        );
    }

    protected void showExceptionDialog(
            @Nullable Throwable e,
            @NonNull Lifecycle lifecycle,
            @NonNull FragmentManager fragmentManager,
            @NotNull Context context,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener,
            boolean isWarning
    ) {
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            if (e != null) e.printStackTrace();
            ExceptionDialog dialog = new ExceptionDialog(e, message, positiveListener, isWarning);
            dialog.show(fragmentManager, tag);
        } else {
            throw new RuntimeException(
                    String.format(
                            "There is no attached activity where an error dialog may be displayed. [lifecycle=%s]",
                            lifecycle.getCurrentState()
                    ), e);
        }
    }

    /** @noinspection SameParameterValue*/
    protected void showWarningDialog(
            @Nullable Throwable e,
            @NonNull Lifecycle lifecycle,
            @NonNull FragmentManager fragmentManager,
            @NotNull Context context,
            @Nullable String tag,
            @Nullable String message,
            @Nullable DialogInterface.OnClickListener positiveListener
    ) {
        if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            if (e != null) e.printStackTrace();
            ExceptionDialog dialog = new ExceptionDialog(e, message, positiveListener, true);
            dialog.show(fragmentManager, tag);
        } else {
            throw new RuntimeException(
                    String.format(
                            "There is no attached activity where an error dialog may be displayed. [lifecycle=%s]",
                            lifecycle.getCurrentState()
                    ), e);
        }
    }

    public void saveException(@NonNull Context context, @NonNull Throwable e) {
        saveException(context, e, null, null);
    }

    public void saveException(
            @NonNull Context context,
            @NotNull Throwable e,
            @Nullable String tag,
            @Nullable String message
    ) {
        if (Config.getInstance().isCrashlyticsEnabled(context)) {
            //noinspection DataFlowIssue
            crashlytics.setCustomKey("tag", tag);
            //noinspection DataFlowIssue
            crashlytics.setCustomKey("message", message);
            crashlytics.recordException(e);
        }
    }

    public void saveException(
            @NonNull Context context,
            @NotNull Throwable e,
            @Nullable String tag
    ) {
        if (Config.getInstance().isCrashlyticsEnabled(context)) {
            //noinspection DataFlowIssue
            crashlytics.setCustomKey("tag", tag);
            crashlytics.recordException(e);
        }
    }

    /**
     * Must be the last one in {@link AppCompatActivity#onCreate}.
     * Because Crashlytics sends logs with delay.
     * If the application crashes at the very beginning on opening, the logs will never be sent.
     * We will never know that the user cannot even open the app.
     * If the app crashes before this is set it will send a crash error.
     */
    public void setUncaughtExceptionHandler(@NonNull AppCompatActivity activity) {
        Thread.setDefaultUncaughtExceptionHandler((paramThread, throwable) -> {
            saveException(activity, throwable);
            crashlytics.sendUnsentReports(); // It doesn't work here
            new Thread(() -> {
                Looper.prepare();
                Toast.makeText(activity, "Fatal error: " + throwable, Toast.LENGTH_LONG).show();
                activity.finish(); // otherwise the app does not close
                Looper.loop();
            }).start();
            throw new RuntimeException(throwable);
        });
    }
}