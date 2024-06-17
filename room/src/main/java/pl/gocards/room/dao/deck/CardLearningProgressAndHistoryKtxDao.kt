package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardLearningProgressAndHistoryKtxDao {

    @Transaction
    @Query("SELECT * FROM Core_CardLearningProgress p WHERE p.cardId=:cardId")
    abstract suspend fun findCurrentByCardId(cardId: Int): CardLearningProgressAndHistory?
}