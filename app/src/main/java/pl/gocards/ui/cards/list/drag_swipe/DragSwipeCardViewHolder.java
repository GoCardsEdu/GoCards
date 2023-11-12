package pl.gocards.ui.cards.list.drag_swipe;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.list.standard.CardViewHolder;

/**
 * C_U_03 Dragging the card to another position.
 * C_D_04 Delete the card by swiping.
 *
 * @author Grzegorz Ziemski
 */
public class DragSwipeCardViewHolder extends CardViewHolder {

    public DragSwipeCardViewHolder(@NonNull ItemCardBinding binding, @NonNull DragSwipeListCardsAdapter adapter) {
        super(binding, adapter);
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        getAdapter().getTouchHelper().startDrag(this);
    }

    @NonNull
    @Override
    protected DragSwipeListCardsAdapter getAdapter() {
        return (DragSwipeListCardsAdapter) super.getAdapter();
    }
}