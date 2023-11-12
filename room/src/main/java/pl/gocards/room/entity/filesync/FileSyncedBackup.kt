package pl.gocards.room.entity.filesync

import androidx.room.*

/**
 * Stores a file backup and data that
 * allows to retry synchronization if failed previously.
 *
 * Useful for debugging, you can repeat sync
 * instead of doing the whole scenario from scratch with every test.
 *
 * @author Grzegorz Ziemski
 */
@Entity(
    tableName = "FileSync_FileSyncedBackup",
)
data class FileSyncedBackup(

    @PrimaryKey
    var id: Int? = null,

    var uri: String = "",

    var fileName: String = "",

    var fileMimeType: String = "",

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var fileBackup: ByteArray? = null,

    /**
     * The first file opening datetime must be used,
     * not the current datetime of the corrupted file.
     */
    var updatedAt: Long = 0
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}