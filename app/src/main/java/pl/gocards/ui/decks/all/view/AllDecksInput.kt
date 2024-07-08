package pl.gocards.ui.decks.all.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.ui.common.RecyclerViewFactory
import pl.gocards.ui.decks.decks.view.EmptyFolderData
import pl.gocards.ui.decks.decks.view.actions.DeckActions
import pl.gocards.ui.decks.decks.view.actions.ImportDbAction
import pl.gocards.ui.decks.search.SearchFoldersDecksAdapter
import pl.gocards.ui.discover.premium.PremiumViewModel
import pl.gocards.ui.decks.decks.view.actions.ImportDeckAction
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.home.view.StartActivityActions

/**
 * @author Grzegorz Ziemski
 */
data class AllDecksInput(
    val onBack: () -> Unit,
    val folderName: String?,
    val searchBarInput: SearchBarInput?,
    val menu: ListAllDecksMenuData,
    val page: ListAllDecksPageData
)

class AllDecksInputFactory(
    private var adapter: SearchFoldersDecksAdapter,
    private var fileSyncViewModel: FileSyncViewModel?,
    private var premiumViewModel: PremiumViewModel,
    private var startActivity: StartActivityActions,

    private var onBack: () -> Unit,
    private var onRefreshItems: () -> Unit,

    private var activity: Activity,
    private var owner: LifecycleOwner,
    private var application: App
)  {
    @Composable
    fun create(): AllDecksInput {
        val scope = owner.lifecycleScope

        val importFile = ImportDeckAction(adapter, fileSyncViewModel, onRefreshItems, activity, owner)
        val deckActions = DeckActions(adapter, onRefreshItems, application)
        val importDb = ImportDbAction(adapter, onRefreshItems, activity, scope)

        return AllDecksInput(
            onBack = onBack,
            folderName = adapter.decksViewModel.folderName.value?.toString(),
            searchBarInput = createSearchBarInput(adapter, premiumViewModel),
            menu = ListAllDecksMenuData(
                onClickSearch = onClickSearch(),
                onClickNewDeck = { deckActions.onClickNewDeck() },
                onClickNewFolder = onClickNewFolder(),
                onClickImportExcel = importFile.onClickImport(),
                onClickImportCsv = importFile.onClickImport(),
                onClickImportDb = importDb.onClickImportDb(),
                onClickOpenDiscord = { startActivity.openDiscord() },
                onClickOpenSettings = { startActivity.startAppSettingsActivity() }
            ),
            page = ListAllDecksPageData(
                isEmptyFolder = isEmptyFolder(),
                recyclerView = RecyclerViewFactory().create(activity, adapter),
                folderPath = adapter.decksViewModel.folderPath.value,
                emptyFolder = EmptyFolderData(
                    onClickNewDeck = { deckActions.onClickNewDeck() },
                    onClickNewFolder = onClickNewFolder(),
                    onClickCreateSampleDeck = { deckActions.onClickCreateSampleDeck() },
                    onClickImport = importFile.onClickImport()
                ),
                showDeckPasteBar = adapter.cutPasteDeckViewModel?.showDeckPasteBar?.value ?: false,
                showFolderPasteBar = adapter.cutPasteFolderViewModel?.showFolderPasteBar?.value
                    ?: false,
                cutPath = adapter.cutPasteDeckViewModel?.cutPath?.value,
                onPaste = { adapter.paste() },
                onCancel = {
                    adapter.cutPasteDeckViewModel?.clear()
                    adapter.cutPasteFolderViewModel?.clear()
                }
            )
        )
    }

    private fun createSearchBarInput(
        adapter: SearchFoldersDecksAdapter,
        premiumViewModel: PremiumViewModel
    ): SearchBarInput? {
        if (!premiumViewModel.isPremium().value) return null

        return SearchBarInput(
            searchQuery = adapter.searchFoldersDecksViewModel.getSearchQuery(),
            isSearchActive = adapter.searchFoldersDecksViewModel.isSearchActive(),
            onSearchEnd = { adapter.clearSearch() },
            onSearchChange = { query -> adapter.searchItems(query) }
        )
    }

    private fun isEmptyFolder(): Boolean {
        return adapter.decksViewModel.isEmptyFolder.value
                && adapter.foldersViewModel.isEmptyFolder.value
    }

    private fun onClickSearch(): (() -> Unit)? {
        return if (premiumViewModel.isPremium().value) {
            { adapter.searchFoldersDecksViewModel.enableSearch() }
        } else null
    }

    private fun onClickNewFolder(): (() -> Unit)? {
        return if (premiumViewModel.isPremium().value) {
            {
                val folder = adapter.getCurrentFolder()
                adapter.folderDialogs.showCreateFolderDialog(folder)
            }
        } else null
    }
}