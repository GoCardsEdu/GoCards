package pl.gocards.ui.cards.kt.list.select

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.R
import pl.gocards.ui.cards.kt.list.ListCardsActivity
import pl.gocards.ui.cards.kt.list.display.CalcCardIdWidth
import pl.gocards.ui.cards.kt.list.display.CardViewHolder
import pl.gocards.ui.cards.kt.list.drag_swap.DragSwipeListCardsAdapter
import pl.gocards.ui.cards.kt.list.model.ListCardsViewModel
import pl.gocards.ui.cards.kt.list.model.UiListCard
import pl.gocards.ui.kt.theme.ExtendedColors
import java.util.stream.Collectors

/**
 * @author Grzegorz Ziemski
 */
open class SelectListCardsAdapter(
    viewModel: ListCardsViewModel,
    val selectViewModel: SelectCardsViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    scope: CoroutineScope,
    calcCardIdWidth: CalcCardIdWidth = CalcCardIdWidth.getInstance(),
): DragSwipeListCardsAdapter(
    calcCardIdWidth,
    viewModel,
    snackbarHostState,
    colors,
    activity,
    scope
) {

    private var hintsOnce = true

    /* -----------------------------------------------------------------------------------------
     * Methods overridden
     * ----------------------------------------------------------------------------------------- */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return SelectCardViewHolder(onCreateView(parent), colors, activity, this)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (isCardSelected(position)) {
            val selectHolder = holder as SelectCardViewHolder
            selectHolder.select()
        } else {
            holder.unfocus()
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Cards selection
     * ----------------------------------------------------------------------------------------- */

    fun isSelectionMode(): Boolean {
        return selectViewModel.isSelectionMode()
    }

    /**
     * C_02_02 When no card is selected and long pressing on the card, select the card.
     */
    fun invertSelection(viewHolder: SelectCardViewHolder) {
        if (isCardSelected(viewHolder.bindingAdapterPosition)) {
            deselect(viewHolder)
        } else {
            select(viewHolder)
        }
    }

    fun isCardSelected(position: Int): Boolean {
        val card = getCard(position)
        return selectViewModel.isSelected(card)
    }

    /**
     * C_R_08 Select (cut) the card.
     */
    fun select(holder: SelectCardViewHolder) {
        holder.select()
        val card = getCard(holder.bindingAdapterPosition)
        selectViewModel.select(card)

        if (selectViewModel.countSelected() == 1) {
            if (hintsOnce) {
                showShortToastMessage(R.string.cards_list_toast_single_tap)
                showShortToastMessage(R.string.cards_list_toast_long_press)
                hintsOnce = false
            }
        }
    }

    /**
     * C_R_09 Deselect the card.
     */
    fun deselect(holder: SelectCardViewHolder) {
        val card = getCard(holder.bindingAdapterPosition)
        selectViewModel.deselect(card)
        holder.unfocus()
    }

    /**
     * C_D_10 Deselect the cards.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun deselectAll() {
        if (selectViewModel.countSelected() > 0) {
            val selected = selectViewModel.getSelectedCardIds()
            selectViewModel.deselectAll()

            for (cardId in selected) {
                val position = getCards().indexOfFirst { it.id == cardId }
                notifyItemChanged(position)
            }
        }
    }

    /**
     * C_D_11 Delete selected cards
     */
    @SuppressLint("CheckResult")
    fun deleteSelected() {
        val deleteCards = selectViewModel.toSet()

        // Delete the items in the order from the end.
        val orderByOrdinalDesc = deleteCards
            .stream()
            .sorted { card1, card2 -> card2.ordinal.compareTo(card1.ordinal) }
            .map { card -> getCards().indexOf(card) }
            .collect(Collectors.toList())

        val deleteCardIds = deleteCards
            .stream()
            .map { card -> card.id }
            .collect(Collectors.toList())

        viewModel.delete(deleteCardIds) {
            scope.launch {
                selectViewModel.deselectAll()

                orderByOrdinalDesc.forEach { position -> notifyItemRemoved(position) }
                val firstPosition = orderByOrdinalDesc.last()
                val countItem = itemCount - firstPosition + 1
                notifyItemRangeChanged(firstPosition, countItem)

                showSnackbar(
                    if (deleteCards.size > 1) {
                        R.string.cards_list_toast_deleted_card
                    } else {
                        R.string.cards_list_toast_deleted_cards
                    },
                    R.string.restore
                ) { restoreSelected(deleteCards) }
            }
        }
    }

    /**
     * C_U_12 Undelete the selected cards
     */
    @SuppressLint("NotifyDataSetChanged")
    fun restoreSelected(cards: Set<UiListCard>) {
        val cardIds = cards
            .stream()
            .map { card -> card.id }
            .collect(Collectors.toList())

        viewModel.restore(cardIds) {
            scope.launch {
                selectViewModel.select(cards)
                notifyDataSetChanged()
                showCardsRestoredToast(cards)
            }
        }
    }

    @UiThread
    private fun showCardsRestoredToast(cards: Set<UiListCard>) {
        val text = if (cards.size > 1) {
            R.string.cards_list_toast_restore_cards
        } else {
             R.string.cards_list_toast_restore_card
        }
        showShortToastMessage(text)
    }

    /**
     * C_U_13 Paste cards before
     */
    fun pasteBefore(beforePosition: Int) {
        pasteCards(getOrdinalFromItemPosition(beforePosition) - 1)
    }

    /**
     * C_U_14 Paste cards after
     */
    fun pasteAfter(afterPosition: Int) {
        pasteCards(getOrdinalFromItemPosition(afterPosition))
    }

    private fun pasteCards(pasteAfterPosition: Int) {
        val selectedCardIds = selectViewModel.getSelectedCardIds()
        viewModel.paste(selectedCardIds, pasteAfterPosition) {
            loadCards()
        }
    }

    /**
     * C_D_25 Delete the card
     */
    @UiThread
    override suspend fun doOnSuccessDelete(position: Int, card: UiListCard) {
        val wasSelected = selectViewModel.isSelected(card)
        if (!wasSelected) {
            super.doOnSuccessDelete(position, card)
        } else {
            selectViewModel.deselect(card)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)

            showSnackbar(
                R.string.cards_list_toast_deleted_card,
                R.string.restore,
            ) { restoreSelected(card) }
        }
    }

    /**
     * C_U_26 Undo card deletion
     */
    private fun restoreSelected(card: UiListCard) {
        viewModel.restore(card.id) {
            scope.launch {
                doOnSuccessRestoreSelected(card)
            }
        }
    }

    /**
     * C_U_26 Undo card deletion
     */
    @UiThread
    private suspend fun doOnSuccessRestoreSelected(card: UiListCard) {
        selectViewModel.select(card)
        super.doOnSuccessRestore(card)
    }

    /*
     * Instead of the fake position from the recycler, it is better to use the real position of the card.
     * Sometimes the item position may not be adequate because some cards may be hidden, e.g. during a search.
     */
    @UiThread
    private fun getOrdinalFromItemPosition(position: Int): Int {
        return getCard(position).ordinal
    }
}