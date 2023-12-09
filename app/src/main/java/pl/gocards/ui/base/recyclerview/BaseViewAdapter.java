package pl.gocards.ui.base.recyclerview;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
public abstract class BaseViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final AppCompatActivity activity;

    public BaseViewAdapter(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        this.applicationContext = activity.getApplicationContext();
    }

    protected void runOnUiThread(@NonNull Runnable action, @NonNull Consumer<? super Throwable> onError) {
        runOnUiThread(() -> getExceptionHandler().tryRun(action, onError));
    }

    protected void runOnUiThread(@NonNull Runnable action) {
        requireActivity().runOnUiThread(action);
    }

    @NonNull
    protected String getString(@StringRes int resId) {
        return applicationContext.getString(resId);
    }

    @UiThread
    protected void showShortToastMessage(@StringRes int resId) {
        Toast.makeText(
                getApplicationContext(),
                getString(resId),
                Toast.LENGTH_SHORT
        ).show();
    }

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull String dbPath) throws DatabaseException {
        return AppDeckDbUtil
                .getInstance(getApplicationContext())
                .getDatabase(getApplicationContext(), dbPath);
    }

    @NonNull
    protected AppDatabase getAppDatabase() {
        return AppDbUtil
                .getInstance(getApplicationContext())
                .getDatabase(getApplicationContext());
    }

    @NonNull
    protected AppDeckDbUtil getDeckDatabaseUtil() {
        return AppDeckDbUtil
                .getInstance(getApplicationContext());
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    public AppCompatActivity requireActivity() {
        return activity;
    }
    @NonNull
    public AppCompatActivity getActivity() {
        return activity;
    }

    @NonNull
    protected Context getApplicationContext() {
        return this.applicationContext;
    }
}
