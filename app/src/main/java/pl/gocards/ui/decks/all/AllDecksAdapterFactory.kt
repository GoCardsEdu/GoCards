package pl.gocards.ui.decks.all

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.ui.decks.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.decks.model.EditDecksViewModel
import pl.gocards.ui.decks.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.folders.dialogs.FolderDialogs
import pl.gocards.ui.decks.folders.model.CutPasteFolderViewModel
import pl.gocards.ui.decks.folders.model.EditFoldersViewModel
import pl.gocards.ui.decks.folders.model.ListFoldersViewModel
import pl.gocards.ui.decks.search.SearchFoldersDecksAdapter
import pl.gocards.ui.decks.search.SearchFoldersDecksViewModel
import pl.gocards.ui.theme.ExtendedColors
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
        isPremium: Boolean,
        onSuccess: () -> Unit,
        colors: ExtendedColors,
        startActivityForResultLauncher: ActivityResultLauncher<Intent>,
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
            isPremium,
            colors,
            startActivityForResultLauncher,
            activity,
            owner.lifecycleScope,
            application,
        )
    }
}