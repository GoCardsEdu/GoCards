package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.deck.Card

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference", "AndroidUnresolvedRoomSqlReference",
    "AndroidUnresolvedRoomSqlReference", "AndroidUnresolvedRoomSqlReference"
)
abstract class CardDao: BaseDao<Card> {

    /* -----------------------------------------------------------------------------------------
     * Count
     * ----------------------------------------------------------------------------------------- */
    @Query("SELECT count(*) FROM Core_Card c WHERE deletedAt IS NULL")
    abstract fun count(): Int

    @Query("SELECT max(ordinal) FROM Core_Card")
    abstract fun maxOrdinal(): Int

    /* -----------------------------------------------------------------------------------------
     * Read
     * ----------------------------------------------------------------------------------------- */

    @Query("SELECT * FROM Core_Card WHERE deletedAt IS NULL")
    abstract fun getCards(): List<Card>

    @Query("SELECT * FROM Core_Card WHERE deletedAt IS NULL ORDER BY ordinal ASC")
    abstract fun getCardsOrderByOrdinalAsc(): List<Card>

    @Query(
        "SELECT * FROM Core_Card WHERE " +
                "deletedAt IS NULL " +
                "AND ordinal > :ordinalGreaterThan " +
                "ORDER BY ordinal ASC LIMIT 100"
    )
    abstract fun getCardsOrderByOrdinalAsc(ordinalGreaterThan: Int): List<Card>

    @Query("SELECT * FROM Core_Card WHERE deletedAt IS NULL ORDER BY ordinal ASC LIMIT 1")
    abstract fun getFirst(): Card?

    @Query("SELECT * FROM Core_Card WHERE id=:id")
    abstract fun findById(id: Int): Card

    @Query("SELECT * FROM Core_Card WHERE ordinal=:ordinal")
    abstract fun findByOrdinal(ordinal: Int): Card

    /* -----------------------------------------------------------------------------------------
     * Delete
     * ----------------------------------------------------------------------------------------- */

    @Query("DELETE FROM Core_Card WHERE deletedAt IS NOT NULL")
    abstract fun purgeDeleted()

    @Query("DELETE FROM Core_Card WHERE id = :id")
    abstract fun forceDeleteById(id: Int)
}