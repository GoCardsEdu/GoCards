package pl.gocards.ui.cards.list.select

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.ui.cards.list.display.CardViewHolder
import pl.gocards.ui.cards.list.drag_swap.DragSwipeCardTouchHelper


/**
 * C_R_08 Select (cut) the card
 * @author Grzegorz Ziemski
 */
class SelectCardTouchHelper(
    override val adapter: SelectListCardsAdapter
): DragSwipeCardTouchHelper(adapter) {

    private var isLongPress = false

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        if (isLongPress && viewHolder.bindingAdapterPosition != DETACHED_POSITION) {
            if (adapter.isSelectionMode()) {
                // C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
                (viewHolder as CardViewHolder).showPopupMenu()
            } else {
                // C_02_02 When no card is selected and long pressing on the card, select the card.
                adapter.invertSelection(viewHolder as SelectCardViewHolder)
            }
        } else {
            super.clearView(recyclerView, viewHolder)
        }
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            isLongPress = true
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        isLongPress = false
        return super.onMove(recyclerView, viewHolder, target)
    }

    companion object {
        private const val DETACHED_POSITION = -1
    }
}