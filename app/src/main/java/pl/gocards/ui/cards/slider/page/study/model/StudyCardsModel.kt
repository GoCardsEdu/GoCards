package pl.gocards.ui.cards.slider.page.study.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.sp
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.ui.cards.slider.slider.model.SliderCardUi

/**
 * @author Grzegorz Ziemski
 */
class StudyCardsModel(
    deckDb: DeckDatabase,
    appDb: AppDatabase,
    deckDbPath: String,
) : LearningProgressModel(deckDb, appDb, deckDbPath) {

    val cards = mutableStateOf(mapOf<Int, StudyCardUi>())

    /* -----------------------------------------------------------------------------------------
     * Loads cards
     * ----------------------------------------------------------------------------------------- */

    suspend fun getCard(id: Int): StudyCardUi? {
        return cards.value[id] ?: reloadCard(id)
    }

    suspend fun reloadCard(id: Int): StudyCardUi? {
        val cardDb = deckDb.cardKtxDao().getCard(id) ?: return null
        val card = mapCard(cardDb)

        val updatedCards = cards.value.toMutableMap()
        updatedCards[card.id] = card
        cards.value = updatedCards.toMap()

        setTermHeightPx(card, null)
        return card
    }

    private suspend fun mapCard(card: Card): StudyCardUi {
        val cardId = card.id!!
        val termFontSize = deckDb.cardConfigKtxDao().getStudyCardTermFontSize(cardId)
        val definitionFontSize = deckDb.cardConfigKtxDao().getStudyCardDefinitionFontSize(cardId)
        val displayRatio = deckDb.cardConfigKtxDao().getStudyCardTdDisplayRatio(cardId)

        return StudyCardUi(
            id = cardId,
            term = card.term,
            definition = card.definition,
            isTermSimpleHtml = card.isTermSimpleHtml,
            isTermFullHtml = card.isTermFullHtml,
            isDefinitionSimpleHtml = card.isDefinitionSimpleHtml,
            isDefinitionFullHtml= card.isDefinitionFullHtml,
            termFontSize = mutableStateOf(termFontSize?.sp),
            defFontSize = mutableStateOf(definitionFontSize?.sp),
            displayRatio = displayRatio ?: 0f,
            current = getCurrent(cardId),
            nextAfterAgain = getNextAfterAgain(cardId),
            nextAfterQuick = getNextAfterQuick(cardId),
            nextAfterEasy = getNextAfterEasy(cardId),
            nextAfterHard = getNextAfterHard(cardId),
        )
    }

    /* -----------------------------------------------------------------------------------------
     * Set current page
     * ----------------------------------------------------------------------------------------- */

    suspend fun loadCard(id: Int, previousId: Int?) {
        val studyCard = getCard(id) ?: return
        studyCard.showDefinition.value = false

        val previousStudyCard = previousId?.let { getCard(it) }
        copyDisplaySettings(studyCard, previousStudyCard)
        if (studyCard.termHeightPx.value != null) {
            studyCard.canPreSetup = false
        }
    }

    suspend fun setTermHeightPx(id: Int, previousId: Int?) {
        val card = getCard(id) ?: return
        setTermHeightPx(
            card,
            previousId?.let { getCard(previousId) }
        )
    }

    /* -----------------------------------------------------------------------------------------
    * Save
    * ----------------------------------------------------------------------------------------- */

    suspend fun saveCard(id: Int) {
        val card = cards.value[id]
        if (card != null) {
            saveDisplaySettings(card)
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Learning Progress
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_U_32 Again
     */
    suspend fun onAgainClick(sliderCard: SliderCardUi) {
        val cardId = sliderCard.id
        val studyCard = cards.value[cardId] ?: return

        super.onAgainClick(studyCard)
        saveCard(cardId)
        reloadCard(cardId)
    }

    /**
     * C_U_33 Quick Repetition (5 min)
     */
    suspend fun onQuickClick(sliderCard: SliderCardUi) {
        val studyCard = cards.value[sliderCard.id] ?: return
        super.onQuickClick(studyCard)
    }

    /**
     * C_U_35 Easy (5 days)
     */
    suspend fun onEasyClick(sliderCard: SliderCardUi) {
        val studyCard = cards.value[sliderCard.id] ?: return
        super.onEasyClick(studyCard)
    }

    /**
     * C_U_34 Hard (3 days)
     */
    suspend fun onHardClick(sliderCard: SliderCardUi) {
        val studyCard = cards.value[sliderCard.id] ?: return
        super.onHardClick(studyCard)
    }
}