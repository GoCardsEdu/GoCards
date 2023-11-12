package pl.gocards.room.entity.filesync

import androidx.room.*
import pl.gocards.room.entity.deck.Card

/**
 * A card representing a card from a synchronized / imported / exported file.
 *
 * @author Grzegorz Ziemski
 */
@Suppress("KDocUnresolvedReference")
@Entity(
    tableName = "FileSync_CardImported",
    indices = [
        Index("graph"),
        Index("cardId"),
        Index("contentStatus"),
        Index("newPreviousCardImportedId"),
        Index("newNextCardImportedId")
    ]
)
data class CardImported(

    @PrimaryKey
    var id: Int? = null,

    /**
     * If it is empty, it means that the card is not from the file, but is a new card from the deck.
     */
    var oldOrdinal: Int? = null,

    /**
     * Represents the order of cards after synchronization.
     * Determined by [pl.gocards.filesync_pro.algorithms.sync.DetermineNewOrderCards.determineNewOrdinals].
     */
    var newOrdinal: Int? = null,

    /**
     * The id of [pl.gocards.room.entity.deck.Card].
     */
    var cardId: Int? = null,
    var contentStatus: String? = null,
    var positionStatus: String? = null,

    /**
     * The value is null if the value in the card is the same.
     * Optimization for storing redundant text data.
     * [CardImported.STATUS_UNCHANGED]
     */
    var term: String? = null,

    /**
     * The value is null if the value in the card is the same.
     * Optimization for storing redundant text data.
     * [CardImported.STATUS_UNCHANGED]
     */
    var definition: String? = null,

    var disabled: Boolean? = null,

    /**
     * Previous [CardImported].
     * Represents the order of cards from the file before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.importAndMatchCardsFromImportedFile]
     */
    var previousId: Int? = null,

    /**
     * Next [CardImported].
     * Represents the order of cards from the file before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.importAndMatchCardsFromImportedFile]
     */
    var nextId: Int? = null,

    /**
     * Previous [Card].
     * Represents the order of cards from the deck before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processDeckCard]
     */
    var previousDeckCardId: Int? = null,

    /**
     * Next [Card].
     * Represents the order of cards from the deck before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processDeckCard]
     */
    var nextDeckCardId: Int? = null,

    /**
     * Previous [Card].
     * Represents the order of cards from the file before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processCardFromFile]
     */
    var previousFileCardId: Int? = null,

    /**
     * Next [Card].
     * Represents the order of cards from the file before synchronization.
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processCardFromFile]
     */
    var nextFileCardId: Int? = null,

    /**
     * Previous [Card]
     * Represents the order of cards after synchronization.
     * Determined by [pl.gocards.filesync_pro.algorithms.sync.DetermineNewOrderCards].
     */
    var newPreviousCardImportedId: Int? = null,

    /**
     * Next [Card].
     * Represents the order of cards after synchronization.
     * Determined by [pl.gocards.filesync_pro.algorithms.sync.DetermineNewOrderCards].
     */
    var newNextCardImportedId: Int? = null,

    /**
     * Cards are merged into graphs.
     * At the end of the [pl.gocards.filesync_pro.algorithms.sync.DetermineNewOrderCards], the cards are merged into one graph.
     */
    var graph: Int? = null,

    /**
     * -----------------------------------------------------------------------------------------
     * Debugging
     * -----------------------------------------------------------------------------------------
     */
    var debugTerm: String? = null,
    var debugDefinition: String? = null,
    var debugFirstGraph: Int? = null,
    var debugCardEdgeId: Int? = null,
    var debugTermSimilarity: Double? = null,
    var debugDefSimilarity: Double? = null
) {

    companion object {

        /* -----------------------------------------------------------------------------------------
         * {@link #contentStatus}
         * ----------------------------------------------------------------------------------------- */
        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.importAndMatchCardsFromImportedFile]
         */
        const val STATUS_UNCHANGED = "UNCHANGED"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.matchSimilarCards]
         */
        const val STATUS_UPDATE_BY_DECK = "UPDATE_BY_DECK"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.matchSimilarCards]
         */
        const val STATUS_UPDATE_BY_FILE = "UPDATE_BY_FILE"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processDeckCard]
         */
        const val STATUS_INSERT_BY_DECK = "INSERT_BY_DECK"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.findNewCardsInImportedFile]
         */
        const val STATUS_INSERT_BY_FILE = "INSERT_BY_FILE"

        /**
         * The entire deck would need to be newer than the imported file.
         */
        const val STATUS_DELETE_BY_DECK = "DELETE_BY_DECK"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.processDeckCard]
         */
        const val STATUS_DELETE_BY_FILE = "DELETE_BY_FILE"

        /* -----------------------------------------------------------------------------------------
         * {@link #positionStatus}
         * ----------------------------------------------------------------------------------------- */
        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.checkIfPositionUnchanged]
         */
        const val POSITION_STATUS_UNCHANGED = "UNCHANGED"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.importAndMatchCardsFromImportedFile]
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.matchSimilarCards]
         */
        const val POSITION_STATUS_BY_DECK = "BY_DECK"

        /**
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.importAndMatchCardsFromImportedFile]
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.matchSimilarCards]
         * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.findNewCardsInImportedFile]
         */
        const val POSITION_STATUS_BY_FILE = "BY_FILE"
    }
}