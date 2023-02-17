package pl.softfly.flashcards.ui.cards.drag_swipe;

import android.view.MotionEvent;

import pl.softfly.flashcards.databinding.ItemCardBinding;
import pl.softfly.flashcards.ui.cards.search.SearchViewHolder;
import pl.softfly.flashcards.ui.cards.standard.CardViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class DragSwipeCardViewHolder extends SearchViewHolder {

    public DragSwipeCardViewHolder(ItemCardBinding binding, DragSwipeCardBaseViewAdapter adapter) {
        super(binding, adapter);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        getAdapter().getTouchHelper().startDrag(this);
    }

    @Override
    protected DragSwipeCardBaseViewAdapter getAdapter() {
        return (DragSwipeCardBaseViewAdapter) super.getAdapter();
    }
}