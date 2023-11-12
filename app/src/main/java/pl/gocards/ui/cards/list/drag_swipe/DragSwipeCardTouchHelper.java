package pl.gocards.ui.cards.list.drag_swipe;

import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.list.standard.CardViewHolder;

/**
 * C_U_03 Dragging the card to another position.
 * C_D_04 Delete the card by swiping.
 *
 * @author Grzegorz Ziemski
 */
public class DragSwipeCardTouchHelper extends ItemTouchHelper.Callback {

    protected static final int NO_DRAG = -1;
    protected static final int DRAG_AFTER_POSITION = 1;

    private final DragSwipeListCardsAdapter adapter;
    private int dragToPosition = NO_DRAG;

    public DragSwipeCardTouchHelper(DragSwipeListCardsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void clearView(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        super.clearView(recyclerView, viewHolder);
        CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
        cardViewHolder.unfocusItemView();
        dragToPosition = NO_DRAG;
    }

    @Override
    public void onSelectedChanged(
            @Nullable RecyclerView.ViewHolder viewHolder,
            int actionState
    ) {
        super.onSelectedChanged(viewHolder, actionState);
        CardViewHolder cardViewHolder = (CardViewHolder) viewHolder;
        switch (actionState) {
            case ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.ACTION_STATE_SWIPE ->
                    Objects.requireNonNull(cardViewHolder).focusSingleTapItemView();
            case ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (dragToPosition != -1) {
                    Card card = adapter.getItem(dragToPosition);
                    if (card.getOrdinal() != dragToPosition + DRAG_AFTER_POSITION) {
                        adapter.onMovedCard(card, dragToPosition + DRAG_AFTER_POSITION);
                    }
                }
            }
        }
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        dragToPosition = target.getBindingAdapterPosition();
        adapter.onMoveCard(viewHolder.getBindingAdapterPosition(), dragToPosition);
        return true;
    }

    /**
     * C_D_04 Delete the card by swiping.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onClickDeleteCard(viewHolder.getBindingAdapterPosition());
    }

    /**
     * It is implemented by {@link DragSwipeCardViewHolder#onLongPress(MotionEvent)}
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    protected DragSwipeListCardsAdapter getAdapter() {
        return adapter;
    }
}
