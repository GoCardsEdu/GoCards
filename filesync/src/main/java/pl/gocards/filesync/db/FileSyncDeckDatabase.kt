package pl.gocards.filesync.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.gocards.db.Converters
import pl.gocards.room.entity.deck.*
import pl.gocards.room.entity.filesync.*
import pl.gocards.filesync.dao.CardDao
import pl.gocards.filesync.dao.FileSyncedDao

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
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class FileSyncDeckDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun fileSyncedDao(): FileSyncedDao
}