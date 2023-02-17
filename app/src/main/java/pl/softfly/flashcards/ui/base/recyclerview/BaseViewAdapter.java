package pl.softfly.flashcards.ui.base.recyclerview;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.functions.Consumer;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.AppDatabaseUtil;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.AppDatabase;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.db.storage.StorageDb;

/**
 * @author Grzegorz Ziemski
 */
public abstract class BaseViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private final AppCompatActivity activity;

    public BaseViewAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    public void runOnUiThread(Runnable action, Consumer<? super Throwable> onError) {
        getExceptionHandler().tryRun(() -> getActivity().runOnUiThread(() -> action.run()), onError);
    }

    @Nullable
    protected DeckDatabase getDeckDatabase(String dbPath) throws DatabaseException {
        return DeckDatabaseUtil
                .getInstance(getApplicationContext())
                .getDatabase(dbPath);
    }

    @Nullable
    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(getApplicationContext())
                .getDatabase();
    }

    protected StorageDb<DeckDatabase> getStorageDb() {
        return DeckDatabaseUtil
                .getInstance(getApplicationContext())
                .getStorageDb();
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    protected Context getApplicationContext() {
        return activity.getApplicationContext();
    }
}
