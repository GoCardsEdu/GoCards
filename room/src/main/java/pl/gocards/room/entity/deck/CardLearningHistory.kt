package pl.gocards.room.entity.deck

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author Grzegorz Ziemski
 */
@Entity(
    tableName = "Core_CardLearningHistory",
    indices = [
        Index(value = arrayOf("cardId"))
    ]
)
data class CardLearningHistory(
    @PrimaryKey
    var id: Int? = null,
    var cardId: Int = 0,

    /**
     * Status if the user remembered this card in this replay.
     *
     * NULL  = No buttons have been clicked in this replay yet.
     * TRUE  = The Again button has been not clicked.
     * FALSE = The Again button has been clicked at least once.
     */
    var wasMemorized: Boolean? = null,

    /**
     * Hours added to the nextReplayAt if the user remembered the card.
     */
    var interval: Int = 0,

    var nextReplayAt: Date? = null,

    /* -----------------------------------------------------------------------------------------
     * Statistics
     * ----------------------------------------------------------------------------------------- */

    /**
     * Replay number
     */
    var replayId: Int = 0,

    /**
     * How many times in a row was memorized.
     */
    var countMemorized: Int = 0,

    /**
     * How many times the card has not been remembered from the beginning.
     */
    var countNotMemorized: Int = 0,

    /**
     * The time when the grade button was clicked.
     */
    var createdAt: Long = 0,

    /**
     * How long has it been memorized.
     * The time when the grade button was clicked after {@link CardLearningHistory#nextReplayAt}
     */
    var memorizedDuration: Long? = null
)