package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import pl.gocards.room.dao.BaseRxDao
import pl.gocards.room.entity.deck.CardLearningProgress

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
interface CardLearningProgressRxDao: BaseRxDao<CardLearningProgress> {

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
                "AND (h.cardId IS NULL OR nextReplayAt IS NULL)"
    )
    fun countByNew(): Maybe<Int>

    @Query(
        "SELECT count(*) FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress l ON l.cardId = c.id  " +
                "LEFT JOIN Core_CardLearningHistory p ON p.id = l.cardLearningHistoryId " +
                "WHERE " +
                "c.deletedAt IS NULL " +
                "AND c.disabled = 0 " +
                "AND nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP)"
    )
    fun countByForgotten(): Maybe<Int>

    /* -----------------------------------------------------------------------------------------
     * Read
     * ----------------------------------------------------------------------------------------- */

    @Query("SELECT h.cardId FROM Core_CardLearningProgress p " +
            "JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
            "WHERE nextReplayAt > strftime('%s', CURRENT_TIMESTAMP)")
    fun findCardIdsByRemembered(): Maybe<List<Int>>

    @Query("SELECT h.cardId FROM Core_CardLearningProgress p " +
            "JOIN Core_CardLearningHistory h ON h.id = p.cardLearningHistoryId " +
            "WHERE nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP)")
    fun findCardIdsByForgotten(): Maybe<List<Int>>
}