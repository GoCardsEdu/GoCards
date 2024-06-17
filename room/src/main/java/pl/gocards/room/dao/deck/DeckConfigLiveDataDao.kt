package pl.gocards.room.dao.deck

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.entity.deck.DeckConfig

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
interface DeckConfigLiveDataDao {

    @Query("SELECT * FROM Core_DeckConfig WHERE `key`=:key")
    fun findByKey(key: String): LiveData<DeckConfig>

    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    fun getLongByKey(key: String): LiveData<Long>
}