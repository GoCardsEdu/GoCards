package pl.gocards.room.entity.filesync

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The entity is used to determine the new order of the cards after synchronization.
 * An edge is created for cards placed side by side.
 *
 * Graph representation:
 * - The cards are the vertices.
 * - Two cards next to each other have an edge.
 * - The weight is the novelty of the data.
 *
 * @author Grzegorz Ziemski
 */
@Entity(tableName = "FileSync_CardEdge")
@SuppressWarnings("unused")
data class CardEdge(
    @PrimaryKey
    var id: Int? = null,
    var fromCardImportedId: Int = 0,
    var toCardImportedId: Int = 0,
    var status: String = "",
    var weight: Int = 0,
    var deleted: Boolean = false
) {
    companion object {
        const val STATUS_UNCHANGED = "UNCHANGED"
        const val STATUS_IMPORTED_BOTH_OLDER = "IMPORTED_BOTH_OLDER"
        const val STATUS_DECK_BOTH_OLDER = "DECK_BOTH_OLDER"
        const val STATUS_IMPORTED_ONE_NEWER = "IMPORTED_ONE_NEWER"
        const val STATUS_DECK_ONE_NEWER = "DECK_ONE_NEWER"
        const val STATUS_IMPORTED_BOTH_NEWER = "IMPORTED_BOTH_NEWER"
        const val STATUS_DECK_BOTH_NEWER = "DECK_BOTH_NEWER"
        const val STATUS_IMPORTED_BOTH_NEW = "IMPORTED_BOTH_NEW"
        const val STATUS_DECK_BOTH_NEW = "DECK_BOTH_NEW"
        const val STATUS_IMPORTED_FIRST_NEW = "IMPORTED_FIRST_NEW"
        const val STATUS_IMPORTED_SECOND_NEW = "IMPORTED_SECOND_NEW"
        const val STATUS_DECK_FIRST_NEW = "DECK_FIRST_NEW"
        const val STATUS_DECK_SECOND_NEW = "DECK_SECOND_NEW"
    }
}