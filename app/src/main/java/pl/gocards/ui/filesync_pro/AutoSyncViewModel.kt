package pl.gocards.ui.filesync_pro

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.LifecycleOwner
import java.lang.reflect.InvocationTargetException

/**
 * @author Grzegorz Ziemski
 */
interface AutoSyncViewModel {

    /**
     * FS_PRO_A Automatically sync with file when deck is opened or closed.
     */
    fun autoSync(onSyncSuccess: () -> Unit = {})

    @Composable
    fun getEditingLocked(): State<Boolean>

    companion object {
        fun getInstance(
            deckDbPath: String,
            owner: LifecycleOwner,
            application: Application
        ): AutoSyncViewModel? {
            return try {
                Class.forName("pl.gocards.filesync_pro.ui.AutoSyncViewModelImpl")
                    .getConstructor(String::class.java, Application::class.java, LifecycleOwner::class.java)
                    .newInstance(deckDbPath, application, owner) as AutoSyncViewModel
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