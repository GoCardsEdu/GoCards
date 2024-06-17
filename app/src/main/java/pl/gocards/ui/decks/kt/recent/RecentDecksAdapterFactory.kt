package pl.gocards.ui.decks.kt.recent

import android.app.Activity
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import pl.gocards.App
import pl.gocards.ui.decks.kt.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.kt.decks.model.EditDecksViewModel
import pl.gocards.ui.decks.kt.decks.model.ListDecksViewModel
import pl.gocards.ui.kt.theme.ExtendedColors
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class RecentDecksAdapterFactory {

    fun create(
        isShownMoreDeckMenu: MutableState<Path?>,
        onSuccess: () -> Unit,
        colors: ExtendedColors,
        activity: Activity,
        scope: CoroutineScope,
        application: App
    ): ListRecentDecksAdapter {

        return ListRecentDecksAdapter(
            ListDecksViewModel(application),
            null,
            DeckDialogs(
                EditDecksViewModel(application),
                onSuccess,
                activity,
                scope,
                application
            ),
            isShownMoreDeckMenu,
            colors,
            activity,
            scope,
            application
        )
    }
}