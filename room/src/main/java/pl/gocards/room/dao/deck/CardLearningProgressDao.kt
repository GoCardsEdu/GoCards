package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.deck.CardLearningProgress

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
abstract class CardLearningProgressDao : BaseDao<CardLearningProgress> {

    @Query("SELECT 1 WHERE EXISTS(SELECT 1 FROM Core_CardLearningProgress WHERE cardId=:cardId LIMIT 1)")
    abstract fun exists(cardId: Int): Boolean

    @Query("UPDATE Core_CardLearningProgress SET " +
            "cardLearningHistoryId=:cardLearningHistoryId, " +
            "isMemorized=:isMemorized " +
            "WHERE cardId=:cardId")
    abstract fun update(cardId: Int, cardLearningHistoryId: Int, isMemorized: Boolean)

    @Query("UPDATE Core_CardLearningProgress SET " +
            "isMemorized=:isMemorized " +
            "WHERE cardId=:cardId")
    abstract fun updateIsMemorized(cardId: Int, isMemorized: Boolean)

    @Query("DELETE FROM Core_CardLearningProgress WHERE cardId=:cardId")
    abstract fun deleteByCardId(cardId: Int)
}