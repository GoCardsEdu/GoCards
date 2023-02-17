package pl.softfly.flashcards.filesync.worker;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import java.io.InputStream;
import java.util.Objects;

import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.FlashCardsApp;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.filesync.FileSynced;
import pl.softfly.flashcards.filesync.algorithms.importt.ImportExcelToDeck;
import pl.softfly.flashcards.filesync.db.FileSyncDatabaseUtil;
import pl.softfly.flashcards.filesync.db.FileSyncDeckDatabase;

/**
 * This class separates the Android API from the algorithm.
 *
 * @author Grzegorz Ziemski
 */
public class ImportExcelWorker extends AbstractFileWorker {

    public static final String IMPORT_TO_FOLDER_PATH = "IMPORT_TO_FOLDER_PATH";
    public static final String FILE_URI = "FILE_URI";
    public static final String AUTO_SYNC = "AUTO_SYNC";
    private final ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();
    protected Uri fileUri;
    protected String importToFolderPath;

    public ImportExcelWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            readInputData();
            askPermissions(fileUri);
            readMetaData(fileUri);

            try (InputStream isFile = openInputStream(fileUri)) {
                String deckPath = (new ImportExcelToDeck(getApplicationContext()))
                        .importExcelFile(
                                importToFolderPath,
                                getFileDisplayName(),
                                isFile,
                                getFileMimeType(),
                                getFileLastModifiedAt()
                        );
                getFsDeckDatabase(deckPath).fileSyncedDao().insert(prepareFileSynced());
            }

            showSuccessNotification();
            return Result.success();
        } catch (Exception e) {
            showExceptionDialog(e);
            return Result.failure();
        }
    }

    protected void readInputData() {
        Data inputData = getInputData();

        String fileUriString = inputData.getString(FILE_URI);
        Objects.requireNonNull(fileUriString);
        fileUri = Uri.parse(fileUriString);

        importToFolderPath = inputData.getString(IMPORT_TO_FOLDER_PATH);
        Objects.requireNonNull(importToFolderPath);
    }

    protected FileSynced prepareFileSynced() {
        FileSynced fileSynced = new FileSynced();
        fileSynced.setDisplayName(getFileDisplayName());
        fileSynced.setUri(fileUri.toString());
        fileSynced.setAutoSync(getInputData().getBoolean(AUTO_SYNC, false));
        fileSynced.setLastSyncAt(getFileLastModifiedAt());
        return fileSynced;
    }

    protected void showSuccessNotification() {
        (new Handler(Looper.getMainLooper())).post(() -> Toast.makeText(
                        getApplicationContext(),
                        String.format("The new deck \"%s\" has been imported from an Excel file.", getFileDisplayName()),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    protected void showExceptionDialog(@NonNull Exception e) {
        exceptionHandler.handleException(
                e, ((FlashCardsApp) getApplicationContext()).getActiveActivity(),
                this.getClass().getSimpleName()
        );
    }

    protected FileSyncDeckDatabase getFsDeckDatabase(@NonNull String deckDbPath)
            throws DatabaseException {
        return FileSyncDatabaseUtil.getInstance(getApplicationContext()).getDeckDatabase(deckDbPath);
    }
}
