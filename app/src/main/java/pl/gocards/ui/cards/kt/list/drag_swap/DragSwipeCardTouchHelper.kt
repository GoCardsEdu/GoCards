package pl.gocards.ui.cards.kt.list.drag_swap

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.ui.cards.kt.list.display.CardViewHolder

/**
 * C_U_03 Dragging the card to another position.
 * C_D_04 Delete the card by swiping.
 *
 * @author Grzegorz Ziemski
 */
open class DragSwipeCardTouchHelper(
    open val adapter: DragSwipeListCardsAdapter
): ItemTouchHelper.Callback() {

    companion object {
        private const val NO_DRAG = -1
    }

    private var dragToPosition = NO_DRAG

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        val position = viewHolder.bindingAdapterPosition
        if (position > -1) {
            adapter.setActive(viewHolder.bindingAdapterPosition, false)
            (viewHolder as CardViewHolder).unfocus()
            dragToPosition = NO_DRAG
        }
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        super.onSelectedChanged(viewHolder, actionState)

        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG,
            ItemTouchHelper.ACTION_STATE_SWIPE -> {
                val index = viewHolder?.bindingAdapterPosition ?: return
                adapter.setActive(index, true)
                (viewHolder as CardViewHolder).focus()
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                if (dragToPosition != -1) {
                    adapter.moveDb(dragToPosition, dragToPosition + 1)
                }
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        dragToPosition = target.bindingAdapterPosition
        adapter.move(viewHolder.bindingAdapterPosition, dragToPosition)
        return true
    }

    /**
     * C_D_04 Delete the card by swiping.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.delete(viewHolder.bindingAdapterPosition)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }
}