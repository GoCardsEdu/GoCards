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
public abstract class AppConfigAsync {

    @NonNull
    @Query("SELECT * FROM AppConfig WHERE `key`=:key")
    public abstract Maybe<AppConfig> getByKey(String key);

    @NonNull
    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    public abstract Maybe<Long> getLongByKey(String key);

    @NonNull
    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    public abstract Maybe<Float> getFloatByKey(String key);

    @NonNull
    @Insert
    public abstract Completable insert(AppConfig deckConfig);

    @Insert
    protected abstract void insertSync(AppConfig deckConfig);

    @NonNull
    @Update
    public abstract Completable update(AppConfig deckConfig);

    @Update
    protected abstract void updateSync(AppConfig deckConfig);

    @Query("DELETE FROM AppConfig WHERE `key`=:key")
    public abstract Completable deleteByKey(String key);

    public void update(
            String key, String value,
            String defaultValue
    ) {
        update(key, value, defaultValue, throwable -> {});
    }

    public void update(
            String key,
            String value,
            String defaultValue,
            Consumer<? super Throwable> onError
    ) {
        if (value == defaultValue) {
            deleteByKey(key)
                    .subscribeOn(Schedulers.io())
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(() -> {}, onError);
        } else {
            getByKey(key)
                    .subscribeOn(Schedulers.io())
                    .doOnError(Throwable::printStackTrace)
                    .doOnEvent((config, throwable) -> {
                        if (config == null && throwable == null) {
                            insertSync(new AppConfig(key, value));
                        }
                    })
                    .subscribe(config -> {
                        config.setValue(value);
                        updateSync(config);
                    }, onError);
        }
    }

    public Observable<AppConfig> load(String key, String defaultValue, java.util.function.Consumer<String> consumer) {
        return getByKey(key)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(appConfig -> consumer.accept(appConfig.getValue()))
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        consumer.accept(defaultValue);
                    }
                })
                .toObservable();
    }

}
