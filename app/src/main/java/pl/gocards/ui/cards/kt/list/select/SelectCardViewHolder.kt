package pl.gocards.ui.cards.kt.list.select

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import pl.gocards.databinding.ItemCardBinding
import pl.gocards.ui.cards.kt.list.ListCardsActivity
import pl.gocards.ui.cards.kt.list.display.CardViewHolder
import pl.gocards.ui.cards.kt.list.display.ListCardsPopupMenu
import pl.gocards.ui.kt.theme.ExtendedColors

/**
 * @author Grzegorz Ziemski
 */
class SelectCardViewHolder(
    binding: ItemCardBinding,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    val adapter: SelectListCardsAdapter
): CardViewHolder(binding, colors, activity) {

    /* -----------------------------------------------------------------------------------------
     * Change the look of the view.
     * ----------------------------------------------------------------------------------------- */

    fun select() {
        // itemView.isSelected = true
        itemView.setBackgroundColor(colors.colorItemSelected.toArgb())
    }

    /* -----------------------------------------------------------------------------------------
     * GestureDetector
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_02_03 When any card is selected and tap on the card, select or unselect the card.
     */
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return if (adapter.isSelectionMode()) {
            adapter.invertSelection(this)
            false
        } else {
            super.onSingleTapUp(e)
        }
    }

    @Composable
    override fun CreatePopupMenu(onDismiss: () -> Unit) {
        if (adapter.isSelectionMode()) {
            SelectListCardsPopupMenu(adapter, this, onDismiss)
        } else {
            ListCardsPopupMenu(adapter, this, onDismiss)
        }
    }
}