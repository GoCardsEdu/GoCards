package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.deck.CardLearningHistory

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
interface CardLearningHistoryDao: BaseDao<CardLearningHistory> {

    @Query("SELECT * FROM Core_CardLearningHistory WHERE cardId=:cardId AND replayId=:replayId")
    fun findByCardIdAndLearningHistoryId(cardId: Int, replayId: Int): CardLearningHistory?
}