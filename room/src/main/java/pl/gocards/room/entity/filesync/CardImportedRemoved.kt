package pl.gocards.room.entity.filesync

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.util.TimeUtil

/**
 * Cards deleted from the deck and then deleted from the file.
 * Prevents you from removing new identical cards from the file indefinitely.
 * If someone adds the card to the file a second time, the card will not be deleted.
 *
 * @author Grzegorz Ziemski
 */
@Entity(
    tableName = "FileSync_CardImportedRemoved",
    foreignKeys = [ForeignKey(
        onDelete = CASCADE,
        entity = Card::class,
        parentColumns = ["id"],
        childColumns = ["cardId"]
    ), ForeignKey(
        onDelete = CASCADE,
        entity = FileSynced::class,
        parentColumns = ["id"],
        childColumns = ["fileSyncedId"]
    )],
    indices = [
        Index(value = ["fileSyncedId", "cardId"], unique = true),
        Index(value = ["cardId"])
    ]
)
@SuppressWarnings("unused")
data class CardImportedRemoved(
    @PrimaryKey
    var id: Int? = null,
    var fileSyncedId: Int,
    var cardId: Int,
    var createdAt: Long = TimeUtil.getNowEpochSec()
)