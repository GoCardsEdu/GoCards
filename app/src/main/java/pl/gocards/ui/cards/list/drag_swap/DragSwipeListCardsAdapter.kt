package pl.gocards.ui.cards.list.drag_swap

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.display.CalcCardIdWidth
import pl.gocards.ui.cards.list.display.ListCardsAdapter
import pl.gocards.ui.cards.list.model.ListCardsViewModel
import pl.gocards.ui.theme.ExtendedColors

/**
 * C_U_03 Dragging the card to another position.
 * @author Grzegorz Ziemski
 */
open class DragSwipeListCardsAdapter(
    calcCardIdWidth: CalcCardIdWidth = CalcCardIdWidth.getInstance(),
    viewModel: ListCardsViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    scope: CoroutineScope
): ListCardsAdapter(
    calcCardIdWidth,
    viewModel,
    snackbarHostState,
    colors,
    activity,
    scope
) {

    /**
     * Invoked when a card is being moved over other cards.
     */
    fun move(fromIndex: Int, toIndex: Int) {
        viewModel.move(fromIndex, toIndex) {
            scope.launch {
                notifyItemMoved(fromIndex, toIndex)
            }
        }
    }

    /**
     * Invoked when the card is lowered and the position is saved.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun moveDb(index: Int, ordinal: Int) {
        val card = getCard(index)
        if (card.ordinal != ordinal) {
            viewModel.moveDb(card.id, ordinal) {
                scope.launch {
                    // It works smoother than #notifyItemMoved here.
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun setActive(index: Int, value: Boolean) {
        val card = getCard(index)
        card.isActive.value = value
    }
}