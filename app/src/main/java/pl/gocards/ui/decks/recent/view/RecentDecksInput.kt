package pl.gocards.ui.decks.recent.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.ui.auth.AuthLauncher
import pl.gocards.ui.common.RecyclerViewFactory
import pl.gocards.ui.decks.decks.view.actions.DeckActions
import pl.gocards.ui.decks.decks.view.EmptyFolderData
import pl.gocards.ui.decks.decks.view.actions.ImportDbAction
import pl.gocards.ui.decks.recent.ListRecentDecksAdapter
import pl.gocards.ui.decks.decks.view.actions.ImportDeckAction
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.home.view.StartActivityActions

/**
 * @author Grzegorz Ziemski
 */
data class RecentDecksInput(
    val onBack: () -> Unit,
    val menu: ListRecentDecksMenuData,
    val page: ListRecentDecksPageData
)

class RecentDecksInputFactory(
    private var adapter: ListRecentDecksAdapter,
    private var fileSyncViewModel: FileSyncViewModel?,
    private var startActivity: StartActivityActions,
    private var logIn: AuthLauncher,

    private var onBack: () -> Unit,
    private var onRefreshItems: () -> Unit,

    private var activity: Activity,
    private var owner: LifecycleOwner,
    private var application: App
)  {

    @Composable
    fun create(): RecentDecksInput {
        val scope = owner.lifecycleScope

        val importFile = ImportDeckAction(adapter, fileSyncViewModel, onRefreshItems, activity, owner)
        val deckActions = DeckActions(adapter, onRefreshItems, application)
        val importDb = ImportDbAction(adapter, onRefreshItems, activity, scope)

        return RecentDecksInput(
            onBack = onBack,
            menu = ListRecentDecksMenuData(
                onClickNewDeck = { deckActions.onClickNewDeck() },
                onClickImportExcel = importFile.onClickImport(),
                onClickImportCsv = importFile.onClickImport(),
                onClickImportDb = importDb.onClickImportDb(),
                onClickOpenDiscord = { startActivity.openDiscord() },
                onClickOpenSettings = { startActivity.startAppSettingsActivity() },
                onClickLogOut = { logIn.logOut() },
                isLoggedIn = logIn.token.value != null
            ),
            page = ListRecentDecksPageData(
                isEmptyFolder = adapter.decksViewModel.isEmptyFolder.value,
                recyclerView = RecyclerViewFactory().create(activity, adapter),
                emptyFolder = EmptyFolderData(
                    onClickNewDeck = { deckActions.onClickNewDeck() },
                    onClickCreateSampleDeck = { deckActions.onClickCreateSampleDeck() },
                    onClickImport = importFile.onClickImport()
                )
            )
        )
    }
}