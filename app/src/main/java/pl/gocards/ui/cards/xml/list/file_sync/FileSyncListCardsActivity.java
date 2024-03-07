package pl.gocards.ui.cards.xml.list.file_sync;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.work.WorkInfo;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import pl.gocards.R;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.filesync.FileSyncLauncher;
import pl.gocards.filesync.FileSyncProLauncher;
import pl.gocards.filesync.AutoSyncActivityUtil;
import pl.gocards.ui.cards.xml.list.learning_progress.LearningProgressListCardsActivity;

/**
 * FS_PRO_S.6. Lock the deck editing.
 * @author Grzegorz Ziemski
 */
public class FileSyncListCardsActivity extends LearningProgressListCardsActivity
        implements AutoSyncActivityUtil.AutoSyncListener {

    @Nullable
    private final FileSyncLauncher fileSyncLauncher = FileSyncLauncher.getInstance(this, getDisposable());
    @Nullable
    private final FileSyncProLauncher fileSyncProLauncher = FileSyncProLauncher.getInstance(this, this::onSyncSuccess, getDisposable());
    @NotNull // Initiated by onCreate
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;
    private boolean editingLocked = false;
    @Nullable
    private AutoSyncActivityUtil autoSyncUtil;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deckDb = getDeckDb(getDeckDbPath());
        autoSyncUtil = new AutoSyncActivityUtil(this);
    }

    @SuppressLint("CheckResult")
    public void autoSyncOnCreate() {
        if (autoSyncUtil != null) autoSyncUtil.autoSyncOnCreate();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onDestroy() {
        super.onDestroy();
        if (deckDb == null) return; // When onCreate fails
        if (autoSyncUtil != null) autoSyncUtil.autoSyncOnDestroy();
    }

    @NonNull
    @Override
    protected FileSyncListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new FileSyncListCardsAdapter(this);
    }

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Don't auto-sync after the screen rotation
        autoSyncUtil = null;
    }

    /* -----------------------------------------------------------------------------------------
     * Menu
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menuIconWithText(
                menu.findItem(R.id.sync_excel),
                R.drawable.ic_sharp_sync_24
        );
        menuIconWithText(
                menu.findItem(R.id.export_excel),
                R.drawable.ic_round_file_upload_24
        );
        menuIconWithText(
                menu.findItem(R.id.export_csv),
                R.drawable.ic_round_file_upload_24
        );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        if (isEditingLocked()) {
            menu.findItem(R.id.deselect_all_cards).setVisible(false);
            menu.findItem(R.id.delete_selected_cards).setVisible(false);
            menu.findItem(R.id.new_card).setVisible(false);
            menu.findItem(R.id.deck_settings).setVisible(false);
            menu.findItem(R.id.sync_excel).setVisible(false);
            menu.findItem(R.id.export_excel).setVisible(false);
            menu.findItem(R.id.export_csv).setVisible(false);
            return true;
        } else {
            return super.onPrepareOptionsMenu(menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync_excel -> {
                Objects.requireNonNull(fileSyncProLauncher);
                fileSyncProLauncher.launchSyncFile(getDeckDbPath());
                return true;
            }
            case R.id.export_excel -> {
                Objects.requireNonNull(fileSyncLauncher);
                fileSyncLauncher.launchExportToExcel(getDeckDbPath());
                return true;
            }
            case R.id.export_csv -> {
                Objects.requireNonNull(fileSyncLauncher);
                fileSyncLauncher.launchExportToCsv(getDeckDbPath());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /* -----------------------------------------------------------------------------------------
     * FileSync features
     * ----------------------------------------------------------------------------------------- */

    public void observeEditingBlockedAt() {
        if (autoSyncUtil != null)
            autoSyncUtil.observeEditingBlockedAt();
    }

    @Override
    public void lockEditing() {
        runOnUiThread(() -> {
            getEditingLocked().setVisibility(View.VISIBLE);
            setDragSwipeEnabled(false);
            refreshMenuOnAppBar();
            editingLocked = true;
        }, Objects.requireNonNull(autoSyncUtil)::onErrorLockEditing);
    }

    @Override
    public void unlockEditing() {
        runOnUiThread(() -> {
            getEditingLocked().setVisibility(View.INVISIBLE);
            setDragSwipeEnabled(true);
            refreshMenuOnAppBar();
            editingLocked = false;
        }, Objects.requireNonNull(autoSyncUtil)::onErrorUnlockEditing);
    }

    @Override
    public void onSyncSuccess(@NonNull WorkInfo workInfo) {
        if (isWorkSucceeded(workInfo) && isActivityResumed()) {
            loadItems();
        }
    }

    private boolean isWorkSucceeded(@NonNull WorkInfo workInfo) {
        return workInfo.getState() == WorkInfo.State.SUCCEEDED;
    }

    private boolean isActivityResumed() {
        return this.getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.RESUMED);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public boolean isEditingLocked() {
        return editingLocked;
    }

    public boolean isEditingUnlocked() {
        return !editingLocked;
    }

    @NonNull
    @Override
    public FileSyncListCardsAdapter getAdapter() {
        return (FileSyncListCardsAdapter) super.getAdapter();
    }

    @NonNull
    protected TextView getEditingLocked() {
        return getBinding().editingLocked;
    }
}
