package pl.softfly.flashcards.dao.deck;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import pl.softfly.flashcards.entity.deck.CardLearningProgress;

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
public interface CardLearningProgressAsyncDao {

    @NonNull
    @Query("SELECT count(*) FROM Core_Card c " +
            "LEFT JOIN Core_CardLearningProgress l ON l.cardId = c.id  " +
            "WHERE " +
            "c.deletedAt IS NULL " +
            "AND c.disabled = 0 " +
            "AND (l.cardId IS NULL OR l.nextReplayAt IS NULL)"
    )
    Maybe<Integer> countByNew();

    @NonNull
    @Query("SELECT count(*) FROM Core_Card c " +
            "LEFT JOIN Core_CardLearningProgress l ON l.cardId = c.id  " +
            "WHERE " +
            "c.deletedAt IS NULL " +
            "AND c.disabled = 0 " +
            "AND nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP)"
    )
    Maybe<Integer> countByForgotten();

    @NonNull
    @Query("SELECT * FROM Core_CardLearningProgress WHERE cardId=:cardId")
    Maybe<CardLearningProgress> findByCardId(Integer cardId);

    @NonNull
    @Query("SELECT cardId FROM Core_CardLearningProgress WHERE nextReplayAt > strftime('%s', CURRENT_TIMESTAMP)")
    Maybe<List<Integer>> findCardIdsByRemembered();

    @NonNull
    @Query("SELECT cardId FROM Core_CardLearningProgress WHERE nextReplayAt <= strftime('%s', CURRENT_TIMESTAMP)")
    Maybe<List<Integer>> findCardIdsByForgotten();

    @NonNull
    @Insert
    Completable insertAll(CardLearningProgress... cards);

    @NonNull
    @Update
    Completable updateAll(CardLearningProgress... cardLearningProgress);
}
