package pl.gocards.ui.decks.folders.model

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gocards.db.deck.AppDeckDbUtil
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Grzegorz Ziemski
 */
class CutPasteFolderViewModel(
    currentFolder: LiveData<Path>,
    private val application: Application,
    owner: LifecycleOwner
) : AndroidViewModel(application) {

    val showFolderPasteBar = mutableStateOf(false)

    private val cutPath = mutableStateOf<Path?>(null)

    init {
        currentFolder.observe(owner) { currentFolder ->
            val path = cutPath.value
            if (path != null && Files.isDirectory(path)) {
                showFolderPasteBar.value = !currentFolder.toString().startsWith(path.toString())
            }
        }
    }

    /**
     * F_C_06 Cut the folder
     */
    fun cut(path: Path) {
        cutPath.value = path
        showFolderPasteBar.value = true
    }

    /**
     * F_C_07 Paste the folder
     */
    fun paste(
        toFolder: Path,
        onSuccess: (String, Boolean) -> Unit
    ) {
        val path = cutPath.value ?: return
        paste(
            path = path,
            toFolder = toFolder,
            onSuccess
        )
        clear()
    }

    /**
     * F_C_07 Paste the folder
     */
    @SuppressLint("CheckResult")
    private fun paste(
        path: Path,
        toFolder: Path,
        onSuccess: (String, Boolean) -> Unit
    ) {
        val fileName = path.fileName.toString()
        val toPath = Paths.get(toFolder.toString(), fileName)

        getDeckDbUtil().moveFolder(application, path, toPath)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { merged -> onSuccess(fileName, merged) }
            .ignoreElement()
            .subscribe(Functions.EMPTY_ACTION)
    }

    fun clear() {
        cutPath.value = null
        showFolderPasteBar.value = false
    }

    private fun getDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(this.application)
    }
}