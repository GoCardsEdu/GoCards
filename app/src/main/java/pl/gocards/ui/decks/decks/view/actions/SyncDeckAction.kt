package pl.gocards.ui.decks.decks.view.actions

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import pl.gocards.ui.filesync_pro.FileSyncProLauncherFactory
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class SyncDeckAction(
    var onRefreshItems: () -> Unit,
    var activity: Activity,
    var owner: LifecycleOwner
) {

    private var fileSyncProInput: ((String, () -> Unit) -> Unit)? = null

    @Composable
    private fun getFileSyncProInput(): ((String, () -> Unit) -> Unit)? {
        if (fileSyncProInput == null) {
            fileSyncProInput = FileSyncProLauncherFactory.getInstance()
                ?.getInstance(activity, owner)
        }
        return fileSyncProInput
    }

    @Composable
    fun onClickSync(): ((deckDbPath: Path) -> Unit)? {
        val fileSyncProInput = getFileSyncProInput()

        return if (fileSyncProInput != null)
            { deckDbPath -> fileSyncProInput(deckDbPath.toString()) { onRefreshItems() } }
        else null
    }
}