package pl.gocards.ui.decks.recent

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import pl.gocards.App
import pl.gocards.ui.decks.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.decks.model.EditDecksViewModel
import pl.gocards.ui.decks.decks.model.ListDecksViewModel
import pl.gocards.ui.theme.ExtendedColors
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class RecentDecksAdapterFactory {

    fun create(
        isShownMoreDeckMenu: MutableState<Path?>,
        isPremium: Boolean,
        onSuccess: () -> Unit,
        colors: ExtendedColors,
        startActivityForResultLauncher: ActivityResultLauncher<Intent>,
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
            isPremium,
            colors,
            startActivityForResultLauncher,
            activity,
            scope,
            application
        )
    }
}