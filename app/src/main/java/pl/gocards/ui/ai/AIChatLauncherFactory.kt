package pl.gocards.ui.ai

import androidx.compose.runtime.Composable
import java.lang.reflect.InvocationTargetException
import pl.gocards.ui.cards.list.AIChatInput

interface AIChatLauncherFactory {

    @Composable
    fun ScaffoldSection(
        aiChatInput: AIChatInput,
    )

    companion object {
        fun getInstance(): AIChatLauncherFactory? {
            return try {
                Class.forName("pl.gocards.ai_chat.AIChatLauncherFactoryImpl")
                    .getConstructor()
                    .newInstance() as AIChatLauncherFactory
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
