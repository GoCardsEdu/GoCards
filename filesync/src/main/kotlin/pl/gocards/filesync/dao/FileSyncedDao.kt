package pl.gocards.filesync.dao

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.entity.filesync.FileSynced

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class FileSyncedDao : BaseDao<FileSynced> {

    @Query("SELECT * FROM FileSync_FileSynced WHERE uri=:uri")
    abstract fun findByUri(uri: String): FileSynced?

    @Query("UPDATE FileSync_FileSynced SET autoSync=0 WHERE id!=:id")
    abstract fun disableAutoSyncByIdNot(id: Int)

    open fun findOrCreate(
        fileUri: String,
        displayName: String,
        autoSync: Boolean
    ): FileSynced {
        var fileSynced: FileSynced? = findByUri(fileUri)
        if (fileSynced == null) {
            fileSynced = FileSynced()
            fileSynced.uri = fileUri
            fileSynced.displayName = displayName
            fileSynced.autoSync = autoSync

            if (fileSynced.id == null) {
                fileSynced.id = insert(fileSynced).toInt()
            }
        }
        return fileSynced
    }
}