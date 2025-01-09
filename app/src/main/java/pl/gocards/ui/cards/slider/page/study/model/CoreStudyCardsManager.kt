package pl.gocards.ui.cards.slider.page.study.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    suspend fun getCachedOrFetchCard(id: Int): StudyCardUi {
        return getCached(id) ?: fetchCard(id)
    }

    fun getCached(id: Int): StudyCardUi? {
        return _cards.value[id]
    }

    suspend fun fetchCard(id: Int): StudyCardUi {
        val cardDb = deckDb.cardKtxDao().getCard(id)!!
        val card = mapCard(cardDb)
        cacheCard(card)
        return card
    }

    abstract suspend fun mapCard(card: Card): StudyCardUi

    private fun cacheCard(card: StudyCardUi) {
        _cards.value = _cards.value
            .toMutableMap()
            .apply { this[card.id] = card }
            .toMap()
    }

    suspend fun setupCardBeforeDisplay(id: Int, previousId: Int?) {
        setupCard(id, previousId) { current, previous ->
            setupCardBeforeDisplay(current, previous)
        }
    }

    protected suspend fun setupCard(
        id: Int,
        previousId: Int?,
        setupFn: suspend (StudyCardUi, StudyCardUi?) -> Unit
    ) {
        val current = getCachedOrFetchCard(id)
        val previous = previousId?.let { getCachedOrFetchCard(it) }
        setupFn(current, previous)
    }

    protected open fun setupCardBeforeDisplay(card: StudyCardUi, previous: StudyCardUi?) {
        hideDefinition(card)
    }

    private fun hideDefinition(card: StudyCardUi) {
        card.showDefinition.value = false
    }
}