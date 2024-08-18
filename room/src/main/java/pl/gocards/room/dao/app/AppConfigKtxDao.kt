package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
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
abstract class AppConfigKtxDao : BaseKtxDao<AppConfig> {

    @Query("SELECT * FROM AppConfig WHERE `key`=:key")
    abstract suspend fun getByKey(key: String): AppConfig?

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    abstract suspend fun getStringByKey(key: String): String?

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    abstract suspend fun getLongByKey(key: String): Long?

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    abstract suspend fun getFloatByKey(key: String): Float?

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    suspend fun getZonedDateTimeByKey(key: String): ZonedDateTime? {
        val v = getStringByKey(key) ?: return null
        return ZonedDateTime.parse(v)
    }

    @Query("DELETE FROM AppConfig WHERE `key`=:key")
    abstract suspend fun deleteByKey(key: String)

    suspend fun update(
        key: String,
        value: String,
        defaultValue: String?
    ) {
        if (value == defaultValue) {
            deleteByKey(key)
        } else {
            val appConfig = getByKey(key)
            if (appConfig == null) {
                insertAll(AppConfig(key, value))
            } else {
                appConfig.value = value
                updateAll(appConfig)
            }
        }
    }

    private suspend fun updateZonedDateTime(key: String, value: ZonedDateTime) {
        this.update(
            key,
            value.withZoneSameInstant(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                .toString(),
            null
        )
    }

    suspend fun getFirstUsedAt(): ZonedDateTime {
        val firstUsedAt = getZonedDateTimeByKey(AppConfig.FIRST_USED_AT)

        return if (firstUsedAt == null) {
            val now = ZonedDateTime.now()
            updateZonedDateTime(AppConfig.FIRST_USED_AT, now)
            now
        } else {
            firstUsedAt
        }
    }

    suspend fun getLastExceptionAt(): ZonedDateTime? {
        return getZonedDateTimeByKey(AppConfig.LAST_EXCEPTION_AT)
    }

    suspend fun refreshLastExceptionAt() {
        updateZonedDateTime(AppConfig.LAST_EXCEPTION_AT, ZonedDateTime.now())
    }
}