package pl.gocards.room.entity.deck

import androidx.room.Entity

/**
 * @author Grzegorz Ziemski
 */
@Entity(
    tableName = "Core_CardConfig",
    primaryKeys = ["cardId", "key"],
)
@SuppressWarnings("unused")
data class CardConfig(
    var cardId: Int,
    var key: String,
    var value: String
) {
    companion object {
        const val STUDY_CARD_TD_DISPLAY_RATIO = "StudyCard_TD_DisplayRatio"
        const val STUDY_CARD_TD_DISPLAY_RATIO_DEFAULT = 0.5f
    }
}