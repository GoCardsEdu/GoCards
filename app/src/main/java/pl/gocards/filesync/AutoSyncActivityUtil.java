package pl.gocards.filesync;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.App;
import pl.gocards.R;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.cards.list.file_sync.FileSyncListCardsActivity;
import pl.gocards.ui.cards.study.file_sync.FileSyncStudyCardActivity;
import pl.gocards.util.ExceptionHandler;

/**
 * Decorator with auto sync feature for Activities.
 * FS_PRO_S.6. Lock the deck editing.
 * @author Grzegorz Ziemski
 */
public class AutoSyncActivityUtil {
    private static final int SECONDS_5 = 5000;
    private final CompositeDisposable disposable;
    private final AppCompatActivity activity;
    @NotNull
    private final DeckDatabase deckDb;
    @Nullable
    private final FileSyncProLauncher fileSyncProLauncher;
    private final boolean autoSyncEnabled = true;
    private final boolean editingLocked = false;
    private boolean startAutoSyncDone = false;
    @NonNull
    private final String deckDbPath;
    private final AutoSyncListener autoSyncListener;

    private AutoSyncActivityUtil(
            @NotNull AppCompatActivity activity,
            @NotNull AutoSyncListener autoSyncListener,
            @NonNull String deckDbPath,
            @NotNull CompositeDisposable disposable
    ) {
        this.activity = activity;
        this.deckDbPath = deckDbPath;
        this.deckDb = getDeckDb(deckDbPath);
        this.disposable = disposable;
        this.autoSyncListener = autoSyncListener;
        this.fileSyncProLauncher = FileSyncProLauncher.getInstance(
                activity,
                autoSyncListener::onSyncSuccess,
                disposable
        );
    }

    public AutoSyncActivityUtil(FileSyncListCardsActivity activity) {
        this(activity, activity, activity.getDeckDbPath(), activity.getDisposable());
    }

    public AutoSyncActivityUtil(FileSyncStudyCardActivity activity) {
        this(activity, activity, activity.getDeckDbPath(), activity.getDisposable());
    }

    public void autoSyncOnCreate() {
        autoSync(disposable);
    }

    /**
     * Don't auto-sync when the database is broken.
     */
    public void autoSyncOnDestroy() {
        if (startAutoSyncDone) {
            autoSync(((App) activity.getApplicationContext()).getDisposable());
        }
    }

    private void autoSync(@NonNull CompositeDisposable parentDisposable) {
        Objects.requireNonNull(fileSyncProLauncher);
        Objects.requireNonNull(deckDb);

        Disposable disposable = deckDb.deckConfigRxDao()
                .getByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .subscribeOn(Schedulers.io())
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        if (autoSyncEnabled) {
                            fileSyncProLauncher.autoSync(
                                    getDeckDbPath(),
                                    parentDisposable,
                                    autoSyncListener::onSyncSuccess,
                                    0
                            );
                        }
                    }
                    runOnUiThread(() -> startAutoSyncDone = true);
                })
                .doOnSuccess(deckConfig -> runOnUiThread(
                        () -> {
                            startAutoSyncDone = true;
                            if (autoSyncEnabled)
                                showShortToastMessage(R.string.filesync_still_in_progress);
                        },
                        this::onAutoSyncError
                ))
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onAutoSyncError);

        parentDisposable.add(disposable);
    }

    /* -----------------------------------------------------------------------------------------
     * Lock / Unlock
     * ----------------------------------------------------------------------------------------- */

    /**
     * TODO Replace and don't use LiveData
     * LiveData has no callback for errors
     * Workaround: Use after checking the room is working properly
     */
    public void observeEditingBlockedAt() {
        runOnUiThread(() -> deckDb.deckConfigLiveDataDao()
                .findByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .observe(activity, deckConfig -> getExceptionHandler().tryRun(() -> {
                    if (deckConfig != null) {
                        autoSyncListener.lockEditing();
                        checkIfEditingIsLocked();
                    } else {
                        autoSyncListener.unlockEditing();
                    }
                }, activity, "Error while unlocking / locking editing.")));
    }

    /**
     * Unlock deck editing if any worker is working.
     * In a properly running application this code should never be executed.
     * That's just in case a worker fails and doesn't unlock the edit deck.
     */
    private void checkIfEditingIsLocked() {
        Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));
        handler.post(new Runnable() {
            public void run() {
                checkIfEditingIsLockedRunnable(handler, this);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void checkIfEditingIsLockedRunnable(
            @NonNull Handler handler,
            Runnable runnable
    ) {
        Disposable disposable = deckDb.deckConfigRxDao()
                .getByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(deckConfig -> {
                    if (isFileSyncWorkersRuns()) {
                        deckDb.deckConfigDao().deleteByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT);
                        if (editingLocked) {
                            autoSyncListener.unlockEditing();
                            sendCrashlyticsLogUnlock();
                        }
                    } else if (!editingLocked) {
                        autoSyncListener.lockEditing();
                    } else {
                        handler.postDelayed(runnable, SECONDS_5);
                    }
                })
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        if (editingLocked) {
                            autoSyncListener.unlockEditing();
                        }
                    }
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorUnlockEditing);
        addToDisposable(disposable);
    }

    private boolean isFileSyncWorkersRuns() throws ExecutionException, InterruptedException {
        WorkManager workManager = WorkManager.getInstance(activity.getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = workManager.getWorkInfos(
                WorkQuery.Builder
                        .fromTags(Collections.singletonList(FileSyncLauncher.TAG))
                        .addStates(Arrays.asList(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING))
                        .build()
        );
        return statuses.get().isEmpty();
    }

    private void sendCrashlyticsLogUnlock() {
        getExceptionHandler().saveException(
                activity,
                new RuntimeException("Emergency editing unlock, the worker has failed miserably."),
                FileSyncListCardsActivity.class.getSimpleName()
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    protected void runOnUiThread(Runnable action) {
        activity.runOnUiThread(action);
    }

    protected void runOnUiThread(
            @NonNull Runnable action,
            @NonNull Consumer<? super Throwable> onError
    ) {
        runOnUiThread(() -> getExceptionHandler().tryRun(action, onError));
    }

    /** @noinspection SameParameterValue*/
    @UiThread
    protected void showShortToastMessage(@StringRes int resId) {
        Toast.makeText(
                activity.getApplicationContext(),
                activity.getString(resId),
                Toast.LENGTH_SHORT
        ).show();
    }

    public void onErrorLockEditing(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, activity, "Error while locking editing.");
    }

    public void onErrorUnlockEditing(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, activity, "Error while unlocking editing.");
    }

    public void onAutoSyncError(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, activity, "Error while auto-sync.");
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    public String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    protected DeckDatabase getDeckDb(@NonNull String dbPath) {
        try {
            return AppDeckDbUtil
                    .getInstance(activity.getApplicationContext())
                    .getDatabase(activity, dbPath);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToDisposable(@NonNull Disposable disposable) {
        this.disposable.add(disposable);
    }

    public interface AutoSyncListener {
        void lockEditing();
        void unlockEditing();
        void onSyncSuccess(@NonNull WorkInfo workInfo);
    }
}
