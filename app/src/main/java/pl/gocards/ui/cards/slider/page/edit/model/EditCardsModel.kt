package pl.gocards.ui.cards.slider.page.edit.model

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.util.TimeUtil

/**
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
class EditCardsModel(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    val deckDbPath: String,
    application: Application
): AndroidViewModel(application) {

    private val cards = mutableStateOf(mapOf<Int, EditCardUi>())
    private var _cards: MutableMap<Int, EditCardUi> = mutableMapOf()

    suspend fun loadCard(id: Int?) {
        if (id == null) {
            return
        } else if (_cards[id] != null) {
            return
        } else {
            reloadCard(id)
        }
    }

    suspend fun reloadCard(id: Int) {
        val cardDb = deckDb.cardKtxDao().getCard(id)!!
        val card = mapCard(cardDb)
        _cards[id] = card
        this.cards.value = _cards.toMutableMap()
    }

    private suspend fun mapCard(card: Card): EditCardUi {
        val cardId = card.id!!
        val learningHistory = deckDb.cardLearningHistoryKtxDao().findCurrentByCardId(cardId)

        return EditCardUi(
            id = cardId,
            term = mutableStateOf(card.term) ,
            definition = mutableStateOf(card.definition),
            nextReplayAt = learningHistory?.nextReplayAt,
            disabled = mutableStateOf(card.disabled)
        )
    }

    suspend fun saveCard(editCard: EditCardUi) {
        val updatedAt = TimeUtil.getNowEpochSec()
        val card = mapCard(editCard)
        card.updatedAt = updatedAt

        deckDb.cardKtxDao().updateAll(card)
        appDb.deckKtxDao().refreshLastUpdatedAt(deckDbPath, updatedAt)
    }

    private suspend fun mapCard(editCard: EditCardUi): Card {
        val card = deckDb.cardKtxDao().getCard(editCard.id)!!
        card.term = editCard.term.value
        card.definition = editCard.definition.value
        card.disabled = editCard.disabled.value
        Card.setHtmlFlags(card)
        return card
    }

    fun getCardsState(): State<Map<Int, EditCardUi>> {
        return cards
    }
}

class EditCardsModelFactory(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    val deckDbPath: String,
    val application: Application
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EditCardsModel::class.java)) {
            EditCardsModel(deckDb, appDb, deckDbPath, application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}