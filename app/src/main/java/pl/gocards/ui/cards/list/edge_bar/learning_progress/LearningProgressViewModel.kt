package pl.gocards.ui.cards.list.edge_bar.learning_progress

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.app.AppConfig

/**
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
class LearningProgressViewModel private constructor(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    application: Application
): AndroidViewModel(application) {

    var disabledCards: Set<Int> = emptySet()

    var forgottenCards: Set<Int> = emptySet()

    var rememberedCards: Set<Int> = emptySet()

    var leftEdgeBar: String = AppConfig.LEFT_EDGE_BAR_DEFAULT

    var rightEdgeBar: String = AppConfig.RIGHT_EDGE_BAR_DEFAULT

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loadLeftEdgeBarConfig()
            loadRightEdgeBarConfig()
            if (isShownLearningStatus()) {
                loadCards()
            }
        }
    }

    fun loadCards(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isShownLearningStatus()) {
                loadDisabledCards()
                loadForgottenCards()
                loadRememberedCards()
            }
            onSuccess()
        }
    }

    private suspend fun loadLeftEdgeBarConfig() {
        val appConfig = appDb.appConfigKtxDao().getByKey(AppConfig.LEFT_EDGE_BAR)
        if (appConfig != null) {
            leftEdgeBar = appConfig.value
        }
    }

    private suspend fun loadRightEdgeBarConfig() {
        val appConfig = appDb.appConfigKtxDao().getByKey(AppConfig.RIGHT_EDGE_BAR)
        if (appConfig != null) {
            rightEdgeBar = appConfig.value
        }
    }

    private fun isShownLearningStatus(): Boolean {
        return AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS == leftEdgeBar
                || AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS == rightEdgeBar
    }

    private suspend fun loadDisabledCards() {
        disabledCards = deckDb.cardKtxDao().findIdsByDisabledTrueCards().toSet()
    }

    private suspend fun loadForgottenCards() {
        forgottenCards = deckDb.cardLearningProgressKtxDao().findCardIdsByForgotten().toSet()
    }

    private suspend fun loadRememberedCards() {
        rememberedCards = deckDb.cardLearningProgressKtxDao().findCardIdsByRemembered().toSet()
    }

    companion object {

        fun getInstance(
            context: Context,
            deckDbPath: String,
        ): LearningProgressViewModel {
            val deckDb = AppDeckDbUtil.getInstance(context).getDatabase(context, deckDbPath)
            val appDb = AppDbUtil.getInstance(context).getDatabase(context)
            return getInstance(
                deckDb,
                appDb,
                context.applicationContext as App
            )
        }

        fun getInstance(
            deckDb: DeckDatabase,
            appDb: AppDatabase,
            application: Application
        ): LearningProgressViewModel {
            return LearningProgressViewModel(
                deckDb,
                appDb,
                application
            )
        }
    }
}