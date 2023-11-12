package pl.gocards.room.entity.app

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author Grzegorz Ziemski
 */
@Entity(
    indices = [Index(value = arrayOf("path"), unique=true)]
)
@SuppressWarnings("unused")
data class Deck(

    @PrimaryKey(autoGenerate=true)
    var id: Int? = null,
    var name: String = "",

    /**
     * With .db extension.
     */
    var path: String = "",

    /**
     * D_R_05 Show recent used decks
     * Refreshed after adding or updating any [pl.gocards.room.entity.deck.Card]
     * or [pl.gocards.room.entity.deck.CardLearningProgressAndHistory].
     */
    var lastUpdatedAt: Long = 0
)