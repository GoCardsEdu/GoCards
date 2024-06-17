package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.util.TimeUtil
import java.util.stream.Collectors

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("AndroidUnresolvedRoomSqlReference")
abstract class CardKtxDao : BaseKtxDao<Card> {

    /* -----------------------------------------------------------------------------------------
     * Count
     * ----------------------------------------------------------------------------------------- */
    @Query("SELECT count(*) FROM Core_Card WHERE deletedAt is NULL")
    abstract suspend fun countByNotDeleted(): Int

    @Query("SELECT max(ordinal) FROM Core_Card c WHERE deletedAt IS NULL")
    protected abstract fun lastOrdinal(): Int

    @Query("SELECT max(id) FROM Core_Card c")
    abstract suspend fun lastId(): Int

    /* -----------------------------------------------------------------------------------------
     * Read
     * ----------------------------------------------------------------------------------------- */
    @Query("SELECT * FROM Core_Card WHERE id=:id")
    abstract suspend fun getCard(id: Int): Card?

    @Query("SELECT * FROM Core_Card WHERE id=:id")
    abstract fun getCardSync(id: Int): Card

    @Query("SELECT * FROM Core_Card WHERE deletedAt IS NULL ORDER BY ordinal ASC")
    abstract suspend fun getAllCards(): List<Card>

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
    abstract suspend fun searchCards(search: String): List<Card>

    @Query("SELECT * FROM Core_Card WHERE id IN (:ids) ORDER BY ordinal")
    abstract suspend fun findByIds(ids: IntArray): List<Card>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND createdAt=:createdAt")
    abstract suspend fun findIdsByCreatedAt(createdAt: Long): List<Int>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND createdAt!=:updatedAt AND updatedAt=:updatedAt")
    abstract suspend fun findIdsByModifiedAtAndCreatedAtNot(updatedAt: Long): List<Int>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND fileSyncCreatedAt=:fileSyncCreatedAt")
    abstract suspend fun findIdsByFileSyncCreatedAt(fileSyncCreatedAt: Long): List<Int>

    @Query("SELECT id FROM Core_Card WHERE deletedAt IS NULL AND disabled=1")
    abstract suspend fun findIdsByDisabledTrueCards(): List<Int>

    @Query(
        "SELECT id FROM Core_Card " +
                "WHERE " +
                "deletedAt IS NULL " +
                "AND (fileSyncCreatedAt!=:fileSyncModifiedAt OR fileSyncCreatedAt IS NULL) " +
                "AND fileSyncModifiedAt=:fileSyncModifiedAt"
    )
    abstract suspend fun findIdsByFileSyncModifiedAtAndFileSyncCreatedAtNot(fileSyncModifiedAt: Long): List<Int>

    @Query("SELECT ordinal FROM Core_Card c WHERE id = :id")
    abstract suspend fun getOrdinal(id: Int): Int

    /* -----------------------------------------------------------------------------------------
     * Create
     * ----------------------------------------------------------------------------------------- */

    @Transaction
    protected open suspend fun insertAtEndSync(card: Card) {
        card.ordinal = lastOrdinal() + 1
        val now = TimeUtil.getNowEpochSec()
        card.updatedAt = now
        card.createdAt = now
        insertAll(card)
    }


    @Transaction
    open suspend fun insertAfter(card: Card, ordinal: Int, updatedAt: Long): Long {
        increaseOrdinalByGreaterThanEqual(ordinal)
        card.ordinal = ordinal
        card.updatedAt = updatedAt
        return insert(card)
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

    private suspend fun decreaseOrdinalByGreaterThan(greaterThan: Int) {
        decreaseOrdinalByGreaterThan(1, greaterThan)
    }

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal-:decrease " +
                "WHERE deletedAt IS NULL AND ordinal>:greaterThan"
    )
    protected abstract suspend fun decreaseOrdinalByGreaterThan(decrease: Int, greaterThan: Int)

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal+1 " +
                "WHERE deletedAt IS NULL AND ordinal>=:greaterThanEqual AND ordinal<:lessThan"
    )
    protected abstract suspend fun increaseOrdinalByBetween(greaterThanEqual: Int, lessThan: Int)

    @Query(
        "UPDATE Core_Card SET ordinal=ordinal+1 " +
                "WHERE deletedAt IS NULL AND ordinal>=:greaterThanEqual"
    )
    protected abstract suspend fun increaseOrdinalByGreaterThanEqual(greaterThanEqual: Int)

    @Transaction
    open suspend fun changeCardOrdinal(cardId: Int, newOrdinal: Int) {
        val card = getCard(cardId)!!
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
        updateAll(card)
    }

    @Transaction
    open suspend fun pasteCards(selectedCardIds: Collection<Int>, pasteAfterPosition: Int) {
        val selectedCards = findByIds(selectedCardIds.toIntArray())

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
            changeCardOrdinal(freshCutCard.id!!, pasteAfterPositionVar)
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
    @Transaction
    open suspend fun deleteById(cardId: Int) {
        val card = getCard(cardId)!!
        delete(card)
    }

    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    @Transaction
    open suspend fun delete(card: Card) {
        card.deletedAt = TimeUtil.getNowEpochSec()
        updateAll(card)
        decreaseOrdinalByGreaterThan(card.ordinal)
    }

    /**
     * Delete and refresh ordinal numbers for all not deleted cards.
     */
    @Transaction
    open suspend fun delete(cardIds: Collection<Int>) {
        val cards = findByIds(cardIds.toIntArray())

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
    abstract suspend fun deleteAll()

    /* -----------------------------------------------------------------------------------------
     * Restore
     * ----------------------------------------------------------------------------------------- */

    /**
     * Restore and refresh ordinal numbers.
     */
    @Transaction
    open suspend fun restore(cardId: Int) {
        val card = getCard(cardId)!!
        restore(card)
    }

    /**
     * Restore and refresh ordinal numbers.
     */
    @Transaction
    open suspend fun restore(card: Card) {
        increaseOrdinalByGreaterThanEqual(card.ordinal)
        card.deletedAt = null
        updateAll(card)
    }

    /**
     * Restore and refresh ordinal numbers.
     */
    @Transaction
    open suspend fun restore(cardIds: Collection<Int>) {
        val cards = findByIds(cardIds.toIntArray())

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