package pl.gocards.db.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.gocards.room.dao.app.AppConfigDao
import pl.gocards.room.dao.app.AppConfigKtxDao
import pl.gocards.room.dao.app.DeckDao
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.room.entity.app.Deck
import pl.gocards.db.Converters
import pl.gocards.room.dao.app.DeckKtxDao
import pl.gocards.room.dao.app.DeckRxDao

/**
 * @author Grzegorz Ziemski
 */
@Database(
    entities = [
        AppConfig::class,
        Deck::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appConfigKtxDao(): AppConfigKtxDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun deckRxDao(): DeckRxDao
    abstract fun deckKtxDao(): DeckKtxDao
    abstract fun deckDao(): DeckDao
}