package pl.gocards.ui.cards.xml.list.select;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import pl.gocards.ui.cards.xml.list.drag_swipe.DragSwipeCardTouchHelper;

/**
 * C_R_08 Select (cut) the card
 * @author Grzegorz Ziemski
 */
public class SelectCardTouchHelper extends DragSwipeCardTouchHelper {

    private static final int DETACHED_POSITION = -1;

    private boolean isLongPress;

    public SelectCardTouchHelper(SelectListCardsAdapter adapter) {
        super(adapter);
    }

    @Override
    public void clearView(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        if (isLongPress && viewHolder.getBindingAdapterPosition() != DETACHED_POSITION) {
            if (getAdapter().isSelectionMode()) {
                // C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
                ((SelectCardViewHolder) viewHolder).showSelectPopupMenu();
            } else {
                // C_02_02 When no card is selected and long pressing on the card, select the card.
                getAdapter().onCardInvertSelect((SelectCardViewHolder) viewHolder);
            }
        } else {
            super.clearView(recyclerView, viewHolder);
        }
    }

    @Override
    public void onSelectedChanged(
            @Nullable RecyclerView.ViewHolder viewHolder,
            int actionState
    ) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            isLongPress = true;
        }
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        isLongPress = false;
        return super.onMove(recyclerView, viewHolder, target);
    }

    @Override
    public SelectListCardsAdapter getAdapter() {
        return (SelectListCardsAdapter) super.getAdapter();
    }
}
