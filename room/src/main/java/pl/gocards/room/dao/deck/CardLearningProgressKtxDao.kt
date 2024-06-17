package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.CardLearningProgress

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
interface CardLearningProgressKtxDao : BaseKtxDao<CardLearningProgress> {

    /* -----------------------------------------------------------------------------------------
     * Count
     * ----------------------------------------------------------------------------------------- */

    @Query(
        "SELECT count(*) FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress p ON p.cardId = c.id  " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
                "WHERE " +
                "c.deletedAt IS NULL " +
                "AND c.disabled = 0 " +
                "AND (h.id IS NULL)"
    )
    suspend fun countByNew(): Int

    @Query(
        "SELECT count(*) FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress p ON p.cardId = c.id  " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
                "WHERE " +
                "c.deletedAt IS NULL " +
                "AND c.disabled = 0 " +
                "AND h.id IS NOT NULL AND (nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP) OR nextReplayAt IS NULL)"
    )
    suspend fun countByForgotten(): Int

    @Query("SELECT 1 WHERE EXISTS(SELECT 1 FROM Core_CardLearningProgress WHERE cardId=:cardId LIMIT 1)")
    suspend fun exists(cardId: Int): Boolean

    @Query(
        "UPDATE Core_CardLearningProgress SET " +
                "cardLearningHistoryId=:cardLearningHistoryId, " +
                "isMemorized=:isMemorized " +
                "WHERE cardId=:cardId"
    )
    suspend fun update(cardId: Int, cardLearningHistoryId: Int, isMemorized: Boolean)

    @Query(
        "UPDATE Core_CardLearningProgress SET " +
                "isMemorized=:isMemorized " +
                "WHERE cardId=:cardId"
    )
    suspend fun updateIsMemorized(cardId: Int, isMemorized: Boolean)

    @Query("DELETE FROM Core_CardLearningProgress WHERE cardId=:cardId")
    suspend fun deleteByCardId(cardId: Int)

    /* -----------------------------------------------------------------------------------------
     * Searching
     * ----------------------------------------------------------------------------------------- */

    @Query(
        "SELECT h.cardId FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress p ON p.cardId = c.id  " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
                "WHERE " +
                "c.deletedAt IS NULL " +
                "AND c.disabled = 0 " +
                "AND nextReplayAt > strftime('%s', CURRENT_TIMESTAMP)"
    )
    suspend fun findCardIdsByRemembered(): List<Int>

    @Query(
        "SELECT h.cardId FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress p ON p.cardId = c.id  " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
                "WHERE " +
                "c.deletedAt IS NULL " +
                "AND c.disabled = 0 " +
                "AND h.id IS NOT NULL AND (nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP) OR nextReplayAt IS NULL)"
    )
    suspend fun findCardIdsByForgotten(): List<Int>
}