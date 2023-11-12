package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import pl.gocards.room.dao.BaseRxDao
import pl.gocards.room.entity.deck.CardLearningHistory

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardLearningHistoryRxDao: BaseRxDao<CardLearningHistory> {

    @Query("SELECT h.* FROM Core_CardLearningHistory h " +
            "LEFT JOIN Core_CardLearningProgress p ON p.cardLearningHistoryId = h.id " +
            "WHERE p.cardId=:cardId")
    abstract fun findCurrentByCardId(cardId: Int): Maybe<CardLearningHistory>

    fun insert(cardLearningHistory: CardLearningHistory): Single<Long> {
        return Single.fromCallable { insertSync(cardLearningHistory) }
    }
    
    @Insert
    abstract fun insertSync(cardLearningHistory: CardLearningHistory): Long
}