package pl.gocards.ui.cards.list

import android.content.Context
import android.util.AttributeSet
import android.util.Xml
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.xmlpull.v1.XmlPullParser
import pl.gocards.App
import pl.gocards.R
import pl.gocards.db.deck.DeckDbUtil
import pl.gocards.ui.cards.list.display.CalcCardIdWidth
import pl.gocards.ui.cards.list.display.ListCardsMenuData
import pl.gocards.ui.cards.list.edge_bar.filesync.FileSyncEdgeBarListCardsAdapter
import pl.gocards.ui.cards.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.list.select.SelectCardTouchHelper
import pl.gocards.ui.cards.list.select.SelectListCardsMenuData
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.filesync_pro.FileSyncProLauncherFactory
import pl.gocards.ui.ai.AIChatContract
import pl.gocards.ui.settings.SettingsActivity
import android.content.Intent

/**
 * @author Grzegorz Ziemski
 */
data class ListCardsScaffoldInput(
    val isDarkTheme: Boolean,
    val preview: Boolean,

    val recyclerView: RecyclerView,
    val snackbarHostState: SnackbarHostState,
    val countSelectedCard: State<Int>,

    val topBar: ListCardsTopBarInput,
    val isSyncInProgress: State<Boolean>?,
    val fileSync: FileSyncLauncherInput?,
    val onClickSync: ((deckDbPath: String, onSuccess: () -> Unit) -> Unit)?,
    val aiChat: AIChatInput? = null
)

data class AIChatInput(
    val viewModel: AIChatContract,
    val showBottomSheet: MutableState<Boolean>,
    val fabTextVisible: State<Boolean> = mutableStateOf(true),
    val fabVisible: State<Boolean> = mutableStateOf(true),
    val pendingScrollToCardIds: MutableState<List<Int>>,
    val onLogin: () -> Unit,
    val onOpenSettings: () -> Unit
)

class ListCardsScaffoldInputFactory {

    @Composable
    fun getInstance(
        activity: ListCardsActivity
    ): ListCardsScaffoldInput {
        return getInstance(
            activity.deckDbPath,
            activity,
            activity.adapter!!,
            activity.viewModel,
            activity.fileSyncViewModel,
            activity.premiumViewModel.isPremium().value,
            { activity.handleOnBackPressed() },
            activity,
            activity.application as App
        )
    }

    @Composable
    private fun getInstance(
        deckDbPath: String,
        activity: ListCardsActivity,
        adapter: FileSyncEdgeBarListCardsAdapter,
        viewModel: SearchListCardsViewModel,
        fileSyncViewModel: FileSyncViewModel?,
        isPremium: Boolean,
        onBack: () -> Unit = {},
        owner: LifecycleOwner,
        application: App
    ): ListCardsScaffoldInput {

        val recyclerView = getRecyclerView(activity, adapter)

        val fileSyncInput = fileSyncViewModel?.let {
            FileSyncLauncherFactory
                .getInstance()
                ?.getInstance(it, activity, activity)
        }

        val onClickSync = FileSyncProLauncherFactory.getInstance()
            ?.getInstance(activity, owner)

        val onClickExportCsv = if (fileSyncInput != null) {
            { fileSyncInput.onClickExportCsv(deckDbPath) }
        } else null



        val aiChatViewModel = activity.aiChatViewModel

        val fabTextVisible = remember { mutableStateOf(true) }
        val fabVisible = remember { mutableStateOf(true) }
        val showAiChat = remember { mutableStateOf(false) }
        return ListCardsScaffoldInput(
            isDarkTheme = application.getDarkMode() ?: isSystemInDarkTheme(),
            preview = false,
            recyclerView = recyclerView,
            snackbarHostState = adapter.snackbarHostState,
            countSelectedCard = adapter.selectViewModel.getCountSelectedState(),
            topBar = ListCardsTopBarInput(
                onBack = onBack,
                deckName = DeckDbUtil.getDeckName(deckDbPath),
                idWidth = CalcCardIdWidth.getInstance().maxIdWidth,
                searchQuery = viewModel.getSearchQuery(),
                isSearchActive = viewModel.isSearchActive(),
                onSearchEnd = {
                    adapter.clearSearch()
                },
                onSearchChange = { adapter.search(it) },
                onDeselectAll = { adapter.deselectAll() },
                selectListCardsMenu = SelectListCardsMenuData(
                    onClickSearch = { viewModel.enableSearch() },
                    onClickDeleteSelected = { adapter.deleteSelected() }
                ),
                listCardsMenu = ListCardsMenuData(
                    onClickSearch = if (isPremium) {
                        { viewModel.enableSearch() }
                    } else null,
                    onClickNewCard = { activity.startNewCardActivity() },
                    onClickSync = if (onClickSync != null) {
                        { onClickSync(deckDbPath) { adapter.loadCards() } }
                    } else null,
                    onClickExportExcel = if (fileSyncInput != null) {
                        { fileSyncInput.onClickExportExcel(deckDbPath) }
                    } else null,
                    onClickExportCsv = onClickExportCsv,
                    onClickSettings = { activity.startDeckSettingsActivity() },
                ),
            ),
            isSyncInProgress = fileSyncViewModel?.inProgress(deckDbPath)
                ?.observeAsState(initial = false),
            fileSync = fileSyncInput,
            onClickSync = onClickSync,

            aiChat = aiChatViewModel?.let { aiViewModel ->
                AIChatInput(
                    viewModel = aiViewModel,
                    showBottomSheet = showAiChat,
                    fabTextVisible = fabTextVisible,
                    fabVisible = fabVisible,
                    pendingScrollToCardIds = activity.pendingScrollToCardIds,
                    onLogin = { onLogin(activity, aiViewModel) },
                    onOpenSettings = { onOpenSettings(activity, aiViewModel, deckDbPath) }
                )
            }
        )
    }


    private fun onLogin(
        activity: ListCardsActivity,
        aiViewModel: AIChatContract
    ) {
        activity.authLauncher.launch()
    }

    private fun onOpenSettings(
        activity: ListCardsActivity,
        aiViewModel: AIChatContract,
        deckDbPath: String
    ) {
        val intent = Intent(activity, SettingsActivity::class.java).apply {
            putExtra(SettingsActivity.DECK_DB_PATH, deckDbPath)
            putExtra(SettingsActivity.INITIAL_TAB, SettingsActivity.TAB_AI)
            putExtra(SettingsActivity.SHOW_API_KEY_DIALOG, true)
        }
        activity.startActivity(intent)
    }

    @Composable
    private fun getRecyclerView(
        activity: ListCardsActivity,
        adapter: FileSyncEdgeBarListCardsAdapter
    ): RecyclerView {
        val recyclerView = RecyclerView(
            LocalContext.current,
            getScrollbarAttributeSet(activity)
        ).apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(LocalContext.current)
        }

        recyclerView.getViewTreeObserver()
            .addOnDrawListener(
                CalcCardIdWidth.getInstance().calcIdWidth(recyclerView)
            )

        val itemTouchHelper = ItemTouchHelper(SelectCardTouchHelper(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return recyclerView
    }

    private fun getScrollbarAttributeSet(context: Context): AttributeSet? {
        return try {
            val parser: XmlPullParser = context.resources.getXml(R.xml.scrollbar)
            parser.next()
            parser.nextTag()
            Xml.asAttributeSet(parser)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}