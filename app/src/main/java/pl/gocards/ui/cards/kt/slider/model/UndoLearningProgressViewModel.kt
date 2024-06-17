package pl.gocards.ui.cards.kt.slider.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.CardLearningHistory
import pl.gocards.ui.cards.kt.slider.page.add.model.NewCardsModel
import pl.gocards.ui.cards.kt.slider.page.edit.model.EditCardsModel
import pl.gocards.ui.cards.kt.slider.page.study.model.StudyCardsModel
import pl.gocards.ui.cards.kt.slider.slider.model.Mode
import pl.gocards.ui.cards.kt.slider.slider.model.SliderCardUi
import pl.gocards.ui.cards.kt.slider.slider.model.SliderCardsModel
import pl.gocards.util.CardReplayScheduler
import java.util.Deque
import java.util.LinkedList
import kotlin.math.abs

/**
 * C_U_39 Undo click on the study buttons.
 * @author Grzegorz Ziemski
 */
open class UndoLearningProgressViewModel(
    defaultMode: Mode,
    deckDb: DeckDatabase,
    sliderCardsModel: SliderCardsModel,
    studyCardsModel: StudyCardsModel,
    newCardsModel: NewCardsModel,
    editCardsModel: EditCardsModel,
    application: Application
) : DeleteSliderCardsViewModel(
    defaultMode,
    deckDb,
    sliderCardsModel,
    studyCardsModel,
    newCardsModel,
    editCardsModel,
    application
) {

    private var lastPage: Int? = null

    /**
     * It is cleared when cards are slided by the user.
     */
    protected val undoCards: Deque<SliderCardUi> = LinkedList()

    /**
     * Undo the first click again, only if it happened during the current study session.
     */
    protected val forgottenInThisSession: MutableList<Int> = mutableListOf()

    protected fun revertPreviousLearningProgress(
        backToCard: SliderCardUi,
        currentPage: Int,
        currentCard: SliderCardUi,
        sliderCards: List<SliderCardUi>
    ) {
        val wasAgain: Boolean = sliderCardsModel.findById(backToCard.id) != null
        viewModelScope.launch(Dispatchers.IO) {
            revertPreviousLearningProgressDb(backToCard.id)
            if (wasAgain) {
                sliderCardsModel.slideToPreviousPage()
                showDefinition(backToCard.id)
            } else {
                val deletePage = if (currentPage == 0) {
                    val isFirst = currentCard.ordinal!! > backToCard.ordinal!!
                    if (isFirst) { 0 } else { sliderCards.size }
                } else {
                    currentPage
                }
                sliderCardsModel.restorePage(deletePage, backToCard)
                showDefinition(backToCard.id)
            }
        }
    }

    private fun showDefinition(cardId: Int) {
        val studyCards = studyCardsModel.cards.value
        val studyCard = studyCards[cardId] ?: return
        studyCard.showDefinition.value = true
    }

    private suspend fun revertPreviousLearningProgressDb(cardId: Int) {
        val current = deckDb.cardLearningHistoryKtxDao().findCurrentByCardId(cardId) ?: return
        val previous = findPreviousLearningHistory(current)
        if (previous != null) {
            revertPrevious(current, previous)
        } else {
            revertFirst(current)
        }
        studyCardsModel.reloadCard(cardId)
    }

    protected open fun revertFirst(current: CardLearningHistory) {
        if (current.countNotMemorized == 1) {
            revertFirstNoMemorized(current)
        } else {
            revertFirstMemorized(current)
        }
    }

    protected open fun revertFirstNoMemorized(current: CardLearningHistory) {
        if (forgottenInThisSession.contains(current.cardId)) {
            revertFirstMemorized(current)
        } else {
            deckDb.cardLearningProgressDao().updateIsMemorized(current.cardId, false)
            current.interval = CardReplayScheduler.AGAIN_FIRST_INTERVAL_MINUTES
            deckDb.cardLearningHistoryDao().update(current)
        }
    }

    protected open fun revertFirstMemorized(current: CardLearningHistory) {
        deckDb.cardLearningProgressDao().deleteByCardId(current.cardId)
        deleteLearningHistory(current)
    }

    protected open fun revertPrevious(current: CardLearningHistory, previous: CardLearningHistory) {
        if (!previous.wasMemorized!!) {
            revertPreviousNoMemorized(current, previous)
        } else {
            revertPreviousMemorized(current, previous)
        }
    }

    /**
     * Restores when "Again" has been clicked
     */
    protected open fun revertPreviousNoMemorized(
        current: CardLearningHistory,
        previous: CardLearningHistory
    ) {
        if (forgottenInThisSession.contains(current.cardId)) {
            revertPreviousMemorized(current, previous)
        } else {
            deckDb.cardLearningProgressDao().updateIsMemorized(current.cardId, false)
            current.interval = previous.interval
            deckDb.cardLearningHistoryDao().update(current)
        }
    }

    protected open fun revertPreviousMemorized(
        current: CardLearningHistory,
        previous: CardLearningHistory
    ) {
        setPreviousLearningHistoryAsCurrent(previous)
        deleteLearningHistory(current)
    }

    private fun findPreviousLearningHistory(current: CardLearningHistory): CardLearningHistory? {
        return deckDb.cardLearningHistoryDao()
            .findByCardIdAndLearningHistoryId(
                current.cardId,
                current.replayId - 1
            )
    }

    private fun setPreviousLearningHistoryAsCurrent(previous: CardLearningHistory) {
        deckDb.cardLearningProgressDao().update(
            previous.cardId,
            previous.id!!,
            true
        )
        previous.wasMemorized = null
        previous.memorizedDuration = null
        deckDb.cardLearningHistoryDao().update(previous)
    }

    private fun deleteLearningHistory(current: CardLearningHistory) {
        deckDb.cardLearningHistoryDao().delete(current)
    }

    protected fun clearUndoCards(page: Int, sliderCard: SliderCardUi) {
        val lastPage = this.lastPage
        if (lastPage != null) {
            val difference = abs(page - lastPage)
            if (difference >= 1) {
                undoCards.clear()
            }
        }
        undoCards.add(sliderCard)
        this.lastPage = page
    }
}