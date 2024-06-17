package pl.gocards.ui.decks.kt.all

import android.app.Activity
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.ui.decks.kt.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.kt.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.kt.decks.model.EditDecksViewModel
import pl.gocards.ui.decks.kt.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.kt.folders.dialogs.FolderDialogs
import pl.gocards.ui.decks.kt.folders.model.CutPasteFolderViewModel
import pl.gocards.ui.decks.kt.folders.model.EditFoldersViewModel
import pl.gocards.ui.decks.kt.folders.model.ListFoldersViewModel
import pl.gocards.ui.decks.kt.search.SearchFoldersDecksAdapter
import pl.gocards.ui.decks.kt.search.SearchFoldersDecksViewModel
import pl.gocards.ui.kt.theme.ExtendedColors
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class AllDecksAdapterFactory {

    fun create(
        listDecksViewModel: ListDecksViewModel,
        listFoldersViewModel: ListFoldersViewModel,
        searchFoldersDecksViewModel: SearchFoldersDecksViewModel,
        isShownMoreDeckMenu: MutableState<Path?>,
        onSuccess: () -> Unit,
        colors: ExtendedColors,
        activity: Activity,
        owner: LifecycleOwner,
        application: App
    ): SearchFoldersDecksAdapter {
        return SearchFoldersDecksAdapter(
            listDecksViewModel,
            CutPasteDeckViewModel(application),
            listFoldersViewModel,
            CutPasteFolderViewModel(
                listFoldersViewModel.currentFolder,
                application,
                owner
            ),
            searchFoldersDecksViewModel,
            DeckDialogs(
                EditDecksViewModel(application),
                onSuccess,
                activity,
                owner.lifecycleScope,
                application
            ),
            FolderDialogs(
                EditFoldersViewModel(application),
                onSuccess,
                activity,
                owner.lifecycleScope,
                application
            ),
            isShownMoreDeckMenu,
            colors,
            activity,
            owner.lifecycleScope,
            application,
        )
    }
}