package pl.gocards.ui.decks.decks.model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.LinkedList


data class UiListDeck(
    val path: Path,
    val name: String,
    val total: Int?,
    val new: Int?,
    val rev: Int?
)

/**
 * @author Grzegorz Ziemski
 */
class ListDecksViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    // must be updated only by the main thread because it is used by RecyclerView
    val decks: MutableList<UiListDeck> = LinkedList()

    var currentFolder: Path = getDbRootFolder()

    val isEmptyFolder: MutableState<Boolean> = mutableStateOf(false)

    val folderName: MutableState<Path?> = mutableStateOf(null)

    val folderPath: MutableState<String?> = mutableStateOf(null)

    fun loadItems(folder: Path = currentFolder, onSuccess: () -> Unit = {}) {
        if (countSeparators(getDbRootFolder()) > countSeparators(folder)) {
            throw UnsupportedOperationException("Folders lower than the root folder for the storage databases cannot be opened.")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                currentFolder = folder
                folderPath.value = getFolderPath()
                folderName.value = getFolderName()
                withContext(Dispatchers.Main) {
                    this@ListDecksViewModel.decks.clear()
                    onSuccess()
                }

                val paths = getDeckDbUtil().listDatabases(folder)
                val decks = mutableListOf<UiListDeck>()
                for (path in paths) {
                    decks.add(mapListDeck(path))
                }

                withContext(Dispatchers.Main) {
                    isEmptyFolder.value = decks.isEmpty()
                    this@ListDecksViewModel.decks.clear()
                    this@ListDecksViewModel.decks.addAll(decks)
                    onSuccess()
                }
            }
        }
    }

    fun searchItems(query: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            folderPath.value = null
            folderName.value = null

            val paths = getDeckDbUtil().searchDatabases(getDbRootFolder(), query)
            val decks = mutableListOf<UiListDeck>()
            for (path in paths) {
                decks.add(mapListDeck(path))
            }

            withContext(Dispatchers.Main) {
                this@ListDecksViewModel.decks.clear()
                this@ListDecksViewModel.decks.addAll(decks)
                onSuccess()
            }
        }
    }

    fun loadLastUsed(onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val paths = getAppDatabase()
                .deckKtxDao()
                .findByLastUpdatedAt(15)

            val decks = mutableListOf<UiListDeck>()
            for (path in paths) {
                decks.add(mapListDeck(Paths.get(path.path)))
            }

            folderPath.value = null
            folderName.value = null
            isEmptyFolder.value = decks.isEmpty()

            withContext(Dispatchers.Main) {
                this@ListDecksViewModel.decks.clear()
                this@ListDecksViewModel.decks.addAll(decks)
                onSuccess()
            }

        }
    }

    private fun countSeparators(path: Path): Long {
        return path.toString().chars().filter { ch: Int -> ch == File.separatorChar.code }
            .count()
    }

    private suspend fun mapListDeck(path: Path): UiListDeck {
        val dbPath = path.toString()

        var deckDb: DeckDatabase?
        try {
            deckDb = getDeckDb(dbPath)
        } catch (e: Exception) {
            deckDb = null
            e.printStackTrace()
        }

        return UiListDeck(
            path = path,
            name = path.fileName.toString().replace(".db", ""),
            total = deckDb?.cardKtxDao()?.countByNotDeleted(),
            new = deckDb?.cardLearningProgressKtxDao()?.countByNew(),
            rev = deckDb?.cardLearningProgressKtxDao()?.countByForgotten()
        )
    }

    private fun getFolderPath(): String {
        return currentFolder.toString()
            .replace(getDbRootFolder().toString(), "")
            .replace("/", " › ")
    }

    private fun getFolderName(): Path? {
        return if (currentFolder == getDbRootFolder()) {
            null
        } else {
            currentFolder.fileName
        }
    }

    private fun getDbRootFolder(): Path {
        return this.getDeckDbUtil().getDbRootFolderPath(this.application)
    }

    private fun getDeckDb(dbPath: String): DeckDatabase {
        return this.getDeckDbUtil().getDatabase(this.application, dbPath)
    }

    private fun getDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(this.application)
    }

    private fun getAppDatabase(): AppDatabase {
        return AppDbUtil
            .getInstance(this.application)
            .getDatabase(this.application)
    }
}

class ListDecksViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ListDecksViewModel::class.java)) {
            ListDecksViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}