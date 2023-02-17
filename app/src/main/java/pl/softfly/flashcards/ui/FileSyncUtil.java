package pl.softfly.flashcards.ui;

import static pl.softfly.flashcards.filesync.FileSync.TYPE_XLS;
import static pl.softfly.flashcards.filesync.FileSync.TYPE_XLSX;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.filesync.FileSync;
import pl.softfly.flashcards.filesync.FileSyncPro;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncUtil {

    protected final FileSync fileSync = FileSync.getInstance();

    protected final FileSyncPro fileSyncPro = FileSyncPro.getInstance();

    protected String path;

    protected AppCompatActivity activity;

    protected ActivityResultLauncher<String[]> syncExcelLauncher;

    protected ActivityResultLauncher<String> exportExcelLauncher;

    protected ActivityResultLauncher<String[]> importExcelLauncher;

    public FileSyncUtil(AppCompatActivity activity) {
        this.activity = activity;
        syncExcelLauncher = initSyncExcelLauncher();
        exportExcelLauncher = initExportExcelLauncher();
        importExcelLauncher = initImportExcelLauncher();
    }

    protected ActivityResultLauncher<String[]> initSyncExcelLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                syncedExcelUri -> {
                    if (syncedExcelUri != null) {
                        try {
                            fileSyncPro.syncFile(activity, path, syncedExcelUri);
                        } catch (DatabaseException e) {
                            onErrorSyncExcel(e);
                        }
                    }
                }
        );
    }

    protected void onErrorSyncExcel(Throwable e) {
        getExceptionHandler().handleException(
                e,
                activity.getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while sync to Excel."
        );
    }

    protected ActivityResultLauncher<String> initExportExcelLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.CreateDocument() {
                    @NonNull
                    @Override
                    public Intent createIntent(@NonNull Context context, @NonNull String input) {
                        return super.createIntent(context, input)
                                .setType(TYPE_XLSX);
                    }
                },
                exportedExcelUri -> {
                    if (exportedExcelUri != null) {
                        try {
                            fileSync.exportFile(activity, path, exportedExcelUri);
                        } catch (DatabaseException e) {
                            onErrorExportExcel(e);
                        }
                    }
                }
        );
    }

    protected void onErrorExportExcel(Throwable e) {
        getExceptionHandler().handleException(
                e,
                activity.getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while exporting to Excel."
        );
    }

    protected ActivityResultLauncher<String[]> initImportExcelLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                importedExcelUri -> {
                    if (importedExcelUri != null)
                        fileSync.importFile(activity, path, importedExcelUri);
                }
        );
    }

    public void launchSyncFile(String deckDbPath) {
        this.path = deckDbPath;
        syncExcelLauncher.launch(new String[]{TYPE_XLS, TYPE_XLSX});
    }

    public void launchExportToFile(String deckDbPath) {
        this.path = deckDbPath;
        exportExcelLauncher.launch(getDeckName(deckDbPath) + ".xlsx");
    }

    public void launchImportExcel(String importToFolder) {
        this.path = importToFolder;
        importExcelLauncher.launch(new String[]{TYPE_XLS, TYPE_XLSX});
    }

    public void autoSync(String deckDbPath, long afterTime) throws DatabaseException {
        fileSyncPro.autoSync(activity, deckDbPath, afterTime);
    }

    protected String getDeckName(String dbPath) {
        return dbPath.substring(dbPath.lastIndexOf("/") + 1)
                .replace(".db", "");
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}