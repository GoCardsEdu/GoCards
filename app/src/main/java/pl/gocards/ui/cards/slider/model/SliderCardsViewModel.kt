package pl.gocards.ui.cards.slider.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import pl.gocards.App
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.ui.cards.slider.page.add.model.NewCardsModel
import pl.gocards.ui.cards.slider.page.add.model.NewCardsModelFactory
import pl.gocards.ui.cards.slider.page.edit.model.EditCardsModel
import pl.gocards.ui.cards.slider.page.edit.model.EditCardsModelFactory
import pl.gocards.ui.cards.slider.page.study.model.StudyCardsModel
import pl.gocards.ui.cards.slider.slider.model.Mode
import pl.gocards.ui.cards.slider.slider.model.SliderCardsModel
import pl.gocards.ui.cards.slider.slider.model.SliderCardsModelFactory

/**
 * @author Grzegorz Ziemski
 */
class SliderCardsViewModel(
    defaultMode: Mode,
    deckDb: DeckDatabase,
    sliderCardsModel: SliderCardsModel,
    studyCardsModel: StudyCardsModel,
    newCardsModel: NewCardsModel,
    editCardsModel: EditCardsModel,
    application: Application
) : LearningProgressViewModel(
    defaultMode,
    deckDb,
    sliderCardsModel,
    studyCardsModel,
    newCardsModel,
    editCardsModel,
    application
) {
    companion object {

        fun getInstance(
            context: Context,
            deckDbPath: String,
            defaultMode: Mode,
            viewModelStoreOwner: ViewModelStoreOwner
        ): SliderCardsViewModel {
            val application = context.applicationContext as App

            val deckDb = AppDeckDbUtil.getInstance(context).getDatabase(context, deckDbPath)
            val appDb = AppDbUtil.getInstance(context).getDatabase(context)

            val sliderCardsModelFactory = SliderCardsModelFactory(deckDb, appDb, application)
            val sliderCardsModel = ViewModelProvider(viewModelStoreOwner, sliderCardsModelFactory)[SliderCardsModel::class.java]

            val newCardsModelFactory = NewCardsModelFactory(deckDb, appDb, deckDbPath, application)
            val newCardsModel = ViewModelProvider(viewModelStoreOwner, newCardsModelFactory)[NewCardsModel::class.java]

            val editCardsModelFactory = EditCardsModelFactory(deckDb, appDb, deckDbPath, application)
            val editCardsModel = ViewModelProvider(viewModelStoreOwner, editCardsModelFactory)[EditCardsModel::class.java]


            return SliderCardsViewModel(
                defaultMode,
                deckDb,
                sliderCardsModel,
                StudyCardsModel(deckDb, appDb, deckDbPath),
                newCardsModel,
                editCardsModel,
                application
            )
        }
    }
}