package pl.gocards.room.dao

import androidx.room.Insert
import androidx.room.Update

/**
 * @author Grzegorz Ziemski
 */
interface BaseKtxDao<T> {

    @Insert
    suspend fun insertAll(vararg obj: T)

    @Insert
    suspend fun insertAll(obj: List<T>)

    @Update
    suspend fun updateAll(vararg obj: T)

    @Update
    suspend fun updateAll(obj: List<T>)

}