package pl.gocards.room.entity.deck

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * The card used in the UI slider.
 *
 * @author Grzegorz Ziemski
 */
@Entity(tableName = "Core_Card")
data class CardSlider(
    @PrimaryKey
    var id: Int? = null,
    var ordinal: Int? = 0,
    @Ignore
    var isSaved: Boolean = true
) {

    constructor(card: Card) : this(card.id)

    constructor(id: Int?) : this(id, 0, true)

    override fun equals(other: Any?): Boolean {
        return if (other is CardSlider) {
            id == other.id
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        return id!!
    }
}