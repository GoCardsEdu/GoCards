package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.app.Deck
import pl.gocards.room.util.DbUtil
import pl.gocards.room.util.TimeUtil

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class DeckDao : BaseDao<Deck> {

    @Query("SELECT * FROM Deck WHERE path=:path")
    protected abstract fun findByKey(path: String): Deck?

    @Query("DELETE FROM Deck WHERE `path` LIKE '%' || :path || '%'")
    abstract fun deleteByStartWithPath(path: String)

    @Transaction
    open fun refreshLastUpdatedAt(path: String): Deck {
        val pathWithDb = DbUtil.addDbExtension(path)
        var deck = findByKey(pathWithDb)
        if (deck != null) {
            deck.lastUpdatedAt = TimeUtil.getNowEpochSec()
            update(deck)
        } else {
            deck = Deck(
                name = DbUtil.getDeckName(pathWithDb),
                path = pathWithDb,
                lastUpdatedAt = TimeUtil.getNowEpochSec()
            )
            insert(deck)
        }
        return deck
    }
}