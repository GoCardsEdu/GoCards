package pl.gocards.ui.cards.slider.model

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.db.room.DeckDatabase
import pl.gocards.ui.cards.slider.page.add.model.NewCardsModel
import pl.gocards.ui.cards.slider.page.edit.model.EditCardsModel
import pl.gocards.ui.cards.slider.page.study.model.StudyCardsModel
import pl.gocards.ui.cards.slider.slider.model.Mode
import pl.gocards.ui.cards.slider.slider.model.SliderCardUi
import pl.gocards.ui.cards.slider.slider.model.SliderCardsModel

/**
 * @author Grzegorz Ziemski
 */
open class LearningProgressViewModel(
    defaultMode: Mode,
    deckDb: DeckDatabase,
    sliderCardsModel: SliderCardsModel,
    studyCardsModel: StudyCardsModel,
    newCardsModel: NewCardsModel,
    editCardsModel: EditCardsModel,
    application: Application
) : UndoSliderCardsViewModel(
    defaultMode,
    deckDb,
    sliderCardsModel,
    studyCardsModel,
    newCardsModel,
    editCardsModel,
    application
) {

    /**
     * C_U_32 Again
     */
    fun onAgainClick(page: Int, sliderCard: SliderCardUi) {
        clearUndoCards(page, sliderCard)
        viewModelScope.launch(Dispatchers.IO) {
            addForgottenInThisSession(sliderCard.id)
            studyCardsModel.onAgainClick(sliderCard)
        }
        sliderCardsModel.forgetAndSlideToNextCard(page)
    }

    private suspend fun addForgottenInThisSession(id: Int) {
        val studyCard = studyCardsModel.getCard(id)!!
        val current = studyCard.current
        if (current == null) {
            forgottenInThisSession.add(studyCard.nextAfterAgain.progress.cardId)
        } else if (current.progress.isMemorized) {
            forgottenInThisSession.add(current.progress.cardId)
        }
    }

    /**
     * C_U_33 Quick Repetition (5 min)
     */
    fun onQuickClick(page: Int, sliderCard: SliderCardUi) {
        clearUndoCards(page, sliderCard)
        viewModelScope.launch(Dispatchers.IO) {
            studyCardsModel.onQuickClick(sliderCard)
        }
        sliderCardsModel.deleteAndSlideToNextPage(page, sliderCard)
    }

    /**
     * C_U_35 Easy (5 days)
     */
    fun onEasyClick(page: Int, sliderCard: SliderCardUi) {
        clearUndoCards(page, sliderCard)
        viewModelScope.launch(Dispatchers.IO) {
            studyCardsModel.onEasyClick(sliderCard)
        }
        sliderCardsModel.deleteAndSlideToNextPage(page, sliderCard)
    }

    /**
     * C_U_34 Hard (3 days)
     */
    fun onHardClick(page: Int, sliderCard: SliderCardUi) {
        clearUndoCards(page, sliderCard)
        viewModelScope.launch(Dispatchers.IO) {
            studyCardsModel.onHardClick(sliderCard)
        }
        sliderCardsModel.deleteAndSlideToNextPage(page, sliderCard)
    }
}