package pl.gocards.filesync;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
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

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.filesync.ui.EditingDeckLockedDialog;
import pl.gocards.filesync.ui.SetUpAutoSyncFileDialog;
import pl.gocards.filesync.worker.FileSyncWorker;
import pl.gocards.filesync.worker.ExportToFileWorker;
import pl.gocards.filesync.worker.ImportFromFileWorker;
import pl.gocards.ui.main.MainActivity;
import pl.gocards.util.ExceptionHandler;
import pl.gocards.room.util.TimeUtil;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncBean {

    protected static final String TAG = "FileSync";

    public FileSyncBean() {}

    /**
     * FS_I Import the file as a new deck.
     */
    public void importFile(
            @NonNull AppCompatActivity activity,
            @NonNull String importToFolderPath,
            @NonNull Uri fileUri
    ) {
        FileSynced fileSynced = new FileSynced();
        fileSynced.setUri(fileUri.toString());
        showSetUpAutoSyncDialog(
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
                new OneTimeWorkRequest.Builder(ImportFromFileWorker.class)
                        .setInputData(createImportInputData(fileSynced, importToFolderPath))
                        .addTag(TAG)
                        .build();

        WorkManager workManager = WorkManager.getInstance(activity.getApplicationContext());
        workManager.enqueue(syncWorkRequest);
        workManager.getWorkInfoByIdLiveData(syncWorkRequest.getId())
                .observe(activity, workInfo -> refreshDeckList(activity, workInfo));
    }

    protected void refreshDeckList(
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
    protected Data createImportInputData(
            @NonNull FileSynced fileSynced,
            @NonNull String importToFolderPath
    ) {
        return new Data.Builder()
                .putString(ImportFromFileWorker.IMPORT_TO_FOLDER_PATH, importToFolderPath)
                .putString(ImportFromFileWorker.FILE_URI, fileSynced.getUri())
                .putBoolean(ImportFromFileWorker.AUTO_SYNC, fileSynced.getAutoSync())
                .build();
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    public void exportFile(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Uri fileUri,
            @NonNull CompositeDisposable activityDisposable
    ) {
        checkIfDeckEditingIsLocked(
                activity,
                deckDbPath,
                () -> {
                    FileSynced fileSynced = new FileSynced();
                    fileSynced.setUri(fileUri.toString());
                    showSetUpAutoSyncDialog(
                            activity,
                            fileSynced,
                            () -> lockDeckEditing(
                                    activity,
                                    deckDbPath,
                                    () -> exportWorkRequest(deckDbPath, fileSynced, activity.getApplicationContext()),
                                    activityDisposable
                            )
                    );
                },
                activityDisposable
        );
    }

    protected void exportWorkRequest(
            @NonNull String deckDbPath,
            @NonNull FileSynced fileSynced,
            @NonNull Context context
    ) {
        WorkRequest syncWorkRequest =
                new OneTimeWorkRequest.Builder(ExportToFileWorker.class)
                        .setInputData(createExportInputData(deckDbPath, fileSynced))
                        .addTag(TAG)
                        .build();
        WorkManager
                .getInstance(context)
                .enqueue(syncWorkRequest);
    }

    @NonNull
    protected Data createExportInputData(@NonNull String deckDbPath, @NonNull FileSynced fileSynced) {
        return new Data.Builder()
                .putString(FileSyncWorker.DECK_DB_PATH, deckDbPath)
                .putString(FileSyncWorker.FILE_URI, fileSynced.getUri())
                .putBoolean(FileSyncWorker.AUTO_SYNC, fileSynced.getAutoSync())
                .build();
    }

    /**
     * FS_PRO_S.1. Check that deck editing is not locked by another export/import/sync process.
     */
    @SuppressLint("CheckResult")
    protected void checkIfDeckEditingIsLocked(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Runnable andThen,
            @NonNull CompositeDisposable compositeDisposable
    ) {
        try {
            // The db connector must be the same as in the UI app,
            // otherwise LiveData in the UI will not work.
            Disposable disposable = getDeckDb(activity, deckDbPath)
                    .deckConfigRxDao()
                    .getLongByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                    .subscribeOn(Schedulers.io())
                    .doOnEvent((value, error) -> {
                        if (value == null && error == null) {
                            doOnSuccessCheckIfDeckEditingIsLocked(activity, null, andThen);
                        }
                    })
                    .doOnSuccess(blockedAt -> doOnSuccessCheckIfDeckEditingIsLocked(activity, blockedAt, andThen))
                    .ignoreElement()
                    .subscribe(EMPTY_ACTION, e -> onErrorCheckIfEditingIsLocked(activity, e));

            compositeDisposable.add(disposable);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doOnSuccessCheckIfDeckEditingIsLocked(
            @NonNull AppCompatActivity activity,
            @Nullable Long blockedAt,
            @NonNull Runnable andThen
    ) throws ExecutionException, InterruptedException {
        if (blockedAt == null || isNoWorkerWorking(activity)) {
            andThen.run();
        } else {
            showEditingDeckLockedDialog(activity);
        }
    }

    protected void showEditingDeckLockedDialog(@NonNull AppCompatActivity activity) {
        new EditingDeckLockedDialog().show(activity.getSupportFragmentManager(), "EditingDeckLocked");
    }

    protected boolean isNoWorkerWorking(@NonNull AppCompatActivity activity)
            throws ExecutionException, InterruptedException {
        WorkManager workManager = WorkManager.getInstance(activity.getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = workManager.getWorkInfosByTag(TAG);
        return statuses.get().isEmpty();
    }

    /**
     * FS_PRO_S.5. Show a dialog asking if the deck should be auto-synced.
     */
    protected void showSetUpAutoSyncDialog(
            @NonNull AppCompatActivity activity,
            @NonNull FileSynced fileSynced,
            @NonNull Runnable andThen
    ) {
        // Check the deck is auto-sync with this file.
        if (fileSynced.getAutoSync()) {
            andThen.run();
        } else {
            // Ask if the deck should auto-sync with this file.
            new SetUpAutoSyncFileDialog(fileSynced, activity, andThen)
                    .show(activity.getSupportFragmentManager(), "SetUpAutoSync");
        }
    }

    /**
     * FS_PRO_S.6. Lock the deck editing.
     */
    @SuppressLint("CheckResult")
    protected void lockDeckEditing(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Runnable andThen,
            @NonNull CompositeDisposable compositeDisposable
    ) {
        try {
            // The db connector must be the same as in the UI app,
            // otherwise LiveData in the UI will not work.
            DeckDatabase deckDb = getDeckDb(activity, deckDbPath);
            Disposable disposable = deckDb.deckConfigRxDao()
                    .getByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(deckConfig -> {
                        throw new RuntimeException(activity.getString(R.string.filesync_editing_deck_locked_dialog_title));
                    })
                    .doOnEvent((value, error) -> {
                        if (value == null && error == null) {
                            DeckConfig deckConfig = new DeckConfig(
                                    DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT,
                                    Long.toString(TimeUtil.getNowEpochSec())
                            );
                            deckDb.deckConfigDao().insert(deckConfig);
                            andThen.run();
                        }
                    })
                    .ignoreElement()
                    .subscribe(EMPTY_ACTION, e -> onErrorLockDeckEditing(activity, e));
            compositeDisposable.add(disposable);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onErrorCheckIfEditingIsLocked(
            @NonNull AppCompatActivity activity,
            @NonNull Throwable e
    ) {
        getExceptionHandler().handleException(
                e, activity, TAG, "Error while checking if deck editing is locked."
        );
    }

    protected void onErrorLockDeckEditing(
            @NonNull AppCompatActivity activity,
            @NonNull Throwable e
    ) {
        getExceptionHandler().handleException(
                e, activity, TAG, "Error while edit locking the deck."
        );
    }

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull Context context, @NonNull String dbPath)
            throws DatabaseException {
        return AppDeckDbUtil
                .getInstance(context)
                .getDatabase(context, dbPath);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
