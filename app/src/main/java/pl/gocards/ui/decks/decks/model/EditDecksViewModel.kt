package pl.gocards.ui.decks.decks.model

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.internal.functions.Functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.db.storage.DatabaseException
import java.nio.file.Path


/**
 * @author Grzegorz Ziemski
 */
class EditDecksViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    /**
     * D_C_06 Create a new deck
     */
    fun createDeck(
        currentFolder: Path,
        name: String,
        onSuccess: () -> Unit,
        onDeckExists: () -> Unit
    ) {
        val deckDbPath = "$currentFolder/$name"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val deckDb = createDatabase(deckDbPath)
                deckDb.cardKtxDao().deleteAll()
                onSuccess()
            } catch (e: DatabaseException) {
                onDeckExists()
            }
        }
    }

    /**
     * D_U_07 Rename the deck
     */
    fun renameDeck(
        currentDeckPath: Path,
        name: String,
        onSuccess: () -> Unit,
        onDeckExists: () -> Unit
    ) {
        this.getDeckDbUtil()
            .renameDatabase(
                application,
                currentDeckPath,
                name,
                {
                    onSuccess()
                },
                onDeckExists
            )
    }

    /**
     * D_R_08 Delete the deck
     */
    @SuppressLint("CheckResult")
    fun deleteDeck(deckDbPath: Path, onSuccess: () -> Unit) {
        getDeckDbUtil().deleteDatabaseCompletable(application, deckDbPath)
            .doOnComplete {
                onSuccess()
            }
            .subscribe(Functions.EMPTY_ACTION)
    }

    private fun createDatabase(dbPath: String): DeckDatabase {
        return this.getDeckDbUtil().createDatabase(this.application, dbPath)
    }

    private fun getDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(this.application)
    }
}