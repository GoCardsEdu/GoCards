package pl.gocards.ui.cards.list.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.DeckConfig
import java.util.LinkedList

/**
 * @author Grzegorz Ziemski
 */
open class ListCardsViewModel(
    val deckDb: DeckDatabase,
    application: Application
): AndroidViewModel(application) {

    val items: LinkedList<UiListCard> = LinkedList()

    var maxLines = DeckConfig.MAX_LINES_DEFAULT

    init {
        viewModelScope.launch(Dispatchers.IO) {
            maxLines = deckDb.deckConfigKtxDao().getListMaxLine()
        }
    }

    /**
     * C_R_01 Display all cards
     */
    fun loadCards(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            loadCardsAsync(onSuccess)
        }
    }

    /**
     * C_R_01 Display all cards
     */
    protected suspend fun loadCardsAsync(onSuccess: () -> Unit) {
        val cards = deckDb.cardKtxDao()
            .getAllCards()
            .map { mapCard(it) }
            .toMutableList()

        withContext(Dispatchers.Main) {
            items.clear()
            items.addAll(cards)
            onSuccess()
        }
    }

    protected fun mapCard(card: Card): UiListCard {
        return UiListCard(card.id!!, card.ordinal, card.term, card.definition)
    }

    /**
     * C_U_03 Dragging the card to another position.
     * Invoked when a card is being moved over other cards.
     */
    fun move(fromIndex: Int, toIndex: Int, onSuccess: () -> Unit = {}) {
        val card = items[fromIndex]
        items.remove(card)
        items.add(toIndex, card)
        onSuccess()
    }

    /**
     * C_U_03 Dragging the card to another position.
     * Invoked when the card is lowered and the position is saved.
     */
    fun moveDb(cardId: Int, toPosition: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().changeCardOrdinal(cardId, toPosition)
            loadCardsAsync(onSuccess)
        }
    }

    /**
     * C_D_25 Delete the card
     */
    fun delete(cardId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().deleteById(cardId)
            loadCardsAsync(onSuccess)
        }
    }

    /**
     * C_U_26 Undo card deletion
     */
    fun restore(cardId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().restore(cardId)
            loadCardsAsync(onSuccess)
        }
    }

    /**
     * C_D_11 Delete selected cards
     */
    fun delete(cards: Collection<Int>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().delete(cards)
            loadCardsAsync(onSuccess)
        }
    }

    /**
     * C_U_12 Undelete the selected cards
     */
    fun restore(cards: Collection<Int>, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().restore(cards)
            loadCardsAsync(onSuccess)
        }
    }

    fun paste(
        selectedCards: Collection<Int>,
        pasteAfterPosition: Int,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            deckDb.cardKtxDao().pasteCards(selectedCards, pasteAfterPosition)
            onSuccess()
        }
    }
}