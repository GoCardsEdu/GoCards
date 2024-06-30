package pl.gocards.ui.decks.folders.model

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.internal.functions.Functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.AppDatabase
import java.io.File
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class EditFoldersViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    /**
     * F_C_03 Create a folder
     */
    fun createFolder(
        currentFolder: Path,
        newFolderName: String,
        onSuccess: () -> Unit,
        onExists: () -> Unit
    ) {
        val folder = File("$currentFolder/$newFolderName")
        if (!folder.exists()) {
            folder.mkdir()
            onSuccess()
        } else {
            onExists()
        }
    }

    /**
     * F_U_04 Rename the folder
     */
    @SuppressLint("CheckResult")
    fun renameFolder(
        folderPath: Path,
        newFolderName: String,
        onRenamed: () -> Unit,
        onMerged: () -> Unit
    ) {
        getDeckDatabaseUtil().renameFolder(application, folderPath, newFolderName)
            .doOnSuccess { merged: Boolean ->
                if (merged) {
                    onMerged()
                } else {
                    onRenamed()
                }
            }
            .ignoreElement()
            .subscribe(Functions.EMPTY_ACTION)
    }

    /**
     * F_D_05 Delete the folder
     */
    fun deleteFolder(
        folderPath: Path,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            getDeckDatabaseUtil().deleteFolder(folderPath)
            deleteDecksInDb(folderPath)
            onSuccess()
        }
    }

    private suspend fun deleteDecksInDb(folder: Path) {
        getAppDb().deckKtxDao().deleteByStartWithPath(folder.toString())
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    private fun getDeckDatabaseUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(application)
    }

    private fun getAppDb(): AppDatabase {
        return AppDbUtil.getInstance(application)
            .getDatabase(application)
    }
}