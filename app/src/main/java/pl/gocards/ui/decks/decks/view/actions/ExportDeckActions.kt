package pl.gocards.ui.decks.decks.view.actions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class ExportDeckActions(
    private var fileSyncViewModel: FileSyncViewModel?,
    private var activity: Activity,
    private var owner: LifecycleOwner
) {

    private var fileSyncInput: FileSyncLauncherInput? = null

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getFileSyncLauncherInput(): FileSyncLauncherInput? {
        if (fileSyncInput == null) {
            return fileSyncViewModel?.let {
                FileSyncLauncherFactory.getInstance()
                    ?.getInstance(it, activity, owner)
            }
        }
        return fileSyncInput
    }

    @Composable
    fun onExportExcel(): ((deckDbPath: Path) -> Unit)? {
        val fileSyncInput = getFileSyncLauncherInput()
        return if (fileSyncInput != null)
            { deckDbPath -> fileSyncInput.onClickExportExcel(deckDbPath.toString()) }
        else null
    }

    @Composable
    fun onExportCsv(): ((deckDbPath: Path) -> Unit)? {
        val fileSyncInput = getFileSyncLauncherInput()
        return if (fileSyncInput != null)
            { deckDbPath -> fileSyncInput.onClickExportCsv(deckDbPath.toString()) }
        else null
    }
}