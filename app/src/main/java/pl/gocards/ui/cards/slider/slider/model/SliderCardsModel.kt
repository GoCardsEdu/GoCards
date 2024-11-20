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
    application: Application,
    onScroll: (Int?, Int) -> Unit
): MaxForgottenSliderCardsModel(deckDb, appDb, application, onScroll)

class SliderCardsModelFactory(
    private val deckDb: DeckDatabase,
    private val appDb: AppDatabase,
    private val application: Application,
    private val onScroll: (Int?, Int) -> Unit
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SliderCardsModel::class.java)) {
            SliderCardsModel(deckDb, appDb, application, onScroll) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}