package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.CardLearningHistory

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardLearningHistoryKtxDao: BaseKtxDao<CardLearningHistory> {

    @Query("SELECT h.* FROM Core_CardLearningHistory h " +
            "LEFT JOIN Core_CardLearningProgress p ON p.cardLearningHistoryId = h.id " +
            "WHERE p.cardId=:cardId")
    abstract suspend fun findCurrentByCardId(cardId: Int): CardLearningHistory?
}