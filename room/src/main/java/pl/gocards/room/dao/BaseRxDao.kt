package pl.gocards.room.dao

import androidx.room.Insert
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable

/**
 * @author Grzegorz Ziemski
 */
interface BaseRxDao<T> {

    @Insert
    fun insertAll(vararg obj: T): Completable

    @Insert
    fun insertAll(obj: List<T>): Completable

    @Update
    fun updateAll(vararg obj: T): Completable

    @Update
    fun updateAll(obj: List<T>): Completable

}