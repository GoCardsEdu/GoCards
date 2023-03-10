package pl.softfly.flashcards.ui.cards.file_sync;

import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import pl.softfly.flashcards.databinding.ItemCardBinding;
import pl.softfly.flashcards.ui.cards.learning_progress.LearningProgressViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncCardViewHolder extends LearningProgressViewHolder {

    public FileSyncCardViewHolder(
            ItemCardBinding binding,
            FileSyncCardBaseViewAdapter adapter
    ) {
        super(binding, adapter);
    }

    @Override
    public void showPopupMenu(CreatePopupMenu createPopupMenu) {
        if (getAdapter().getActivity().isEditingUnlocked()) super.showPopupMenu(createPopupMenu);
    }

    @Override
    public void showSelectPopupMenu() {
        if (getAdapter().getActivity().isEditingUnlocked()) super.showSelectPopupMenu();
    }

    @Override
    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        if (getAdapter().getActivity().isEditingUnlocked()) return super.onPopupMenuItemClick(item);
        else throw new RuntimeException("Deck editing is locked. Sync in progress..");
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (getAdapter().getActivity().isEditingUnlocked()) super.onLongPress(e);
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        if (getAdapter().getActivity().isEditingUnlocked()) return super.onSingleTapUp(e);
        return false;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @Override
    public FileSyncCardBaseViewAdapter getAdapter() {
        return (FileSyncCardBaseViewAdapter) super.getAdapter();
    }
}