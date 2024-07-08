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
    private var adapter: ListDecksAdapter,
    private var fileSyncViewModel: FileSyncViewModel?,
    private var onRefreshItems: () -> Unit,
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
    fun onClickImport(): (() -> Unit)? {
        val fileSyncInput = getFileSyncLauncherInput()
        return if (fileSyncInput != null) {
            {
                val folder = adapter.getCurrentFolder().toString()
                fileSyncInput.onClickImport(folder) { onRefreshItems() }
            }
        } else null
    }
}