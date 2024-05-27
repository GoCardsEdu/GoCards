package pl.gocards.ui.cards.xml.list.file_sync;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.xml.list.learning_progress.LearningProgressViewHolder;

/**
 * FS_PRO_S.6. Lock the deck editing.
 * @author Grzegorz Ziemski
 */
public class FileSyncCardViewHolder extends LearningProgressViewHolder {

    public FileSyncCardViewHolder(
            @NonNull ItemCardBinding binding,
            @NonNull FileSyncListCardsAdapter adapter
    ) {
        super(binding, adapter);
    }

    @Override
    public void showSingleTapMenu() {
        if (getAdapter().requireActivity().isEditingUnlocked()) {
            super.showSingleTapMenu();
        }
    }

    @Override
    public void showSelectPopupMenu() {
        if (requireActivity().isEditingUnlocked()) {
            super.showSelectPopupMenu();
        }
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        if (getAdapter().requireActivity().isEditingUnlocked()) super.onLongPress(e);
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        if (getAdapter().requireActivity().isEditingUnlocked()) return super.onSingleTapUp(e);
        return false;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public FileSyncListCardsAdapter getAdapter() {
        return (FileSyncListCardsAdapter) super.getAdapter();
    }

    @NonNull
    @Override
    protected FileSyncListCardsActivity requireActivity() {
        return getAdapter().requireActivity();
    }
}