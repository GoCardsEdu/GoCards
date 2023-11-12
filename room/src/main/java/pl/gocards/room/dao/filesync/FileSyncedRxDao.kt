package pl.gocards.room.dao.filesync

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import pl.gocards.room.dao.BaseRxDao
import pl.gocards.room.entity.filesync.FileSynced

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
interface FileSyncedRxDao: BaseRxDao<FileSynced> {

    @Query("SELECT * FROM FileSync_FileSynced WHERE autoSync = 1")
    fun findByAutoSyncTrue(): Maybe<FileSynced>

    @Query("SELECT deckLastUpdatedAt FROM FileSync_FileSynced ORDER BY deckLastUpdatedAt DESC")
    fun findDeckModifiedAt(): Maybe<Long>
}