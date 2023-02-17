package pl.softfly.flashcards.dao.app;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.entity.app.AppConfig;

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
public abstract class AppConfigDao {

    @NonNull
    @Query("SELECT * FROM AppConfig WHERE `key`=:key")
    public abstract AppConfig getByKey(String key);

    @NonNull
    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    public abstract String getStringByKey(String key);

    @NonNull
    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    public abstract Long getLongByKey(String key);

    @NonNull
    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    public abstract Float getFloatByKey(String key);

}
