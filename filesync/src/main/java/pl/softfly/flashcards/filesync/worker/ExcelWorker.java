package pl.softfly.flashcards.filesync.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.FlashCardsApp;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.filesync.db.FileSyncDatabaseUtil;
import pl.softfly.flashcards.filesync.db.FileSyncDeckDatabase;
import pl.softfly.flashcards.ui.ExceptionDialog;

/**
 * @author Grzegorz Ziemski
 */
public abstract class ExcelWorker extends AbstractFileWorker {

    public static final String DECK_DB_PATH = "DECK_DB_PATH";
    public static final String FILE_URI = "FILE_URI";
    public static final String AUTO_SYNC = "AUTO_SYNC";

    protected Uri fileUri;
    protected String deckDbPath;
    protected FileSyncDeckDatabase fsDeckDb;
    protected ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();

    public ExcelWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected void readInputData() {
        Data inputData = getInputData();
        deckDbPath = inputData.getString(DECK_DB_PATH);
        Objects.requireNonNull(deckDbPath);
        fileUri = Uri.parse(inputData.getString(FILE_URI));
        Objects.requireNonNull(fileUri);
    }

    protected void unlockDeckEditing(String deckDbPath) throws DatabaseException {
        // The db connector must be the same as in the UI app,
        // otherwise LiveData in the UI will not work.
        getDeckDb(deckDbPath).deckConfigDao().deleteByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT);
    }

    protected void showExceptionDialog(@NonNull Exception e) {
        exceptionHandler.handleException(
                e, ((FlashCardsApp) getApplicationContext()).getActiveActivity(),
                this.getClass().getSimpleName()
        );
    }

    protected void showExceptionDialog(@NonNull Exception e, String message) {
        exceptionHandler.handleException(
                e, ((FlashCardsApp) getApplicationContext()).getActiveActivity(),
                this.getClass().getSimpleName(),
                message
        );
    }

    protected void onlyShowExceptionDialog(@NonNull Exception e, String message) {
        exceptionHandler.onlyShowExceptionDialog(
                e, ((FlashCardsApp) getApplicationContext()).getActiveActivity(),
                this.getClass().getSimpleName(),
                message,
                null
        );
    }

    @Nullable
    protected DeckDatabase getDeckDb(@NonNull String deckName)
            throws DatabaseException {
        return DeckDatabaseUtil.getInstance(getApplicationContext()).getDatabase(deckName);
    }

    @Nullable
    protected FileSyncDeckDatabase getFsDeckDb(@NonNull String deckName)
            throws DatabaseException {
        return FileSyncDatabaseUtil.getInstance(getApplicationContext()).getDeckDatabase(deckName);
    }
}
