package pl.gocards.ui.cards.study.file_sync;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.work.WorkInfo;

import java.util.Objects;

import pl.gocards.filesync.AutoSyncActivityUtil;
import pl.gocards.ui.cards.slider.slider.CardFragment;
import pl.gocards.ui.cards.study.display_ratio.DisplayRatioStudyCardActivity;
import pl.gocards.ui.cards.study.slider.fragment.StudyCardSliderFragment;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncStudyCardActivity
        extends DisplayRatioStudyCardActivity
        implements AutoSyncActivityUtil.AutoSyncListener {

    private boolean editingLocked = false;

    @Nullable
    private Integer cardId = null;

    @Nullable
    private AutoSyncActivityUtil autoSyncUtil;

    @NonNull
    @Override
    protected FileSyncStudyCardAdapter createAdapter() {
        return new FileSyncStudyCardAdapter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.autoSyncUtil = new AutoSyncActivityUtil(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoSyncUtil != null) autoSyncUtil.autoSyncOnCreate();
        observeEditingBlockedAt();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onDestroy() {
        super.onDestroy();
        if (getDeckDb() == null) return; // When onCreate fails
        if (autoSyncUtil != null) autoSyncUtil.autoSyncOnDestroy();
    }

    public void observeEditingBlockedAt() {
        if (autoSyncUtil != null)
            autoSyncUtil.observeEditingBlockedAt();
    }

    @Override
    public void lockEditing() {
        runOnUiThread(() -> {
            editingLocked = true;
            if (getStudyActiveFragment() != null) {
                getStudyActiveFragment().lockEditing();
            }
            refreshMenuOnAppBar();
        }, Objects.requireNonNull(autoSyncUtil)::onErrorLockEditing);
    }

    @Override
    public void unlockEditing() {
        runOnUiThread(() -> {
            editingLocked = false;
            if (getStudyActiveFragment() != null) {
                getStudyActiveFragment().unlockEditing();
            }
            refreshMenuOnAppBar();
        }, Objects.requireNonNull(autoSyncUtil)::onErrorUnlockEditing);
    }

    @Override
    public void onSyncSuccess(@NonNull WorkInfo workInfo) {
        if (isWorkSucceeded(workInfo) && isActivityResumed()) {
            cardId = getActiveCardId();
            getAdapter().loadCards();
        }
    }

    @Override
    public void onSuccessLoadCards() {
        if (cardId == null) return;
        Integer position = getActivityModel().findIndexByCardId(cardId);
        if (position != null) {
            getViewPager().setCurrentItem(position, false);
        } else {
            slideToFirstCard();
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

    public void refreshMenuOnAppBar() {
        invalidateOptionsMenu();
    }

    public boolean isEditingLocked() {
        return editingLocked;
    }

    @Nullable
    public FileSyncStudyCardFragment getStudyActiveFragment() {
        CardFragment cardFragment = super.getActiveFragment();
        if (cardFragment instanceof StudyCardSliderFragment) {
            return (FileSyncStudyCardFragment) super.getActiveFragment();
        }
        return null;
    }
}