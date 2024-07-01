package pl.gocards.ui.filesync

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import java.lang.reflect.InvocationTargetException

enum class FileSyncAction {
    IMPORT, EXPORT
}

data class FileSyncLauncherInput(
    /**
     * FS_E Export the deck to to a new file.
     */
    val onClickExportExcel: (deckDbPath: String) -> Unit,
    /**
     * FS_E Export the deck to to a new file.
     */
    val onClickExportCsv: (deckDbPath: String) -> Unit,
    /**
     * FS_I Import the file as a new deck.
     */
    val onClickImport: (importToFolderPath: String, onSuccess: () -> Unit) -> Unit
)

/**
 * @author Grzegorz Ziemski
 */
interface FileSyncLauncherFactory {

    @Composable
    fun getInstance(
        fileSyncViewModel: FileSyncViewModel,
        activity: Activity,
        owner: LifecycleOwner
    ): FileSyncLauncherInput

    companion object {
        const val TAG = "FileSync"

        fun getInstance(): FileSyncLauncherFactory? {
            return try {
                Class.forName("pl.gocards.filesync.ui.FileSyncLauncherFactoryImpl")
                    .getConstructor()
                    .newInstance() as FileSyncLauncherFactory
            } catch (e: ClassNotFoundException) {
                null
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InstantiationException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            }
        }
    }
}