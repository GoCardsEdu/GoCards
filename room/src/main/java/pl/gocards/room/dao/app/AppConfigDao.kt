package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseDao
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.app.AppConfig
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class AppConfigDao : BaseDao<AppConfig> {

    @Query("SELECT * FROM AppConfig WHERE `key`=:key")
    abstract fun getByKey(key: String): AppConfig?

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    abstract fun getStringByKey(key: String): String?

    fun update(key: String, value: String) {
        val appConfig = getByKey(key)
        if (appConfig == null) {
            insertAll(AppConfig(key, value))
        } else {
            appConfig.value = value
            updateAll(appConfig)
        }
    }

    @Suppress("SameParameterValue")
    private fun updateZonedDateTime(key: String, value: ZonedDateTime) {
        this.update(
            key,
            value.withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toString(),
        )
    }

    fun refreshLastExceptionAt() {
        updateZonedDateTime(AppConfig.LAST_EXCEPTION_AT, ZonedDateTime.now())
    }
}