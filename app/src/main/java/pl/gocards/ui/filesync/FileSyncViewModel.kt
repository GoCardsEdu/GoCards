package pl.gocards.ui.filesync

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import pl.gocards.room.entity.filesync.FileSynced
import java.lang.reflect.InvocationTargetException

/**
 * @author Grzegorz Ziemski
 */
interface FileSyncViewModel {

    fun inProgress(deckDbPath: String): MutableLiveData<Boolean>

    /**
     * FS_PRO_S.1. Check that deck editing is not locked by another export/import/sync process.
     */
    fun checkIfDeckEditingIsLocked(
        context: Context,
        deckDbPath: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    )

    /**
     * FS_PRO_S.6. Lock the deck editing.
     */
    fun lockDeckEditing(
        context: Context,
        deckDbPath: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    )

    fun findFileSynced(
        context: Context,
        deckDbPath: String,
        fileUri: String,
        onSuccess: (fileSynced: FileSynced?) -> Unit
    )

    companion object {
        const val TAG = "FileSync"

        fun getInstance(
            owner: LifecycleOwner,
            application: Application,
        ): FileSyncViewModel? {
            return try {
                Class.forName("pl.gocards.filesync.ui.FileSyncViewModelImpl")
                    .getConstructor(
                        LifecycleOwner::class.java,
                        Application::class.java
                    )
                    .newInstance(owner, application) as FileSyncViewModel
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