package pl.gocards.ui.cards.slider

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.db.storage.DatabaseException
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.Card.Companion.setHtmlFlags
import pl.gocards.room.util.TimeUtil
import java.nio.file.Path
import java.util.LinkedList

/**
 * @author Grzegorz Ziemski
 */
class CreateSampleNumericDeck(private var application: Application) : AndroidViewModel(application) {

    suspend fun create(
        folder: Path,
        name: String,
        count: Int
    ) {
        val deckDb = createDatabase(application, "$folder/$name.db")
        val cards = LinkedList<Card>()
        val updatedAt = TimeUtil.getNowEpochSec()

        for (i in 0 until count) {
            val card = Card()
            card.ordinal = i + 1
            Card.setTerm(card, "Term ${i + 1}")
            Card.setDefinition(card, "Definition ${i + 1}")
            card.createdAt = updatedAt
            card.updatedAt = updatedAt
            setHtmlFlags(card)
            cards += card
        }

        deckDb.cardKtxDao().insertAll(cards)
    }

    @Throws(DatabaseException::class)
    private fun createDatabase(
        application: Context,
        dbPath: String
    ): DeckDatabase {
        return getAppDeckDbUtil(application).createDatabase(application, dbPath)
    }

    private fun getAppDeckDbUtil(application: Context): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(application)
    }
}