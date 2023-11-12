package pl.gocards.ui.settings.model

import androidx.lifecycle.AndroidViewModel
import pl.gocards.App
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.ui.settings.model.app.DarkModeModel
import pl.gocards.ui.settings.model.all_decks.MaxForgottenCardsModel
import pl.gocards.ui.settings.model.all_decks.MaxLinesModel
import pl.gocards.ui.settings.model.all_decks.ShowLeftEdgeBarModel
import pl.gocards.ui.settings.model.all_decks.ShowRightEdgeBarModel
import pl.gocards.ui.settings.model.deck.DeckAutoSyncModel
import pl.gocards.ui.settings.model.deck.DeckMaxForgottenCardsModel
import pl.gocards.ui.settings.model.deck.DeckMaxLinesModel

/**
 * DS_R_01 Deck Settings
 * @author Grzegorz Ziemski
 */
class SettingsViewModel(
    appDb: AppDatabase,
    deckDb: DeckDatabase?,
    isSystemInDarkTheme: Boolean,
    application: App
) : AndroidViewModel(application) {

    var autoSync: DeckAutoSyncModel? = null
    var deckMaxForgottenCards: DeckMaxForgottenCardsModel? = null
    val appMaxForgottenCards: MaxForgottenCardsModel = MaxForgottenCardsModel(appDb, application)
    var deckMaxLines: DeckMaxLinesModel? = null
    val appMaxLines = MaxLinesModel(appDb, application)
    val showLeftEdgeBar = ShowLeftEdgeBarModel(appDb, application)
    val showRightEdgeBar = ShowRightEdgeBarModel(appDb, application)
    val darkMode: DarkModeModel = DarkModeModel(appDb, isSystemInDarkTheme, application)

    init {
        if (deckDb != null) {
            autoSync = DeckAutoSyncModel(deckDb, application)
            deckMaxForgottenCards = DeckMaxForgottenCardsModel(appDb, deckDb, application)
            deckMaxLines = DeckMaxLinesModel(appDb, deckDb, application)

            deckMaxForgottenCards!!.appMaxForgottenCards = appMaxForgottenCards
            appMaxForgottenCards.deckMaxForgottenCards = deckMaxForgottenCards as DeckMaxForgottenCardsModel
            deckMaxLines!!.appMaxLinesModel = appMaxLines
            appMaxLines.deckMaxLinesModel = deckMaxLines as DeckMaxLinesModel

            autoSync!!.init()
            deckMaxForgottenCards!!.init()
            deckMaxLines!!.init()
        }

        appMaxForgottenCards.init()
        appMaxLines.init()
        showLeftEdgeBar.init()
        showRightEdgeBar.init()
        darkMode.init()
    }
}