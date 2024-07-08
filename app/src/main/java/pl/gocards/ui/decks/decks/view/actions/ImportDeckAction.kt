package pl.gocards.ui.decks.decks.view.actions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel

/**
 * @author Grzegorz Ziemski
 */
class ImportDeckAction(
    private val adapter: ListDecksAdapter,
    private val fileSyncViewModel: FileSyncViewModel?,
    private val onRefreshItems: () -> Unit,
    private val activity: Activity,
    private val owner: LifecycleOwner
) {

    private var fileSyncInput: FileSyncLauncherInput? = null

    @Composable
    fun onClickImport(): (() -> Unit)? {
        val fileSyncInput = getFileSyncLauncherInput()
        return if (fileSyncInput != null) {
            {
                val folder = adapter.getCurrentFolder().toString()
                fileSyncInput.onClickImport(folder) { onRefreshItems() }
            }
        } else null
    }

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
}