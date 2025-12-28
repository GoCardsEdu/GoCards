package pl.gocards.ui.cards.slider.page.study.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card

/**
 * @author Grzegorz Ziemski
 */
abstract class CoreStudyCardsManager(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    val deckDbPath: String
) {

    private val _cards = mutableStateOf(emptyMap<Int, StudyCardUi>())
    val cards: State<Map<Int, StudyCardUi>> get() = _cards
    private val mutex = Mutex()

    protected suspend fun getCachedOrFetchCard(id: Int): StudyCardUi {
        return getCached(id) ?: fetchCard(id)
    }

    suspend fun getCached(id: Int): StudyCardUi? {
        mutex.withLock {
            return _cards.value[id]
        }
    }

    suspend fun fetchCard(id: Int): StudyCardUi {
        mutex.withLock {
            val cardDb = deckDb.cardKtxDao().getCard(id)!!
            val card = mapCard(cardDb)
            cacheCard(card)
            return card
        }
    }

    suspend fun refreshCard(id: Int): StudyCardUi {
        mutex.withLock {
            val cardDb = deckDb.cardKtxDao().getCard(id)!!
            val card = mapCard(cardDb, _cards.value[id]!!)
            cacheCard(card)
            return card
        }
    }

    abstract suspend fun mapCard(card: Card): StudyCardUi

    abstract suspend fun mapCard(card: Card, old: StudyCardUi): StudyCardUi

    private fun cacheCard(card: StudyCardUi) {
        _cards.value = _cards.value
            .toMutableMap()
            .apply { this[card.id] = card }
            .toMap()
    }

    open suspend fun onCardPause(cardId: Int) {
        hideDefinition(cardId)
    }

    protected fun hideDefinition(cardId: Int) {
        val studyCards = cards.value
        val studyCard = studyCards[cardId] ?: return
        studyCard.showDefinition.value = false
    }

    fun showDefinition(cardId: Int) {
        val studyCards = cards.value
        val studyCard = studyCards[cardId] ?: return
        studyCard.showDefinition.value = true
    }
}