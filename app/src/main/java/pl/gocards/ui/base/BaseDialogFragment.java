package pl.gocards.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.app.AppDbUtil;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class BaseDialogFragment extends DialogFragment {

    private static final String TAG = "BaseDialogFragment";

    /**
     * Some async operations are performed after closing the dialog.
     * A copy of the reference is made when the fragment is created.
     * java.lang.IllegalStateException: Fragment not attached to an activity.
     */
    @Nullable
    private FragmentActivity activity;


    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected String getStringHelper(@StringRes int resId) {
        return requireParentActivity().getString(resId);
    }

    protected void runOnUiThread(@NonNull Runnable action) {
        runOnUiThread(action, this::onError);
    }

    protected void runOnUiThread(@NonNull Runnable action, @NonNull Consumer<? super Throwable> onError) {
        requireParentActivity().runOnUiThread(() -> getExceptionHandler().tryRun(action, onError));
    }

    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(),
                TAG,
                "Error on the dialog"
        );
    }

    @UiThread
    protected void showShortToastMessage(CharSequence text) {
        Toast.makeText(
                // Fix for fragment not attached to a context.
                requireParentActivity(),
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

    @UiThread
    protected void showShortToastMessage(@StringRes int resId) {
        Toast.makeText(
                // Fix for fragment not attached to a context.
                requireParentActivity(),
                requireParentActivity().getString(resId),
                Toast.LENGTH_SHORT
        ).show();
    }

    /* -----------------------------------------------------------------------------------------
     * Db
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected AppDbUtil getAppDbUtil() {
        return AppDbUtil.getInstance(requireParentActivity());
    }

    @NonNull
    protected AppDatabase getAppDb() {
        return getAppDbUtil().getDatabase(requireParentActivity());
    }

    @NonNull
    protected AppDeckDbUtil getDeckDbUtil() {
        return AppDeckDbUtil.getInstance(requireParentActivity());
    }

    /* -----------------------------------------------------------------------------------------
     * Get/sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    @SuppressLint("UseRequireInsteadOfGet")
    protected FragmentActivity requireParentActivity() {
        return Objects.requireNonNull(activity);
    }

    protected Context getApplicationContext() {
        return requireParentActivity().getApplicationContext();
    }

    protected void setParentActivity(@NonNull FragmentActivity activity) {
        this.activity = activity;
    }
}
