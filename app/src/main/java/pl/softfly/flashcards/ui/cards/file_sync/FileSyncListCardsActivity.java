package pl.softfly.flashcards.ui.cards.file_sync;

import static pl.softfly.flashcards.filesync.FileSync.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.Config;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.filesync.FileSync;
import pl.softfly.flashcards.ui.FileSyncUtil;
import pl.softfly.flashcards.ui.cards.learning_progress.LearningProgressListCardsActivity;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncListCardsActivity extends LearningProgressListCardsActivity {

    private static final int SECONDS_5 = 5000;

    @NonNull
    private final FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

    private FileSyncUtil fileSyncUtil;

    @Nullable
    private DeckDatabase deckDb;

    private boolean editingLocked = false;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            deckDb = getDeckDatabase(getDeckDbPath());
            fileSyncUtil = new FileSyncUtil(this);
            observeEditingBlockedAt();
            fileSyncUtil.autoSync(getDeckDbPath(),  60);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deckDb == null) return;
        try {
             fileSyncUtil.autoSync(getDeckDbPath(), 0);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    protected void observeEditingBlockedAt() {
        deckDb.deckConfigLiveData().findByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                .observe(this, deckConfig -> getExceptionHandler().tryRun(
                        () -> {
                            if (deckConfig != null && deckConfig.getValue() != null) {
                                lockEditing();
                                checkIfEditingIsLocked();
                            } else {
                                unlockEditing();
                            }
                        },
                        getSupportFragmentManager(),
                        this.getClass().getSimpleName(),
                        "Error while unlocking / locking editing."
                ));
    }

    @Override
    protected FileSyncCardBaseViewAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new FileSyncCardBaseViewAdapter(this, getDeckDbPath());
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    private boolean isNotMenuCreated = true;

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menuIconWithText(
                menu.findItem(R.id.sync_excel),
                R.drawable.ic_sharp_sync_24,
                "Sync with Excel"
        );
        menuIconWithText(
                menu.findItem(R.id.export_excel),
                R.drawable.ic_round_file_upload_24,
                "Export to new Excel"
        );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (deckDb == null) return false;

        if (!isEditingUnlocked()) {
            menu.findItem(R.id.deselect_all).setVisible(false);
            menu.findItem(R.id.delete_selected).setVisible(false);
            menu.findItem(R.id.new_card).setVisible(false);
            menu.findItem(R.id.settings).setVisible(false);
            menu.findItem(R.id.sync_excel).setVisible(false);
            menu.findItem(R.id.export_excel).setVisible(false);
            return true;
        } else {
            return super.onPrepareOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_excel:
                fileSyncUtil.launchSyncFile(getDeckDbPath());
                return true;
            case R.id.export_excel:
                fileSyncUtil.launchExportToFile(getDeckDbPath());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* -----------------------------------------------------------------------------------------
     * FileSync features
     * ----------------------------------------------------------------------------------------- */

    /**
     * Unlock deck editing if any worker is working.
     * In a properly running application this code should never be executed.
     * That's just in case a worker fails and doesn't unlock the edit deck.
     */
    private void checkIfEditingIsLocked() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                deckDb.deckConfigAsyncDao().getByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
                        .subscribeOn(Schedulers.io())
                        .doOnError(Throwable::printStackTrace)
                        .doOnSuccess(deckConfig -> {
                            if (isFileSyncWorkersRuns()) {
                                deckConfig.setValue(null);
                                deckDb.deckConfigDao().update(deckConfig);
                                if (editingLocked) {
                                    unlockEditing();
                                    sendCrashlyticsLogUnlock();
                                }
                            } else if (!editingLocked) {
                                lockEditing();
                            } else if (editingLocked) {
                                handler.postDelayed(this, SECONDS_5);
                            }
                        })
                        .doOnEvent((value, error) -> {
                            if (value == null && error == null) {
                                if (editingLocked) {
                                    unlockEditing();
                                }
                            }
                        })
                        .subscribe(deckConfig -> {}, e -> getExceptionHandler().handleException(
                                e, getSupportFragmentManager(),
                                this.getClass().getSimpleName() + "_CheckIfEditingIsLocked",
                                (dialog, which) -> onBackPressed()
                        ));
            }
        });
    }

    protected boolean isFileSyncWorkersRuns() throws ExecutionException, InterruptedException {
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = workManager.getWorkInfos(
                WorkQuery.Builder
                        .fromTags(Arrays.asList(FileSync.TAG))
                        .addStates(Arrays.asList(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING))
                        .build()
        );
        return statuses.get().isEmpty();
    }

    protected void sendCrashlyticsLogUnlock() {
        if (Config.getInstance(getApplicationContext()).isCrashlyticsEnabled()) {
            crashlytics.setCustomKey("tag", TAG);
            crashlytics.log("Emergency editing unlock, the worker has failed miserably.");
        }
    }

    protected void lockEditing() {
        runOnUiThread(() -> {
            findViewById(R.id.editingLocked).setVisibility(View.VISIBLE);
            setDragSwipeEnabled(false);
            refreshMenuOnAppBar();
            editingLocked = true;
        }, this::onErrorLockEditing);
    }

    protected void unlockEditing() {
        runOnUiThread(() -> {
            findViewById(R.id.editingLocked).setVisibility(View.INVISIBLE);
            setDragSwipeEnabled(true);
            refreshMenuOnAppBar();
            editingLocked = false;
        }, this::onErrorLockEditing);
    }

    protected void onErrorLockEditing(Throwable e) {
        getExceptionHandler().handleException(
                e, getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while unlocking / locking editing."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public boolean isEditingUnlocked() {
        return !editingLocked;
    }

    @Override
    public FileSyncCardBaseViewAdapter getAdapter() {
        return (FileSyncCardBaseViewAdapter) super.getAdapter();
    }
}
