package pl.gocards.ui.cards.kt.list.edge_bar.filesync

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
 * C_R_06 Show card last sync status: added, updated on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
class FileSyncEdgeBarViewModel private constructor(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    application: Application
) : AndroidViewModel(application) {

    var recentlyAddedCards: Set<Int> = emptySet()

    var recentlyUpdatedCards: Set<Int> = emptySet()

    var recentlyAddedFileCards: Set<Int> = emptySet()

    var recentlyUpdatedFileCards: Set<Int> = emptySet()

    var leftEdgeBar: String = AppConfig.LEFT_EDGE_BAR_DEFAULT

    var rightEdgeBar: String = AppConfig.RIGHT_EDGE_BAR_DEFAULT

    init {
        load()
    }

    fun load(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            loadLeftEdgeBarConfig()
            loadRightEdgeBarConfig()
            if (isShownRecentlySynced()) {
                val modifiedAt = getDeckModifiedAt()
                if (modifiedAt != null) {
                    loadRecentlyAddedDeckCards(modifiedAt)
                    loadRecentlyUpdatedDeckCards(modifiedAt)
                    loadRecentlyAddedFileCards(modifiedAt)
                    loadRecentlyUpdatedFileCards(modifiedAt)
                }
            }
            onSuccess()
        }
    }

    private fun isShownRecentlySynced(): Boolean {
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

    private suspend fun getDeckModifiedAt(): Long? {
        return deckDb.fileSyncedKtxDao().findDeckModifiedAt()
    }

    private suspend fun loadRecentlyAddedDeckCards(createdAt: Long) {
        recentlyAddedCards = deckDb.cardKtxDao()
            .findIdsByCreatedAt(createdAt)
            .toSet()
    }

    private suspend fun loadRecentlyUpdatedDeckCards(updatedAt: Long) {
        recentlyUpdatedCards = deckDb.cardKtxDao()
            .findIdsByModifiedAtAndCreatedAtNot(updatedAt)
            .toSet()
    }

    private suspend fun loadRecentlyAddedFileCards(fileSyncCreatedAt: Long) {
        recentlyAddedFileCards = deckDb.cardKtxDao()
            .findIdsByFileSyncCreatedAt(fileSyncCreatedAt)
            .toSet()
    }

    private suspend fun loadRecentlyUpdatedFileCards(fileSyncModifiedAt: Long) {
        recentlyUpdatedFileCards = deckDb.cardKtxDao()
            .findIdsByFileSyncModifiedAtAndFileSyncCreatedAtNot(fileSyncModifiedAt)
            .toSet()
    }

    companion object {

        fun getInstance(
            context: Context,
            deckDbPath: String,
        ): FileSyncEdgeBarViewModel {
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
        ): FileSyncEdgeBarViewModel {
            return FileSyncEdgeBarViewModel(
                deckDb,
                appDb,
                application
            )
        }
    }
}