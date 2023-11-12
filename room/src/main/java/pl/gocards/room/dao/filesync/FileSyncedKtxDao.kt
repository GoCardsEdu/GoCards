package pl.gocards.room.dao.filesync

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.filesync.FileSynced

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
interface FileSyncedKtxDao: BaseKtxDao<FileSynced> {

    @Query("SELECT * FROM FileSync_FileSynced WHERE autoSync = 1")
    suspend fun findByAutoSyncTrue(): FileSynced
}