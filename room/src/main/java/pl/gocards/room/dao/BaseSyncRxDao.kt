package pl.gocards.room.dao

import androidx.room.Insert
import androidx.room.Update

/**
 * @author Grzegorz Ziemski
 */
abstract class BaseSyncRxDao<T>: BaseRxDao<T> {

    @Insert
    protected abstract fun insertSync(obj: T): Long

    @Insert
    protected abstract fun insertAllSync(obj: List<T>)

    @Insert
    protected abstract fun insertAllSync(vararg obj: T)

    @Update
    protected abstract fun updateSync(obj: T)

    @Update
    protected abstract fun updateAllSync(vararg obj: T)

    @Update
    protected abstract fun updateAllSync(obj: List<T>)
}