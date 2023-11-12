package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.DeckConfig

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class DeckConfigKtxDao: BaseKtxDao<DeckConfig> {

    @Query("SELECT * FROM Core_DeckConfig WHERE `key`=:key")
    abstract suspend fun getByKey(key: String): DeckConfig?

    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    abstract suspend fun getLongByKey(key: String): Long?

    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    abstract suspend fun getFloatByKey(key: String): Float?

    @Query("DELETE FROM Core_DeckConfig WHERE `key`=:key")
    abstract suspend fun deleteByKey(key: String)

    suspend fun update(
        key: String,
        value: String,
        defaultValue: String
    ) {
        if (value == defaultValue) {
            deleteByKey(key)
        } else {
            val deckConfig = getByKey(key)
            if (deckConfig == null) {
                insertAll(DeckConfig(key, value))
            } else {
                deckConfig.value = value
                updateAll(deckConfig)
            }
        }
    }
}