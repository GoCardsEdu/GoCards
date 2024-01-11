package pl.gocards.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.db.app.AppDbUtil;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class BaseFragment extends Fragment {

    /**
     * Some async operations can be performed after closing the Fragment.
     * A copy of the reference is made when the fragment is created.
     * java.lang.IllegalStateException: Fragment not attached to an activity.
     */
    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private FragmentActivity activity;

    private final CompositeDisposable disposable = new CompositeDisposable();

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /* -----------------------------------------------------------------------------------------
     * DB
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected AppDeckDbUtil getDeckDbUtil() {
        return AppDeckDbUtil.getInstance(getApplicationContext());
    }

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull String dbPath) {
        try {
            return getDeckDbUtil().getDatabase(getApplicationContext(), dbPath);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    protected AppDatabase getAppDb() {
        return AppDbUtil
                .getInstance(getApplicationContext())
                .getDatabase(getApplicationContext());
    }

    /* -----------------------------------------------------------------------------------------
     * Others
     * ----------------------------------------------------------------------------------------- */

    protected void runOnUiThread(
            @NonNull Runnable action,
            @NonNull Consumer<? super Throwable> onError
    ) {
        runOnUiThread(() -> getExceptionHandler().tryRun(action, onError));
    }

    protected void runOnUiThread(@NonNull Runnable action) {
        requireActivity().runOnUiThread(action);
    }

    /** @noinspection SameParameterValue*/
    protected static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    protected static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @UiThread
    protected void showShortToastMessage(@StringRes int resId) {
        Toast.makeText(
                getApplicationContext(),
                getString(resId),
                Toast.LENGTH_SHORT
        ).show();
    }

    @UiThread
    protected void showShortToastMessage(CharSequence text) {
        Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_SHORT
        ).show();
    }

    /* -----------------------------------------------------------------------------------------
     * Get/sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    protected Context getApplicationContext() {
        return requireActivity().getApplicationContext();
    }

    @NonNull
    public FragmentActivity requireBackupActivity() {
        return activity;
    }

    public void setActivity(@NonNull FragmentActivity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean addToDisposable(@NonNull Disposable disposable) {
        return this.disposable.add(disposable);
    }

    @NonNull
    public CompositeDisposable getDisposable() {
        return disposable;
    }
}
