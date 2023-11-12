package pl.gocards.room.dao.app

import androidx.room.Dao
import androidx.room.Query

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class AppConfigDao {

    @Query("SELECT value FROM AppConfig WHERE `key`=:key")
    abstract fun getStringByKey(key: String): String?
}