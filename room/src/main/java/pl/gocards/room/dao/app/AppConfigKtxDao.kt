package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.app.AppConfig

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
}