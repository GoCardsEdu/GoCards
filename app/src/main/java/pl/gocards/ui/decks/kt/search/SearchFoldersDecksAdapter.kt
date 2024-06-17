package pl.gocards.ui.decks.kt.search

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import pl.gocards.App
import pl.gocards.ui.decks.kt.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.kt.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.kt.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.kt.folders.ListFoldersAdapter
import pl.gocards.ui.decks.kt.folders.dialogs.FolderDialogs
import pl.gocards.ui.decks.kt.folders.model.CutPasteFolderViewModel
import pl.gocards.ui.decks.kt.folders.model.ListFoldersViewModel
import pl.gocards.ui.kt.theme.ExtendedColors
import java.nio.file.Path

/**
 * D_R_03 Search decks
 * @author Grzegorz Ziemski
 */
class SearchFoldersDecksAdapter(
    decksViewModel: ListDecksViewModel,
    cutPasteDeckViewModel: CutPasteDeckViewModel?,
    foldersViewModel: ListFoldersViewModel,
    cutPasteFolderViewModel: CutPasteFolderViewModel?,
    val searchFoldersDecksViewModel: SearchFoldersDecksViewModel,
    deckDialogs: DeckDialogs,
    folderDialogs: FolderDialogs,
    isShownMoreDeckMenu: MutableState<Path?>,
    colors: ExtendedColors,
    activity: Activity,
    scope: CoroutineScope,
    application: App,
) : ListFoldersAdapter(
    decksViewModel,
    cutPasteDeckViewModel,
    foldersViewModel,
    cutPasteFolderViewModel,
    deckDialogs,
    folderDialogs,
    isShownMoreDeckMenu,
    colors,
    activity,
    scope,
    application
) {

    /**
     * D_R_03 Search decks
     */
    override fun searchItems(query: String) {
        searchFoldersDecksViewModel.search(query)
        super.searchItems(query)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSearch() {
        searchFoldersDecksViewModel.disableSearch()
        loadItems()
    }

}