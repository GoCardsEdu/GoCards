package pl.gocards.room.dao.app

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.app.Deck
import pl.gocards.room.util.DbUtil

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class DeckKtxDao : BaseKtxDao<Deck> {

    @Suppress("PrivatePropertyName")
    private val TAG = "DeckKtxDao"

    @Query("SELECT * FROM Deck WHERE path=:path")
    protected abstract suspend fun findByKey(path: String): Deck?

    @Query("SELECT * FROM Deck ORDER BY lastUpdatedAt DESC LIMIT :limit")
    abstract suspend fun findByLastUpdatedAt(limit: Int): List<Deck>

    @Query("DELETE FROM Deck WHERE `path` LIKE '%' || :path || '%'")
    abstract suspend fun deleteByStartWithPath(path: String)

    @Transaction
    open suspend fun refreshLastUpdatedAt(path: String, updatedAt: Long) {
        val pathWithDb = DbUtil.addDbExtension(path)
        var deck = findByKey(pathWithDb)
        if (deck != null) {
            deck.lastUpdatedAt = updatedAt
            updateAll(deck)
        } else {
            deck = Deck(
                name = DbUtil.getDeckName(pathWithDb),
                path = pathWithDb,
                lastUpdatedAt = updatedAt
            )
            insert(deck)
        }
        Log.i(TAG, String.format("Deck updated: lastUpdatedAt=%d", updatedAt))
    }
}