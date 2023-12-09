package pl.gocards.filesync.worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.work.WorkerParameters;

import java.io.OutputStream;
import java.util.Objects;

import pl.gocards.R;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.filesync.sheet.SheetWarningException;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.filesync.algorithms.export.ExportDeckToFile;
import pl.gocards.filesync.db.FileSyncDeckDatabase;
import pl.gocards.util.FirebaseAnalyticsHelper;

/**
 * This class separates the Android API from the algorithm.
 *
 * @author Grzegorz Ziemski
 */
public class ExportToFileWorker extends FileSyncWorker {

    public static final String TAG = "ExportToFileWorker";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized") // Initiated by #doWork
    protected FileSyncDeckDatabase fsDeckDb;

    public ExportToFileWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            extractInputData();
            askPermissions(fileUri);
            Objects.nonNull(deckDbPath);
            fsDeckDb = getFsDeckFreshDb(deckDbPath);
            readFileMetadata(fileUri);

            FileSynced fileSynced  = fsDeckDb.fileSyncedDao().findOrCreate(
                    fileUri.toString(),
                    getFileDisplayName(),
                    getInputData().getBoolean(AUTO_SYNC, false)
            );

            ExportDeckToFile exportFileToDeck = new ExportDeckToFile(
                    getApplicationContext(),
                    deckDbPath,
                    fileSynced,
                    fsDeckDb
            );

            try (OutputStream outFile = openFileToWrite(fileUri)) {
                exportFileToDeck.export(
                        outFile,
                        getFileMimeType()
                );
            }

            exportFileToDeck.saveFileSynced();
            unlockDeckEditing(deckDbPath);
            showSuccessNotification();

            FirebaseAnalyticsHelper
                    .getInstance(getApplicationContext())
                    .exportDeck();

            return Result.success();
        } catch (SheetWarningException e) {
            showWarningDialog(e);
            return Result.failure();
        } catch (Exception e) {
            handleException(e);
            return Result.failure();
        } finally {
            getFsDbUtil().closeDatabase(deckDbPath);
        }
    }

    protected void showSuccessNotification() {
        (new Handler(Looper.getMainLooper()))
                .post(() -> showLongToastMessage(
                        String.format(getString(R.string.filesync_export_success_toast),
                                getDeckName(deckDbPath))
                ));
    }

    @UiThread
    protected void showLongToastMessage(CharSequence text) {
        Toast.makeText(
                getApplicationContext(),
                text,
                Toast.LENGTH_LONG
        ).show();
    }

    @NonNull
    protected String getDeckName(@NonNull String dbPath) {
        return AppDeckDbUtil.getDeckName(dbPath);
    }

    @Override
    protected String getTag() {
        return TAG;
    }
}