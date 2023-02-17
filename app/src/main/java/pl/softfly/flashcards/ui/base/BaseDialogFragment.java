package pl.softfly.flashcards.ui.base;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.reactivex.rxjava3.functions.Consumer;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.AppDatabaseUtil;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.AppDatabase;

/**
 * @author Grzegorz Ziemski
 */
public class BaseDialogFragment extends DialogFragment {

    protected void runOnUiThread(Runnable action, Consumer<? super Throwable> onError) {
        getExceptionHandler().tryRun(() -> runOnUiThread(() -> action.run()), onError);
    }

    protected void runOnUiThread(Runnable action) {
        requireActivity().runOnUiThread(action);
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @Nullable
    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(getContext())
                .getDatabase();
    }

    protected DeckDatabaseUtil getDeckDatabaseUtil() {
        return DeckDatabaseUtil.getInstance(getContext());
    }

}
