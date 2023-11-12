package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import pl.gocards.room.dao.BaseSyncRxDao
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.CardSlider

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
abstract class CardSliderRxDao : BaseSyncRxDao<Card>() {

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL ORDER BY ordinal ASC")
    abstract fun getAllCards(): Maybe<List<CardSlider>>

    @Query(
        "SELECT c.id FROM Core_Card c " +
                "LEFT JOIN Core_CardLearningProgress l ON l.cardId = c.id " +
                "LEFT JOIN Core_CardLearningHistory h ON h.id = l.cardLearningHistoryId " +
                "WHERE " +
                "c.disabled=0 " +
                "AND c.deletedAt IS NULL " +
                "AND (h.nextReplayAt < strftime('%s', CURRENT_TIMESTAMP) OR h.nextReplayAt IS NULL) " +
                "ORDER BY c.ordinal ASC, h.nextReplayAt ASC " +
                "LIMIT 100"
    )
    abstract fun getNextCardsToReplay(): Maybe<List<CardSlider>>
}