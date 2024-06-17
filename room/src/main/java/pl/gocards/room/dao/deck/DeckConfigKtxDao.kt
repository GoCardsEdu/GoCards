package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.room.util.TimeUtil

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

    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    abstract suspend fun getStringByKey(key: String): String?

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

    suspend fun getListMaxLine(): Int {
        return getLongByKey(DeckConfig.MAX_LINES)?.toInt() ?: DeckConfig.MAX_LINES_DEFAULT
    }

    @Transaction
    open suspend fun lockDeckEditing(): Boolean {
        val blockedAt = getLongByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
        return if (blockedAt == null) {
            val deckConfig = DeckConfig(
                DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT,
                TimeUtil.getNowEpochSec().toString()
            )
            insert(deckConfig)
            true
        } else {
            false
        }
    }
}