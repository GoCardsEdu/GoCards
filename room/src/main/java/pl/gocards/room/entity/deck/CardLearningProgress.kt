package pl.gocards.room.entity.deck

import androidx.room.Entity
import androidx.room.Index

/**
 * @author Grzegorz Ziemski
 */
@Entity(
    tableName = "Core_CardLearningProgress",
    primaryKeys = ["cardId", "cardLearningHistoryId"],
    indices = [
        Index(value = arrayOf("cardId"), unique=true),
        Index(value = arrayOf("cardLearningHistoryId"), unique=true)
    ]
)
data class CardLearningProgress(
    var cardId: Int = 0,
    var cardLearningHistoryId: Int = 0,

    /**
     * Status if the card is currently memorized
     */
    var isMemorized: Boolean = false
)