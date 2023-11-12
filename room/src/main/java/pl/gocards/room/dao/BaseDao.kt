package pl.gocards.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * @author Grzegorz Ziemski
 */
interface BaseDao<T> {

    @Insert
    fun insert(obj: T): Long

    @Insert
    fun insertAll(vararg obj: T)

    @Insert
    fun insertAll(obj: List<T>)

    @Update
    fun update(obj: T)

    @Update
    fun updateAll(vararg obj: T)

    @Update
    fun updateAll(obj: List<T>)

    @Delete
    fun delete(obj: T)

}