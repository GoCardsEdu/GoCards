package pl.gocards.ui.cards.slider.slider.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase

/**
 * @author Grzegorz Ziemski
 */
class SliderCardsModel(
    deckDb: DeckDatabase,
    appDb: AppDatabase,
    application: Application
): MaxForgottenSliderCardsModel(deckDb, appDb, application)

class SliderCardsModelFactory(
    private val deckDb: DeckDatabase,
    private val appDb: AppDatabase,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SliderCardsModel::class.java)) {
            SliderCardsModel(deckDb, appDb, application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}