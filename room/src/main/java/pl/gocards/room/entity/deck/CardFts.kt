package pl.gocards.room.entity.deck

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * It has been moved to a external table otherwise foreign keys do not work.
 *
 * @author Grzegorz Ziemski
 */
@Fts4(contentEntity = Card::class)
@Entity(tableName = "Core_Card_fts4")
@SuppressWarnings("unused")
data class CardFts(
    @PrimaryKey
    var rowid: Int,
    var id: Int,
    var term: String? = null,
    var definition: String? = null
)