package pl.softfly.flashcards.filesync;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.TimeUtil;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.entity.filesync.FileSynced;
import pl.softfly.flashcards.filesync.db.FileSyncDatabaseUtil;
import pl.softfly.flashcards.filesync.db.FileSyncDeckDatabase;
import pl.softfly.flashcards.filesync.ui.EditingDeckLockedDialog;
import pl.softfly.flashcards.filesync.ui.SetUpAutoSyncFileDialog;
import pl.softfly.flashcards.filesync.worker.ExcelWorker;
import pl.softfly.flashcards.filesync.worker.ExportExcelWorker;
import pl.softfly.flashcards.filesync.worker.ImportExcelWorker;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * 1. Check that the deck is not being edited by another task.
 * 1.1 If Yes, display a message that deck editing is blocked and end use case.
 * 2. Check the deck is auto-sync with this file.
 * If Yes, skip steps 3,4.
 * 3. Ask if the deck should auto-sync with this file.
 * If No, skip step 4.
 * 4. Set up the file to auto-sync in the future.
 *
 * @author Grzegorz Ziemski
 */
public class FileSyncBean implements FileSync {

    private final ExceptionHandler exceptionHandler = ExceptionHandler.getInstance();

    @Nullable
    private FileSyncDeckDatabase fsDeckDb;

    @Nullable
    private DeckDatabase deckDb;

    protected boolean isWorkSucceeded(@NonNull WorkInfo workInfo) {
        return workInfo.getState() == WorkInfo.State.SUCCEEDED;
    }

    protected boolean isActivityResumed(@NonNull AppCompatActivity activity) {
        return activity
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.RESUMED);
    }

    @NonNull
    protected Data createInputData(@NonNull String deckDbPath, @NonNull FileSynced fileSynced) {
        return new Data.Builder()
                .putString(ExcelWorker.DECK_DB_PATH, deckDbPath)
                .putString(ExcelWorker.FILE_URI, fileSynced.getUri())
                .putBoolean(ExcelWorker.AUTO_SYNC, fileSynced.isAutoSync())
                .build();
    }

    /**
     * IE Create a deck from an imported Excel file.
     */
    @Override
    public void importFile(
            @NonNull AppCompatActivity activity,
            @NonNull String importToFolderPath,
            @NonNull Uri fileUri
    ) {
        FileSynced fileSynced = new FileSynced();
        fileSynced.setUri(fileUri.toString());
        setUpAutoSync(
                activity,
                fileSynced,
                () -> importWorkRequest(activity, fileSynced, importToFolderPath)
        );
    }

    protected void importWorkRequest(
            @NonNull AppCompatActivity activity,
            @NonNull FileSynced fileSynced,
            @NonNull String importToFolderPath
    ) {
        WorkRequest syncWorkRequest =
                new OneTimeWorkRequest.Builder(ImportExcelWorker.class)
                        .setInputData(createImportInputData(fileSynced, importToFolderPath))
                        .addTag(TAG)
                        .build();

        WorkManager workManager = WorkManager.getInstance(activity.getApplicationContext());
        workManager.enqueue(syncWorkRequest);
        workManager.getWorkInfoByIdLiveData(syncWorkRequest.getId())
                .observe(activity, workInfo -> doOnSuccessImportWork(activity, workInfo));
    }

    protected void doOnSuccessImportWork(
            @NonNull AppCompatActivity activity,
            @NonNull WorkInfo workInfo
    ) {
        if (isWorkSucceeded(workInfo)
                && isActivityResumed(activity)
                && activity instanceof MainActivity
        ) {
            ((MainActivity) activity).refreshItems();
        }
    }

    @NonNull
    protected Data createImportInputData(
            @NonNull FileSynced fileSynced,
            @NonNull String importToFolderPath
    ) {
        return new Data.Builder()
                .putString(ImportExcelWorker.IMPORT_TO_FOLDER_PATH, importToFolderPath)
                .putString(ImportExcelWorker.FILE_URI, fileSynced.getUri())
                .putBoolean(ImportExcelWorker.AUTO_SYNC, fileSynced.isAutoSync())
                .build();
    }

    /**
     * EE Create a new Excel file from the exported deck.
     */
    @Override
    public void exportFile(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Uri fileUri
    ) throws DatabaseException {
        fsDeckDb = FileSyncDatabaseUtil
                .getInstance(activity.getApplicationContext())
                .getDeckDatabase(deckDbPath);

        checkIfEditingIsLocked(activity, () -> {
            FileSynced fileSynced = new FileSynced();
            fileSynced.setUri(fileUri.toString());
            setUpAutoSync(
                    activity,
                    fileSynced,
                    () -> {
                        try {
                            lockDeckEditing(activity, deckDbPath);
                        } catch (DatabaseException e) {
                            throw new RuntimeException(e);
                        }
                        exportWorkRequest(deckDbPath, fileSynced, activity.getApplicationContext());
                    }
            );
        });
    }

    protected void exportWorkRequest(
            @NonNull String deckDbPath,
            @NonNull FileSynced fileSynced,
            @NonNull Context context
    ) {
        WorkRequest syncWorkRequest =
                new OneTimeWorkRequest.Builder(ExportExcelWorker.class)
                        .setInputData(createInputData(deckDbPath, fileSynced))
                        .addTag(TAG)
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(syncWorkRequest);
    }

    /**
     * 1. Check that the deck is not being edited by another task.
     */
    protected void checkIfEditingIsLocked(@NonNull AppCompatActivity activity, @NonNull Runnable andThen) {
        fsDeckDb.deckConfigAsyncDao().getLongByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(blockedAt -> doOnSuccessCheckIfEditingIsLocked(activity, blockedAt, andThen))
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        doOnSuccessCheckIfEditingIsLocked(activity, null, andThen);
                    }
                })
                .subscribe(blockedAt -> {}, e -> onErrorCheckIfEditingIsLocked(activity, e));
    }

    protected void doOnSuccessCheckIfEditingIsLocked(
            @NonNull AppCompatActivity activity,
            @NonNull Long blockedAt,
            @NonNull Runnable andThen
    )
            throws ExecutionException, InterruptedException {
        if (blockedAt == null || isAnyWorkerWorking(activity)) {
            andThen.run();
        } else {
            // 1.1 If Yes, display a message that deck editing is blocked and end use case.
            showEditingDeckLockedDialog(activity);
        }
    }

    protected void showEditingDeckLockedDialog(@NonNull AppCompatActivity activity) {
        new EditingDeckLockedDialog().show(activity.getSupportFragmentManager(), "EditingDeckLocked");
    }

    protected boolean isAnyWorkerWorking(@NonNull AppCompatActivity activity)
            throws ExecutionException, InterruptedException {
        WorkManager workManager = WorkManager.getInstance(activity.getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = workManager.getWorkInfosByTag(FileSync.TAG);
        return statuses.get().isEmpty();
    }

    protected void onErrorCheckIfEditingIsLocked(@NonNull AppCompatActivity activity, @NonNull Throwable e) {
        exceptionHandler.handleException(
                e, activity.getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while exporting or syncing cards.",
                (dialog, which) -> activity.onBackPressed()
        );
    }

    protected void lockDeckEditing(@NonNull AppCompatActivity activity, @NonNull String deckDbPath)
            throws DatabaseException {
        // The db connector must be the same as in the UI app,
        // otherwise LiveData in the UI will not work.
        deckDb = DeckDatabaseUtil
                .getInstance(activity.getApplicationContext())
                .getDatabase(deckDbPath);

        deckDb.deckConfigAsyncDao()
                .getByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(deckConfig -> {
                    deckConfig.setValue(Long.toString(TimeUtil.getNowEpochSec()));
                    deckDb.deckConfigAsyncDao().update(deckConfig).subscribe();
                })
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        DeckConfig deckConfig = new DeckConfig();
                        deckConfig.setKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT);
                        deckConfig.setValue(Long.toString(TimeUtil.getNowEpochSec()));
                        deckDb.deckConfigAsyncDao().insert(deckConfig).subscribe();
                    }
                })
                .subscribe(deckConfig -> {}, e -> onErrorLockDeckEditing(activity, e));
    }

    protected void onErrorLockDeckEditing(@NonNull AppCompatActivity activity, @NonNull Throwable e) {
        exceptionHandler.handleException(
                e, activity.getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while edit blocking the deck.",
                (dialog, which) -> activity.onBackPressed()
        );
    }

    /**
     * Shows the auto-sync setup dialog.
     */
    protected void setUpAutoSync(
            @NonNull AppCompatActivity activity,
            @NonNull FileSynced fileSynced,
            @NonNull Runnable andThen
    ) {
        // 2. Check the deck is auto-sync with this file.
        if (fileSynced.isAutoSync()) {
            andThen.run();
        } else {
            // 3. Ask if the deck should auto-sync with this file.
            new SetUpAutoSyncFileDialog(fileSynced, activity, andThen)
                    .show(activity.getSupportFragmentManager(), "SetUpAutoSync");
        }
    }
}
