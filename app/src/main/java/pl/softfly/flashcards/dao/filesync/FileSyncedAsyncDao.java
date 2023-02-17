package pl.softfly.flashcards.dao.filesync;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import pl.softfly.flashcards.entity.filesync.FileSynced;

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
public interface FileSyncedAsyncDao {

    @NonNull
    @Query("SELECT * FROM FileSync_FileSynced WHERE autoSync = 1")
    Maybe<FileSynced> findByAutoSyncTrue();

    @NonNull
    @Query("SELECT deckModifiedAt FROM FileSync_FileSynced ORDER BY deckModifiedAt DESC")
    Maybe<Long> findDeckModifiedAt();

    @NonNull
    @Update
    Completable updateAll(FileSynced... cards);

}
