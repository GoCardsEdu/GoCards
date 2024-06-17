package pl.gocards.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.gocards.db.Converters
import pl.gocards.room.dao.deck.CardConfigKtxDao
import pl.gocards.room.dao.deck.CardConfigRxDao
import pl.gocards.room.dao.deck.CardDao
import pl.gocards.room.dao.deck.CardKtxDao
import pl.gocards.room.dao.deck.CardLearningHistoryDao
import pl.gocards.room.dao.deck.CardLearningHistoryKtxDao
import pl.gocards.room.dao.deck.CardLearningHistoryRxDao
import pl.gocards.room.dao.deck.CardLearningProgressAndHistoryKtxDao
import pl.gocards.room.dao.deck.CardLearningProgressAndHistoryRxDao
import pl.gocards.room.dao.deck.CardLearningProgressDao
import pl.gocards.room.dao.deck.CardLearningProgressKtxDao
import pl.gocards.room.dao.deck.CardLearningProgressRxDao
import pl.gocards.room.dao.deck.CardRxDao
import pl.gocards.room.dao.deck.CardSliderKtxDao
import pl.gocards.room.dao.deck.CardSliderRxDao
import pl.gocards.room.dao.deck.DeckConfigDao
import pl.gocards.room.dao.deck.DeckConfigKtxDao
import pl.gocards.room.dao.deck.DeckConfigLiveDataDao
import pl.gocards.room.dao.deck.DeckConfigRxDao
import pl.gocards.room.dao.filesync.FileSyncedBackupKtxDao
import pl.gocards.room.dao.filesync.FileSyncedKtxDao
import pl.gocards.room.dao.filesync.FileSyncedRxDao
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.CardConfig
import pl.gocards.room.entity.deck.CardFts
import pl.gocards.room.entity.deck.CardLearningHistory
import pl.gocards.room.entity.deck.CardLearningProgress
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.room.entity.filesync.CardEdge
import pl.gocards.room.entity.filesync.CardImported
import pl.gocards.room.entity.filesync.CardImportedRemoved
import pl.gocards.room.entity.filesync.CountToGraphOnlyNewCards
import pl.gocards.room.entity.filesync.FileSynced
import pl.gocards.room.entity.filesync.FileSyncedBackup
import pl.gocards.room.entity.filesync.GraphEdge
import pl.gocards.room.entity.filesync.GraphEdgeOnlyNewCards

/**
 * @author Grzegorz Ziemski
 */
@Database(
    entities = [
        Card::class,
        CardFts::class,
        CardLearningHistory::class,
        CardLearningProgress::class,
        CardConfig::class,
        DeckConfig::class,
        CardImported::class,
        CardEdge::class,
        FileSynced::class,
        FileSyncedBackup::class,
        CardImportedRemoved::class
    ],
    views = [
        GraphEdge::class,
        GraphEdgeOnlyNewCards::class,
        CountToGraphOnlyNewCards::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(Converters::class)
abstract class DeckDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun cardRxDao(): CardRxDao
    abstract fun cardKtxDao(): CardKtxDao
    abstract fun cardLearningProgressDao(): CardLearningProgressDao
    abstract fun cardLearningProgressRxDao(): CardLearningProgressRxDao
    abstract fun cardLearningProgressKtxDao(): CardLearningProgressKtxDao
    abstract fun cardLearningHistoryDao(): CardLearningHistoryDao
    abstract fun cardLearningHistoryRxDao(): CardLearningHistoryRxDao
    abstract fun cardLearningHistoryKtxDao(): CardLearningHistoryKtxDao
    abstract fun cardLearningProgressAndHistoryRxDao(): CardLearningProgressAndHistoryRxDao
    abstract fun cardLearningProgressAndHistoryKtxDao(): CardLearningProgressAndHistoryKtxDao
    abstract fun cardSliderRxDao(): CardSliderRxDao
    abstract fun cardSliderKtxDao(): CardSliderKtxDao
    abstract fun cardConfigRxDao(): CardConfigRxDao
    abstract fun deckConfigRxDao(): DeckConfigRxDao
    abstract fun cardConfigKtxDao(): CardConfigKtxDao
    abstract fun deckConfigDao(): DeckConfigDao
    abstract fun deckConfigKtxDao(): DeckConfigKtxDao
    abstract fun deckConfigLiveDataDao(): DeckConfigLiveDataDao
    abstract fun fileSyncedRxDao(): FileSyncedRxDao
    abstract fun fileSyncedKtxDao(): FileSyncedKtxDao
    abstract fun fileSyncedBackupKtxDao(): FileSyncedBackupKtxDao
}