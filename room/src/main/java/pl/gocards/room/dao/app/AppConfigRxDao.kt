package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gocards.room.dao.BaseSyncRxDao
import pl.gocards.room.entity.app.AppConfig

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class AppConfigRxDao : BaseSyncRxDao<AppConfig>() {

    @Query("SELECT * FROM AppConfig WHERE `key`=:key")
    abstract fun getByKey(key: String): Maybe<AppConfig>

    fun load(
        key: String, defaultValue: String, consumer: Consumer<String>
    ): Observable<AppConfig> {
        return getByKey(key)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { consumer.accept(it.value) }
            .doOnEvent { value: AppConfig?, error: Throwable? ->
                if (value == null && error == null) {
                    consumer.accept(defaultValue)
                }
            }.toObservable()
    }
}