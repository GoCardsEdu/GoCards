package pl.gocards.ui.base;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.db.app.AppDbMainThreadUtil;
import pl.gocards.db.app.AppDbUtil;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class BaseActivity extends AppCompatActivity {

    private final CompositeDisposable disposable = new CompositeDisposable();

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /* -----------------------------------------------------------------------------------------
     * UI
     * ----------------------------------------------------------------------------------------- */

    protected void setBarSameColoursAsToolbar() {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    protected void showBackArrow() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    /* -----------------------------------------------------------------------------------------
     * DB
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull String dbPath) {
        try {
            return AppDeckDbUtil
                    .getInstance(getApplicationContext())
                    .getDatabase(this, dbPath);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    protected AppDatabase getAppDb() {
        return AppDbUtil
                .getInstance(getApplicationContext())
                .getDatabase(getApplicationContext());
    }

    protected AppDbMainThreadUtil getAppDbMainThreadUtil() {
        return AppDbMainThreadUtil
                .getInstance(getApplicationContext());
    }

    protected AppDatabase getAppDbMainThread() {
        return getAppDbMainThreadUtil()
                .getDatabase(getApplicationContext());
    }

    /* -----------------------------------------------------------------------------------------
     * Others
     * ----------------------------------------------------------------------------------------- */

    public void runOnUiThread(
            @NonNull Runnable action,
            @NonNull Consumer<? super Throwable> onError
    ) {
        runOnUiThread(() -> getExceptionHandler().tryRun(action, onError));
    }

    /** @noinspection SameParameterValue*/
    @UiThread
    protected void showShortToastMessage(@StringRes int resId) {
        Toast.makeText(
                getApplicationContext(),
                getString(resId),
                Toast.LENGTH_SHORT
        ).show();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public boolean addToDisposable(@NonNull Disposable disposable) {
        return this.disposable.add(disposable);
    }
}
