package pl.gocards.room.dao.deck

import androidx.room.*
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.deck.DeckConfig

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class DeckConfigDao: BaseDao<DeckConfig> {

    @Query("DELETE FROM Core_DeckConfig WHERE `key`=:key")
    abstract fun deleteByKey(key: String)
}