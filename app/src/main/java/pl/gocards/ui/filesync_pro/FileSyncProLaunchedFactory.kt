package pl.gocards.ui.filesync_pro

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import java.lang.reflect.InvocationTargetException


/**
 * FS_PRO_S Synchronize the deck with a file.
 * @author Grzegorz Ziemski
 */
interface FileSyncProLauncherFactory {

    @Composable
    fun getInstance(
        activity: Activity,
        owner: LifecycleOwner
    ): (deckDbPath: String, onSuccess: () -> Unit) -> Unit

    companion object {

        fun getInstance(): FileSyncProLauncherFactory? {
            return try {
                Class.forName("pl.gocards.filesync_pro.ui.FileSyncProLauncherFactoryImpl")
                    .getConstructor()
                    .newInstance() as FileSyncProLauncherFactory
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