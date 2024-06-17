package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.CardSlider

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardSliderKtxDao : BaseKtxDao<Card> {

    @Query("SELECT c.id, c.ordinal FROM Core_Card c WHERE deletedAt IS NULL ORDER BY ordinal ASC")
    abstract suspend fun getAllCards(): List<CardSlider>

    @Query(
        "SELECT c.id, c.ordinal FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress l ON l.cardId = c.id " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = l.cardLearningHistoryId " +
                "WHERE " +
                "c.disabled=0 " +
                "AND c.deletedAt IS NULL " +
                "AND (h.nextReplayAt < strftime('%s', CURRENT_TIMESTAMP) OR h.nextReplayAt IS NULL) " +
                "ORDER BY c.ordinal ASC, h.nextReplayAt ASC " +
                "LIMIT 100"
    )
    abstract suspend fun getNextCardsToReplay(): List<CardSlider>
}