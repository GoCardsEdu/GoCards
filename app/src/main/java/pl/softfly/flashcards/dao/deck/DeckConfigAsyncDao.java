package pl.softfly.flashcards.dao.deck;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.entity.deck.DeckConfig;

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
public abstract class DeckConfigAsyncDao {

    @NonNull
    @Query("SELECT * FROM Core_DeckConfig WHERE `key`=:key")
    public abstract Maybe<DeckConfig> getByKey(String key);

    @NonNull
    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    public abstract Maybe<Long> getLongByKey(String key);

    @NonNull
    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    public abstract Maybe<Float> getFloatByKey(String key);

    @NonNull
    @Insert
    public abstract Completable insert(DeckConfig deckConfig);

    @Insert
    protected abstract void insertSync(DeckConfig deckConfig);

    @NonNull
    @Update
    public abstract Completable update(DeckConfig deckConfig);

    @Update
    protected abstract void updateSync(DeckConfig deckConfig);

    @Query("DELETE FROM Core_DeckConfig WHERE `key`=:key")
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
                    .doOnEvent((deckConfig, throwable) -> {
                        if (deckConfig == null && throwable == null) {
                            insertSync(new DeckConfig(key, value));
                        }
                    })
                    .subscribe(deckConfig -> {
                        deckConfig.setValue(value);
                        updateSync(deckConfig);
                    }, onError);
        }
    }
}
