package pl.gocards.ui.decks.folders.model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.gocards.db.deck.AppDeckDbUtil
import java.io.File
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class ListFoldersViewModel(private val application: Application) : AndroidViewModel(application) {

    // must be updated only by the main thread because it is used by RecyclerView
    val folders: ArrayList<Path> = ArrayList()

    /**
     * It must remain as LiveData because it is observed by CutPasteFolderViewModel
     */
    val currentFolder: MutableLiveData<Path> = MutableLiveData(getRootFolder())

    val isEmptyFolder: MutableState<Boolean> = mutableStateOf(false)

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    fun loadItems(folder: Path, onSuccess: (folder: Path) -> Unit = {}) {
        if (countSeparators(getRootFolder()) > countSeparators(folder)) {
            throw UnsupportedOperationException("Folders lower than the root folder for the storage databases cannot be opened.")
        } else {
            currentFolder.postValue(folder)
            viewModelScope.launch(Dispatchers.IO) {

                withContext(Dispatchers.Main) {
                    folders.clear()
                    onSuccess(folder)
                }

                val folders = getDeckDatabaseUtil().listFolders(folder)
                isEmptyFolder.value = folders.isEmpty()

                withContext(Dispatchers.Main) {
                    this@ListFoldersViewModel.folders.addAll(folders)
                    onSuccess(folder)
                }
            }
        }
    }

    /**
     * D_R_03 Search decks
     */
    fun searchItems(query: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {

            val folders = getDeckDatabaseUtil().searchFolders(getRootFolder(), query)

            withContext(Dispatchers.Main) {
                this@ListFoldersViewModel.folders.clear()
                this@ListFoldersViewModel.folders.addAll(folders)
                onSuccess()
            }
        }
    }

    private fun countSeparators(path: Path): Long {
        return path.toString().chars().filter { ch: Int -> ch == File.separatorChar.code }
            .count()
    }

    /**
     * F_R_02 Open folder
     */
    fun openFolder(position: Int, onSuccess: (folder: Path) -> Unit = {}) {
        loadItems(folders[position], onSuccess)
    }

    fun openFolderUp(onSuccess: (folder: Path) -> Unit = {}) {
        loadItems(currentFolder.value!!.parent, onSuccess)
    }

    fun isRootFolder(): Boolean {
        return getRootFolder() == currentFolder.value
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    private fun getRootFolder(): Path {
        return getDeckDatabaseUtil().getDbRootFolderPath(application)
    }

    private fun getDeckDatabaseUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(application)
    }
}