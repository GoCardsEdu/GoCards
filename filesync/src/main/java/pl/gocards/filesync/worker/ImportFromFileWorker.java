package pl.gocards.filesync.worker;

import static pl.gocards.filesync.sheet.WorkbookFactory.SUPPORTED_EXTENSIONS;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

import pl.gocards.App;
import pl.gocards.R;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.filesync.algorithms.import_file.ImportFileToDeck;
import pl.gocards.filesync.db.FileSyncDbUtil;
import pl.gocards.filesync.db.FileSyncDeckDatabase;
import pl.gocards.filesync.sheet.SheetWarningException;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.util.ExceptionHandler;
import pl.gocards.util.FirebaseAnalyticsHelper;
import pl.gocards.util.WarningException;

/**
 * This class separates the Android API from the algorithm.
 *
 * @author Grzegorz Ziemski
 */
public class ImportFromFileWorker extends FileWorker {

    private static final String TAG = "ImportFromFileWorker";
    public static final String IMPORT_TO_FOLDER_PATH = "IMPORT_TO_FOLDER_PATH";
    public static final String FILE_URI = "FILE_URI";
    public static final String AUTO_SYNC = "AUTO_SYNC";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized") // Initiated by extractInputData
    protected Uri fileUri;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized") // Initiated by extractInputData
    protected String importToFolderPath;

    public ImportFromFileWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String deckDbPath = null;
        try {
            extractInputData();
            askPermissions(fileUri);
            readFileMetadata(fileUri);

            try (InputStream isFile = openInputStream(fileUri)) {
                deckDbPath = (new ImportFileToDeck(getApplicationContext()))
                        .importFile(
                                importToFolderPath,
                                removeExtension(getFileDisplayName()),
                                isFile,
                                getFileMimeType(),
                                getFileModifiedAt()
                        );

                getFsFreshDeckDb(deckDbPath)
                        .fileSyncedDao()
                        .insert(prepareFileSynced());
            }

            showSuccessNotification();

            FirebaseAnalyticsHelper
                    .getInstance(getApplicationContext())
                    .importDeck();

            return Result.success();
        } catch (WarningException | SheetWarningException e) {
            showWarningDialog(e);
            return Result.failure();
        } catch (Exception e) {
            handleException(e);
            return Result.failure();
        } finally {
            if (deckDbPath != null)
                getFsDbUtil().closeDatabase(deckDbPath);
        }
    }

    protected void extractInputData() {
        Data inputData = getInputData();
        String fileUriString = inputData.getString(FILE_URI);
        fileUri = Objects.requireNonNull(Uri.parse(fileUriString));
        importToFolderPath = Objects.requireNonNull(inputData.getString(IMPORT_TO_FOLDER_PATH));
    }

    @NonNull
    protected FileSynced prepareFileSynced() {
        FileSynced fileSynced = new FileSynced();
        fileSynced.setDisplayName(getFileDisplayName());
        fileSynced.setUri(fileUri.toString());
        fileSynced.setAutoSync(getInputData().getBoolean(AUTO_SYNC, false));
        fileSynced.setLastSyncAt(getFileModifiedAt());
        return fileSynced;
    }

    @NonNull
    public String removeExtension(@NonNull String fileName) {
        for (String extension: SUPPORTED_EXTENSIONS) {
            extension = "." + extension;
            if (fileName.toLowerCase(Locale.getDefault()).endsWith(extension)) {
                return fileName.substring(0, fileName.length() - extension.length());
            }
        }
        return fileName;
    }

    protected void showSuccessNotification() {
        (new Handler(Looper.getMainLooper()))
                .post(() -> showLongToastMessage(String.format(getString(R.string.filesync_import_success_toast), getFileDisplayName())));
    }

    @UiThread
    protected void showLongToastMessage(CharSequence text) {
        Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_LONG
        ).show();
    }

    protected void showWarningDialog(@NonNull Exception e) {
        getExceptionHandler().showWarningDialog(
                e, ((App) getApplicationContext()).getActiveActivity(), TAG
        );
    }

    protected void handleException(@NonNull Exception e) {
        getExceptionHandler().handleException(
                e, ((App) getApplicationContext()).getActiveActivity(), TAG,
                null, true
        );
    }

    @NonNull
    protected FileSyncDbUtil getFsDbUtil() {
        return FileSyncDbUtil.getInstance(getApplicationContext());
    }

    @NonNull
    protected FileSyncDeckDatabase getFsFreshDeckDb(@NonNull String deckDbPath)
            throws DatabaseException {
        return getFsDbUtil().getFreshDatabase(getApplicationContext(), deckDbPath);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
