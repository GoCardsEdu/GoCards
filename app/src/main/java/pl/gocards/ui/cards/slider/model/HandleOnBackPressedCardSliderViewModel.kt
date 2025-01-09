package pl.gocards.ui.cards.slider.model

import android.app.Application
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.util.TimeUtil
import pl.gocards.ui.cards.slider.page.add.model.NewCardManager
import pl.gocards.ui.cards.slider.page.card.model.CardMode
import pl.gocards.ui.cards.slider.page.card.model.SliderCardManager
import pl.gocards.ui.cards.slider.page.card.model.SliderCardUi
import pl.gocards.ui.cards.slider.page.edit.model.EditCardManager
import pl.gocards.ui.cards.slider.page.study.model.StudyCardManager
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * @author Grzegorz Ziemski
 */
open class HandleOnBackPressedCardSliderViewModel(
    defaultMode: CardMode,
    deckDb: DeckDatabase,
    sliderCardManager: SliderCardManager,
    studyCardManager: StudyCardManager?,
    newCardManager: NewCardManager,
    editCardManager: EditCardManager,
    analytics: FirebaseAnalyticsHelper,
    application: Application
) : UndoLearningProgressCardSliderViewModel(
    defaultMode,
    deckDb,
    sliderCardManager,
    studyCardManager,
    newCardManager,
    editCardManager,
    analytics,
    application
) {

    fun handleOnBackPressed(): Boolean {
        val currentPage = sliderCardManager.getSettledPage() ?: return false
        val currentCard = sliderCardManager.getItem(currentPage)

        return when (currentCard.cardMode.value) {
            CardMode.EDIT -> {
                handleEditMode(currentPage, currentCard)
            }

            CardMode.NEW -> {
                handleNewModeBackPress(currentPage, currentCard)
                true
            }

            else -> {
                processUndoCardsStack(currentPage, currentCard)
            }
        }
    }

    private fun handleEditMode(currentPage: Int, currentCard: SliderCardUi): Boolean {
        return when (defaultMode) {
            CardMode.STUDY -> {
                switchToStudyMode(currentPage, currentCard)
                true
            }

            CardMode.EDIT -> {
                processUndoCardsStack(currentPage, currentCard)
            }

            else -> false
        }
    }

    private fun handleNewModeBackPress(currentPage: Int, currentCard: SliderCardUi) {
        sliderCardManager.deleteAndSlideToPrevious(currentPage, currentCard)
        analytics.sliderDeleteNewCard(currentPage)
    }

    private fun switchToStudyMode(currentPage: Int, currentCard: SliderCardUi) {
        currentCard.cardMode.value = CardMode.STUDY
        analytics.sliderStudyMode(currentPage)
    }

    private fun processUndoCardsStack(currentPage: Int, currentCard: SliderCardUi): Boolean {
        val backToCard: SliderCardUi = this.undoCards.pollLast() ?: return false
        return if (backToCard.deletedAt != null) {
            handleRestoreDeletedCard(backToCard, currentPage)
            true
        } else {
            revertLearningProgress(
                backToCard,
                currentPage,
                currentCard,
                sliderCardManager.getItems().size
            )
            true
        }
    }

    private fun handleRestoreDeletedCard(sliderCard: SliderCardUi, currentPage: Int) {
        sliderCard.deletedAt = null
        super.restoreDeletedCard(sliderCard.ordinal!! - 1, sliderCard)
        analytics.sliderRestoreCard(currentPage)
    }

    private fun revertLearningProgress(
        backToCard: SliderCardUi,
        currentPage: Int,
        currentCard: SliderCardUi,
        sliderCardsSize: Int
    ) {
        revertPreviousLearningProgress(
            backToCard,
            currentPage,
            currentCard,
            sliderCardsSize
        )
        analytics.revertLearningProgress(currentPage)
    }

    /**
     * C_D_25 Delete the card
     */
    override fun deleteCard(page: Int, sliderCard: SliderCardUi) {
        sliderCard.deletedAt = TimeUtil.getNowEpochSec()
        sliderCard.ordinal = page + 1
        undoCards.add(sliderCard)
        super.deleteCard(page, sliderCard)
    }

    /**
     * C_U_26 Undo card deletion
     */
    fun restoreDeletedCard(sliderCard: SliderCardUi, currentPage: Int) {
        if (!undoCards.remove(sliderCard)) return
        sliderCard.deletedAt = null
        super.restoreDeletedCard(sliderCard.ordinal!! - 1, sliderCard)
        analytics.sliderRestoreCard(currentPage)
    }
}