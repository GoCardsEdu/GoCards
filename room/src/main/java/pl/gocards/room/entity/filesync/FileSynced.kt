package pl.gocards.room.entity.filesync

import androidx.room.*
import java.io.Serializable

/**
 * Represents a synchronized file.
 *
 * @author Grzegorz Ziemski
 */
@Suppress("KDocUnresolvedReference")
@Entity(
    tableName = "FileSync_FileSynced",
    indices = [Index(value = ["uri"], unique = true)]
)
class FileSynced(

    @PrimaryKey
    var id: Int? = null,
    var displayName: String = "",
    var uri: String = "",

    /**
     * It helps to more accurately estimate whether the card is newer in the deck or in the file.
     * If the card is newer than lastSyncAt, it means that the card in the deck is newer than the card in the file.
     *
     * [pl.gocards.filesync_pro.algorithms.sync.SyncFileToDeck.isImportedFileNewer]
     */
    var lastSyncAt: Long = 0L,

    /**
     * The last known updatedAt value of the file,
     * if the current value is greater, it means that the file has changed since the last synchronization and should be performed again.
     */
    var fileLastUpdatedAt: Long = 0,

    /**
     * Last deck update by sync.
     * Used in the UI to show which cards were updated during the last sync.
     */
    var deckLastUpdatedAt: Long = 0L,

    /**
     * A deck can only automatically sync with one file.
     */
    var autoSync: Boolean = false

) : Serializable