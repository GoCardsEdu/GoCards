package pl.softfly.flashcards.filesync.worker;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.io.OutputStream;

import pl.softfly.flashcards.filesync.algorithms.export.ExportExcelToDeck;

/**
 * This class separates the Android API from the algorithm.
 *
 * @author Grzegorz Ziemski
 */
public class ExportExcelWorker extends ExcelWorker {

    public ExportExcelWorker(
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
            fsDeckDb = getFsDeckDb(deckDbPath);
            readMetaData(fileUri);

            ExportExcelToDeck exportExcelToDeck = new ExportExcelToDeck(getApplicationContext());
            exportExcelToDeck.initFileSynced(
                    deckDbPath,
                    fileUri.toString(),
                    getInputData().getBoolean(AUTO_SYNC, false)
            );
            try (OutputStream outFile = openFileToWrite(fileUri)) {
                exportExcelToDeck.export(
                        deckDbPath,
                        outFile,
                        getFileMimeType()
                );
            }
            exportExcelToDeck.saveFileSynced();
            unlockDeckEditing(deckDbPath);
            showSuccessNotification();
            return Result.success();
        } catch (Exception e) {
            showExceptionDialog(e);
            return Result.failure();
        }
    }

    protected void showSuccessNotification() {
        (new Handler(Looper.getMainLooper())).post(() -> Toast.makeText(
                        getApplicationContext(),
                        String.format("The \"%s\" has been exported to the file.", getDeckName(deckDbPath)),
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    @NonNull
    protected String getDeckName(String dbPath) {
        return dbPath.substring(dbPath.lastIndexOf("/") + 1)
                .replace(".db", "");
    }
}