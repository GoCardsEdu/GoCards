package pl.gocards.ui.ai

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.StateFlow
import pl.gocards.ui.cards.list.model.ListCardsViewModel
import java.lang.reflect.InvocationTargetException

interface AIChatContract {
    val modelName: StateFlow<String?>
    val availableModels: StateFlow<List<String>>
    fun onOpen()
    fun switchModel(modelName: String)
    fun sendMessage(message: String)

    companion object {
        fun createViewModel(
            owner: ViewModelStoreOwner,
            application: Application,
            activity: Activity,
            listCardsViewModel: ListCardsViewModel,
            onCardsReloaded: ((Int?) -> Unit)? = null
        ): AIChatContract? {
            return try {
                val factoryClass = Class.forName("pl.gocards.ai_chat.viewmodel.AIChatViewModelFactory")
                val factory = factoryClass
                    .getConstructor(Application::class.java, Activity::class.java, ListCardsViewModel::class.java, Function1::class.java)
                    .newInstance(application, activity, listCardsViewModel, onCardsReloaded) as ViewModelProvider.Factory

                @Suppress("UNCHECKED_CAST")
                val viewModelClass = Class.forName("pl.gocards.ai_chat.viewmodel.AIChatViewModel") as Class<ViewModel>
                ViewModelProvider(owner, factory)[viewModelClass] as AIChatContract
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
