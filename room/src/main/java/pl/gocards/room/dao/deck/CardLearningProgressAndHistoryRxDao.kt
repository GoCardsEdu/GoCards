/*
 * Copyright (c) 2023. Grzegorz Ziemski
 */

package pl.gocards.room.dao.deck

import androidx.room.*
import io.reactivex.rxjava3.core.Maybe
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardLearningProgressAndHistoryRxDao {

    @Transaction
    @Query("SELECT * FROM Core_CardLearningProgress p WHERE p.cardId=:cardId")
    abstract fun findCurrentByCardId(cardId: Int): Maybe<CardLearningProgressAndHistory>
}