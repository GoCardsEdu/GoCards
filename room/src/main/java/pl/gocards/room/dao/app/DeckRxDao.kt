package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import pl.gocards.room.dao.BaseSyncRxDao
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
abstract class DeckRxDao : BaseSyncRxDao<Deck>() {

    /* -----------------------------------------------------------------------------------------
     * Read
     * ----------------------------------------------------------------------------------------- */

    @Query("SELECT * FROM Deck WHERE path=:path")
    protected abstract fun findByKeySync(path: String): Deck?

    @Query("SELECT * FROM Deck WHERE path LIKE :folderPath || '%'")
    abstract fun findByFolder(folderPath: String): Maybe<List<Deck>>

    @Query("SELECT * FROM Deck ORDER BY lastUpdatedAt DESC LIMIT :limit")
    abstract fun findByLastUpdatedAt(limit: Int): Maybe<List<Deck>>

    /* -----------------------------------------------------------------------------------------
     * Delete
     * ----------------------------------------------------------------------------------------- */

    @Query("DELETE FROM Deck WHERE `path`=:path")
    abstract fun deleteByPath(path: String): Completable

    @Query("DELETE FROM Deck WHERE `path` LIKE '%' || :path || '%'")
    abstract fun deleteByStartWithPath(path: String): Completable

    /* -----------------------------------------------------------------------------------------
     * Update
     * ----------------------------------------------------------------------------------------- */

    @Query("UPDATE Deck SET path=:newPath WHERE path=:oldPath")
    abstract fun updatePathByPath(oldPath: String, newPath: String): Completable

    @Transaction
    protected open fun refreshLastUpdatedAtSync(path: String): Deck {
        val pathWithDb = DbUtil.addDbExtension(path)
        var deck = findByKeySync(pathWithDb)
        if (deck != null) {
            deck.lastUpdatedAt = TimeUtil.getNowEpochSec()
            updateSync(deck)
        } else {
            deck = Deck(
                name = DbUtil.getDeckName(pathWithDb),
                path = pathWithDb,
                lastUpdatedAt = TimeUtil.getNowEpochSec()
            )
            insertSync(deck)
        }
        return deck
    }

    open fun refreshLastUpdatedAt(path: String): Maybe<Deck> {
        return Maybe.fromAction { refreshLastUpdatedAtSync(path) }
    }
}