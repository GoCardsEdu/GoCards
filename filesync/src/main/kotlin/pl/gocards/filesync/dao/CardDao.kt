package pl.gocards.filesync.dao

import androidx.room.Dao

/**
 * This is for avoiding "Type is defined multiple times" errors in minifyReleaseWithR8.
 *
 * @author Grzegorz Ziemski
 */
@Dao
@Suppress("RemoveEmptyClassBody")
abstract class CardDao: pl.gocards.room.dao.deck.CardDao() {
}