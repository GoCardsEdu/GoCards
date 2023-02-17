package pl.softfly.flashcards.ui.base;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.elevation.SurfaceColors;

import io.reactivex.rxjava3.functions.Consumer;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.AppDatabaseUtil;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.AppDatabase;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;

/**
 * @author Grzegorz Ziemski
 */
public class BaseActivity extends AppCompatActivity {

    public void runOnUiThread(Runnable action, Consumer<? super Throwable> onError) {
        getExceptionHandler().tryRun(() -> runOnUiThread(() -> action.run()), onError);
    }

    protected void setBarSameColours() {
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    @Nullable
    protected DeckDatabase getDeckDatabase(String dbPath) throws DatabaseException {
        return DeckDatabaseUtil
                .getInstance(getApplicationContext())
                .getDatabase(dbPath);
    }

    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(getApplicationContext())
                .getDatabase();
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
