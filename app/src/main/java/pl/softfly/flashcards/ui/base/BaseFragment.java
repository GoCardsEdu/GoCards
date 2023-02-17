package pl.softfly.flashcards.ui.base;

import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
public class BaseFragment extends Fragment {

    public void runOnUiThread(Runnable action, Consumer<? super Throwable> onError) {
        getExceptionHandler().tryRun(() -> requireActivity().runOnUiThread(() -> action.run()), onError);
    }

    protected static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Nullable
    protected DeckDatabase getDeckDatabase(String dbPath) throws DatabaseException {
        return DeckDatabaseUtil
                .getInstance(getContext())
                .getDatabase(dbPath);
    }

    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(getContext())
                .getDatabase();
    }

    protected DeckDatabaseUtil getDeckDatabaseUtil() {
        return DeckDatabaseUtil.getInstance(getContext());
    }

    protected StorageDb<DeckDatabase> getStorageDb() {
        return getDeckDatabaseUtil().getStorageDb();
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
