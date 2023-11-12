package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gocards.room.dao.BaseSyncRxDao
import pl.gocards.room.entity.deck.DeckConfig

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class DeckConfigRxDao: BaseSyncRxDao<DeckConfig>() {

    @Query("SELECT * FROM Core_DeckConfig WHERE `key`=:key")
    abstract fun getByKey(key: String): Maybe<DeckConfig>

    @Query("SELECT value FROM Core_DeckConfig WHERE `key`=:key")
    abstract fun getLongByKey(key: String): Maybe<Long?>

    fun load(
        key: String, defaultValue: String, consumer: Consumer<String>
    ): Observable<DeckConfig> {
        return getByKey(key)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { consumer.accept(it.value) }
            .doOnEvent { value: DeckConfig?, error: Throwable? ->
                if (value == null && error == null) {
                    consumer.accept(defaultValue)
                }
            }.toObservable()
    }
}