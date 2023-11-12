package pl.gocards.filesync.worker;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import java.util.Objects;

import pl.gocards.util.ExceptionHandler;
import pl.gocards.App;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.filesync.db.FileSyncDbUtil;
import pl.gocards.filesync.db.FileSyncDeckDatabase;

/**
 * @author Grzegorz Ziemski
 */
public abstract class FileSyncWorker extends FileWorker {
    public static final String DECK_DB_PATH = "DECK_DB_PATH";
    public static final String FILE_URI = "FILE_URI";
    public static final String AUTO_SYNC = "AUTO_SYNC";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized") // Initiated by #extractInputData
    protected Uri fileUri;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized") // Initiated by #extractInputData
    protected String deckDbPath;

    @SuppressWarnings("unused")
    public FileSyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    protected void extractInputData() {
        Data inputData = getInputData();
        deckDbPath = Objects.requireNonNull(inputData.getString(DECK_DB_PATH));
        fileUri = Uri.parse(inputData.getString(FILE_URI));
    }

    /**
     * FS_PRO_S.20. Unlock the deck editing.
     */
    protected void unlockDeckEditing(@NonNull String deckDbPath) throws DatabaseException {
        // The db connector must be the same as in the UI app,
        // otherwise LiveData in the UI will not work.
        getDeckDb(deckDbPath).deckConfigDao().deleteByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT);
    }

    protected void showWarningDialog(@NonNull Exception exception) {
        getExceptionHandler().showWarningDialog(
                exception,
                ((App) getApplicationContext()).getActiveActivity(),
                getTag()
        );
    }

    protected void showErrorDialog(String message) {
        getExceptionHandler().showExceptionDialog(
                null,
                ((App) getApplicationContext()).getActiveActivity(),
                getTag(),
                message,
                null
        );
    }

    protected void handleException(@NonNull Exception e) {
        handleException(e, null);
    }

    @SuppressWarnings("SameParameterValue")
    protected void handleException(@NonNull Exception e, @StringRes int message) {
        handleException(e, getApplicationContext().getString(message));
    }

    protected void handleException(@NonNull Exception exception, String message) {
        getExceptionHandler().handleException(
                exception,
                ((App) getApplicationContext()).getActiveActivity(),
                getTag(), message, true
        );
    }

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull String deckName)
            throws DatabaseException {
        return AppDeckDbUtil.getInstance(getApplicationContext())
                .getDatabase(getApplicationContext(), deckName);
    }

    @NonNull
    protected FileSyncDeckDatabase getFsDeckFreshDb(@NonNull String deckName)
            throws DatabaseException {
        // Don't use the cache. Because perhaps the DB was removed and new DB created with this same name in the UI.
        return getFsDbUtil().getFreshDatabase(getApplicationContext(), deckName);
    }

    @NonNull
    protected FileSyncDbUtil getFsDbUtil() {
        return FileSyncDbUtil.getInstance(getApplicationContext());
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    protected abstract String getTag();
}