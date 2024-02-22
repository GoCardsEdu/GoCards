package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gocards.room.dao.BaseSyncRxDao
import pl.gocards.room.entity.deck.CardConfig
import java.util.function.Consumer

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardConfigRxDao: BaseSyncRxDao<CardConfig>() {

    @Query("SELECT * FROM Core_CardConfig WHERE cardId=:cardId AND `key`=:key")
    abstract fun getByKey(cardId: Int, key: String): Maybe<CardConfig>

    @Query("DELETE FROM Core_CardConfig WHERE cardId=:cardId AND `key`=:key")
    abstract fun deleteByKey(cardId: Int, key: String): Completable

    fun load(
        cardId: Int, key: String, defaultValue: Float?, consumer: Consumer<Float?>
    ): Observable<CardConfig> {
        return getByKey(cardId, key)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { consumer.accept(it.value.toFloat()) }
            .doOnEvent { value: CardConfig?, error: Throwable? ->
                if (value == null && error == null) {
                    consumer.accept(defaultValue)
                }
            }.toObservable()
    }

    fun update(
        cardId: Int,
        key: String,
        value: String
    ) : Completable {
        return getByKey(cardId, key)
            .subscribeOn(Schedulers.io())
            .doOnEvent { deckConfig: CardConfig?, throwable: Throwable? ->
                if (deckConfig == null && throwable == null) {
                    insertSync(CardConfig(cardId, key, value))
                }
            }
            .doOnSuccess { deckConfig: CardConfig ->
                deckConfig.value = value
                updateSync(deckConfig)
            }
            .ignoreElement()
    }
}