package pl.gocards.ui.cards.kt.slider.slider.model

import android.app.Application
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.DeckConfig

/**
 * @author Grzegorz Ziemski
 */
open class MaxForgottenSliderCardsModel(
    deckDb: DeckDatabase,
    appDb: AppDatabase,
    application: Application
): BaseSliderCardsModel(deckDb, appDb, application) {

    private var maxForgottenCards = DeckConfig.MAX_FORGOTTEN_CARDS_DEFAULT

    /**
     * If the number of unmemorized cards reaches {@link #maxForgottenCards}
     * then the stack of cards will be restarted and browsing will go back to the first unmemorized card.
     */
    private var countForgottenCards = 0

    suspend fun loadMaxForgottenCards() {
        val deckConfig = deckDb.deckConfigKtxDao().getByKey(DeckConfig.MAX_FORGOTTEN_CARDS)
        if (deckConfig != null) {
            maxForgottenCards = deckConfig.value.toInt()
            return
        }
        val appConfig = appDb.appConfigKtxDao().getByKey(DeckConfig.MAX_FORGOTTEN_CARDS)
        if (appConfig != null) {
            maxForgottenCards = appConfig.value.toInt()
        }
    }

    fun forgetAndSlideToNextCard(page: Int): Int {
        val exceededForgottenCards = (maxForgottenCards != 0 && countForgottenCards >= maxForgottenCards)
        return if (exceededForgottenCards) {
            countForgottenCards = 0
            slideToFirstPage()
        } else {
            countForgottenCards++
            slideToNextPage(page)
        }
    }

}