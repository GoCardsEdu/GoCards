package pl.gocards.ui.decks.kt.recent

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.ui.decks.kt.decks.ListDecksAdapter
import pl.gocards.ui.decks.kt.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.kt.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.kt.decks.model.ListDecksViewModel
import pl.gocards.ui.kt.theme.ExtendedColors
import java.nio.file.Path

/**
 * D_R_05 Show recent used decks
 * @author Grzegorz Ziemski
 */
open class ListRecentDecksAdapter(
    decksViewModel: ListDecksViewModel,
    cutPasteDeckViewModel: CutPasteDeckViewModel?,
    deckDialogs: DeckDialogs,
    isShownMoreDeckMenu: MutableState<Path?>,
    colors: ExtendedColors,
    activity: Activity,
    scope: CoroutineScope,
    application: App
): ListDecksAdapter(
    decksViewModel,
    cutPasteDeckViewModel,
    deckDialogs,
    isShownMoreDeckMenu,
    colors,
    activity,
    scope,
    application
) {
    @SuppressLint("NotifyDataSetChanged")
    fun loadItems() {
        decksViewModel.loadLastUsed {
            scope.launch {
                notifyDataSetChanged()
            }
        }
    }
}