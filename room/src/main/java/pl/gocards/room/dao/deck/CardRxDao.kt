package pl.gocards.room.dao.deck

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gocards.room.dao.BaseSyncRxDao
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.util.TimeUtil
import java.util.*
import java.util.stream.Collectors

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
abstract class CardRxDao : BaseSyncRxDao<Card>() {

    /* -----------------------------------------------------------------------------------------
     * Count
     * ----------------------------------------------------------------------------------------- */
    @Query("SELECT count(*) FROM Core_Card WHERE deletedAt is NULL")
    abstract fun countByNotDeleted(): Maybe<Int>

    @Query("SELECT max(ordinal) FROM Core_Card c WHERE deletedAt IS NULL")
    protected abstract fun lastOrdinal(): Int

    @Query("SELECT max(id) FROM Core_Card c")
    abstract fun lastId(): Maybe<Int>

    /* -----------------------------------------------------------------------------------------
     * Read
     * ----------------------------------------------------------------------------------------- */
    @Query("SELECT * FROM Core_Card WHERE id=:id")
    abstract fun getCard(id: Int): Maybe<Card>

    @Query("SELECT * FROM Core_Card WHERE id=:id")
    abstract fun getCardSync(id: Int): Card

    @Query("SELECT * FROM Core_Card WHERE deletedAt IS NULL ORDER BY ordinal ASC")
    abstract fun getAllCards(): Maybe<List<Card>>

    @Query(
        "SELECT c.*, " +
                "CASE WHEN t.term IS NOT NULL THEN t.term ELSE c.term END AS term, " +
                "CASE WHEN d.definition IS NOT NULL THEN d.definition ELSE c.term END AS definition " +
                "FROM Core_Card c " +
                "LEFT JOIN (SELECT id, snippet(Core_Card_fts4, '{search}', '{esearch}') as term FROM Core_Card_fts4 WHERE term MATCH '*' || :search || '*') t USING (id) " +
                "LEFT JOIN (SELECT id, snippet(Core_Card_fts4, '{search}', '{esearch}') as definition FROM Core_Card_fts4 WHERE definition MATCH '*' || :search || '*') d USING (id) " +
                "WHERE " +
                "deletedAt IS NULL " +
                "AND (t.id IS NOT NULL OR d.id IS NOT NULL)  " +
                "ORDER BY ordinal ASC"
    )
    abstract fun searchCards(search: String): Maybe<List<Card>>

    @Query("SELECT * FROM Core_Card WHERE id IN (:ids) ORDER BY ordinal")
    abstract fun findByIds(ids: IntArray): Maybe<List<Card>>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND createdAt=:createdAt")
    abstract fun findIdsByCreatedAt(createdAt: Long): Maybe<List<Int>>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND createdAt!=:updatedAt AND updatedAt=:updatedAt")
    abstract fun findIdsByModifiedAtAndCreatedAtNot(updatedAt: Long): Maybe<List<Int>>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND fileSyncCreatedAt=:fileSyncCreatedAt")
    abstract fun findIdsByFileSyncCreatedAt(fileSyncCreatedAt: Long): Maybe<List<Int>>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND disabled=1")
    abstract fun findIdsByDisabledTrueCards(): Maybe<List<Int>>

    @Query(
        "SELECT id FROM Core_Card " +
                "WHERE " +
                "deletedAt IS NULL " +
                "AND (fileSyncCreatedAt!=:fileSyncModifiedAt OR fileSyncCreatedAt IS NULL) " +
                "AND fileSyncModifiedAt=:fileSyncModifiedAt"
    )
    abstract fun findIdsByFileSyncModifiedAtAndFileSyncCreatedAtNot(fileSyncModifiedAt: Long): Maybe<List<Int>>

    @Query("SELECT ordinal FROM Core_Card c WHERE id = :id")
    abstract fun getOrdinal(id: Int): Maybe<Int>

    /* -----------------------------------------------------------------------------------------
     * Create
     * ----------------------------------------------------------------------------------------- */

    @Transaction
    protected open fun insertAtEndSync(card: Card) {
        card.ordinal = lastOrdinal() + 1
        val now = TimeUtil.getNowEpochSec()
        card.updatedAt = now
        card.createdAt = now
        insertAllSync(card)
    }


    @Transaction
    protected open fun insertAfterSync(card: Card, ordinal: Int) {
        increaseOrdinalByGreaterThanEqual(ordinal)
        card.ordinal = ordinal
        card.updatedAt = TimeUtil.getNowEpochSec()
        insertAllSync(card)
    }

    fun insertAfter(card: Card, afterOrdinal: Int): Completable {
        return Completable.fromAction { insertAfterSync(card, afterOrdinal) }
    }

    /* -----------------------------------------------------------------------------------------
     * Update - change the order of the cards
     * ----------------------------------------------------------------------------------------- */

    private fun decreaseOrdinalByBetween(greaterThan: Int, lessThanEqual: Int) {
        return decreaseOrdinalByBetween(1, greaterThan, lessThanEqual)
    }

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal-:decrease " +
                "WHERE deletedAt IS NULL AND ordinal>:greaterThan AND ordinal<=:lessThanEqual"
    )
    protected abstract fun decreaseOrdinalByBetween(
        decrease: Int,
        greaterThan: Int,
        lessThanEqual: Int
    )

    private fun decreaseOrdinalByGreaterThan(greaterThan: Int) {
        decreaseOrdinalByGreaterThan(1, greaterThan)
    }

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal-:decrease " +
                "WHERE deletedAt IS NULL AND ordinal>:greaterThan"
    )
    protected abstract fun decreaseOrdinalByGreaterThan(decrease: Int, greaterThan: Int)

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal+1 " +
                "WHERE deletedAt IS NULL AND ordinal>=:greaterThanEqual AND ordinal<:lessThan"
    )
    protected abstract fun increaseOrdinalByBetween(greaterThanEqual: Int, lessThan: Int)

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal+1 " +
                "WHERE deletedAt IS NULL AND ordinal>=:greaterThanEqual"
    )
    protected abstract fun increaseOrdinalByGreaterThanEqual(greaterThanEqual: Int)

    @Transaction
    open fun changeCardOrdinalSync(card: Card, newOrdinal: Int) {
        if (card.ordinal == newOrdinal) {
            throw UnsupportedOperationException("Move to the same place.")
        } else if (newOrdinal > card.ordinal) {
            decreaseOrdinalByBetween(card.ordinal, newOrdinal)
            card.ordinal = newOrdinal
        } else {
            increaseOrdinalByBetween(newOrdinal, card.ordinal)
            card.ordinal = newOrdinal
        }
        card.updatedAt = TimeUtil.getNowEpochSec()
        updateAllSync(card)
    }

    fun changeCardOrdinal(card: Card, afterOrdinal: Int): Completable {
        return Completable.fromAction { changeCardOrdinalSync(card, afterOrdinal) }
    }

    @Transaction
    open fun pasteCards(selectedCards: Set<Card>, pasteAfterPosition: Int) {
        val sortedByOrdinal: List<Card> = selectedCards.stream()
            .sorted(Comparator.comparing(Card::ordinal))
            .collect(Collectors.toList())

        var pasteAfterPositionVar = pasteAfterPosition

        for (cutCard in sortedByOrdinal) {
            // Refresh as the ordinal might have changed in previous iteration this loop.
            val freshCutCard = getCardSync(cutCard.id!!)
            val cutCardOrdinal = freshCutCard.ordinal
            // Paste after the clicked card.
            if (cutCardOrdinal > pasteAfterPositionVar) {
                pasteAfterPositionVar++
            }
            if (cutCardOrdinal == pasteAfterPositionVar) {
                continue
            }
            changeCardOrdinalSync(freshCutCard, pasteAfterPositionVar)
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Delete
     * ----------------------------------------------------------------------------------------- */

    @Query(
        "UPDATE Core_Card " +
                "SET deletedAt=strftime('%s', CURRENT_TIMESTAMP) " +
                "WHERE id=:cardId"
    )
    protected abstract fun deleteSync(cardId: Int)


    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    fun delete(card: Card): Completable {
        return Completable.fromAction { deleteSync(card) }
            .subscribeOn(Schedulers.io())
    }

    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    @Transaction
    protected open fun deleteSync(card: Card) {
        card.deletedAt = TimeUtil.getNowEpochSec()
        updateSync(card)
        decreaseOrdinalByGreaterThan(card.ordinal)
    }

    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    fun delete(cards: Collection<Card>): Completable {
        return Completable.fromAction { deleteSync(cards) }
    }

    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    @Transaction
    protected open fun deleteSync(cards: Collection<Card>) {
        val sorted = cards.stream()
            .sorted(Comparator.comparing(Card::ordinal))
            .collect(Collectors.toList())

        var card1: Card = sorted.removeAt(0)
        var deleted = 1
        for (card2 in sorted) {
            deleteSync(card1.id!!)
            decreaseOrdinalByBetween(deleted++, card1.ordinal, card2.ordinal)
            card1 = card2
        }
        deleteSync(card1.id!!)
        decreaseOrdinalByGreaterThan(deleted, card1.ordinal)
    }

    @Query("DELETE FROM Core_Card")
    abstract fun deleteAll(): Completable

    /* -----------------------------------------------------------------------------------------
     * Restore
     * ----------------------------------------------------------------------------------------- */

    /**
     * Restore and refresh ordinal numbers.
     */
    fun restore(card: Card): Completable {
        return Completable.fromAction { restoreSync(card) }
            .subscribeOn(Schedulers.io())
    }

    /**
     * Restore and refresh ordinal numbers.
     */
    @Transaction
    protected open fun restoreSync(card: Card) {
        increaseOrdinalByGreaterThanEqual(card.ordinal)
        card.deletedAt = null
        updateSync(card)
    }

    /**
     * Restore and refresh ordinal numbers.
     */
    fun restore(cards: Set<Card>): Completable {
        return Completable.fromAction { restoreSync(cards) }
    }

    /**
     * Restore and refresh ordinal numbers.
     */
    @Transaction
    protected open fun restoreSync(cards: Set<Card>) {
        val sorted = cards.stream()
            .sorted(Comparator.comparing(Card::ordinal))
            .collect(Collectors.toList())
        for (card in sorted) {
            card.deletedAt = null
            increaseOrdinalByGreaterThanEqual(card.ordinal)
            updateAll(card)
        }
    }
}