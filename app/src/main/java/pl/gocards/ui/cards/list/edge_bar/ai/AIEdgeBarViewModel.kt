package pl.gocards.ui.cards.list.edge_bar.ai

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
import pl.gocards.room.entity.deck.DeckConfig

/**
 * Show AI-created / AI-updated edge bar status.
 * @author Grzegorz Ziemski
 */
class AIEdgeBarViewModel private constructor(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    application: Application
) : AndroidViewModel(application) {

    var aiCreatedCards: Set<Int> = emptySet()

    var aiUpdatedCards: Set<Int> = emptySet()

    var leftEdgeBar: String = AppConfig.LEFT_EDGE_BAR_DEFAULT

    var rightEdgeBar: String = AppConfig.RIGHT_EDGE_BAR_DEFAULT

    init {
        load()
    }

    fun load(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            loadLeftEdgeBarConfig()
            loadRightEdgeBarConfig()
            if (isShownAIStatus()) {
                val updatedAt = getLastAIUpdatedAt()
                if (updatedAt != null) {
                    loadAICreatedCards(updatedAt)
                    loadAIUpdatedCards(updatedAt)
                }
            }
            onSuccess()
        }
    }

    private fun isShownAIStatus(): Boolean {
        return AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == leftEdgeBar
                || AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == rightEdgeBar
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

    private suspend fun getLastAIUpdatedAt(): Long? {
        return deckDb.deckConfigKtxDao().getLongByKey(DeckConfig.AI_LAST_UPDATED_AT)
    }

    private suspend fun loadAICreatedCards(updatedAt: Long) {
        aiCreatedCards = deckDb.cardKtxDao()
            .findIdsByCreatedAt(updatedAt)
            .toSet()
    }

    private suspend fun loadAIUpdatedCards(updatedAt: Long) {
        aiUpdatedCards = deckDb.cardKtxDao()
            .findIdsByModifiedAtAndCreatedAtNot(updatedAt)
            .toSet()
    }

    companion object {

        fun getInstance(
            context: Context,
            deckDbPath: String,
        ): AIEdgeBarViewModel {
            val deckDb = AppDeckDbUtil.getInstance(context).getDatabase(context, deckDbPath)
            val appDb = AppDbUtil.getInstance(context).getDatabase(context)
            return AIEdgeBarViewModel(
                deckDb,
                appDb,
                context.applicationContext as App
            )
        }
    }
}
