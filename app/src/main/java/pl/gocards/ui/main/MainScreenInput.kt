package pl.gocards.ui.main

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Xml
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.elevation.SurfaceColors
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import pl.gocards.App
import pl.gocards.R
import pl.gocards.ui.decks.all.view.ListAllDecksMenuData
import pl.gocards.ui.decks.all.view.ListAllDecksPageData
import pl.gocards.ui.decks.all.view.SearchBarInput
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.decks.decks.service.CreateSampleDeck
import pl.gocards.ui.decks.decks.service.ExportImportDb
import pl.gocards.ui.decks.decks.service.ExportImportDbKtxUtil
import pl.gocards.ui.decks.decks.view.DeckBottomMenuInput
import pl.gocards.ui.decks.decks.view.EmptyFolderData
import pl.gocards.ui.decks.recent.ListRecentDecksAdapter
import pl.gocards.ui.decks.recent.view.ListRecentDecksMenuData
import pl.gocards.ui.decks.recent.view.ListRecentDecksPageData
import pl.gocards.ui.decks.search.SearchFoldersDecksAdapter
import pl.gocards.ui.discover.premium.BillingClient
import pl.gocards.ui.discover.Discover
import pl.gocards.ui.discover.premium.PremiumViewModel
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.filesync_pro.FileSyncProLauncherFactory
import pl.gocards.ui.settings.SettingsActivity
import pl.gocards.util.Config
import pl.gocards.util.FirebaseAnalyticsHelper
import java.nio.file.Path

@Immutable
@OptIn(ExperimentalFoundationApi::class)
data class MainScreenInput(
    val isDarkTheme: Boolean,
    val pagerState: PagerState,
    val recentDecks: RecentDecks,
    val allDecks: AllDecks,
    val deckBottomMenu: DeckBottomMenuInput,
    val fileSync: FileSyncLauncherInput?,
    val discover: Discover
)

data class RecentDecks(
    val onBack: () -> Unit,
    val menu: ListRecentDecksMenuData,
    val page: ListRecentDecksPageData
)

data class AllDecks(
    val onBack: () -> Unit,
    val folderName: String?,
    val searchBarInput: SearchBarInput?,
    val menu: ListAllDecksMenuData,
    val page: ListAllDecksPageData
)

/**
 * @author Grzegorz Ziemski
 */
class MainScreenInputFactory {

    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var recentAdapter: ListRecentDecksAdapter
    private lateinit var allAdapter: SearchFoldersDecksAdapter

    @Composable
    fun create(
        activity: MainActivity
    ): MainScreenInput {
        return create(
            activity.recentAdapter!!,
            activity.allAdapter!!,

            { activity.handleOnBackPressed() },

            activity.allAdapter!!.isShownMoreDeckMenu,
            activity.fileSyncViewModel,
            activity.premiumViewModel,
            activity.billingClient,

            activity,
            activity
        )
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun create(
        recentAdapter: ListRecentDecksAdapter,
        allAdapter: SearchFoldersDecksAdapter,
        onBack: () -> Unit,

        isShownMoreDeckMenu: MutableState<Path?>,
        fileSyncViewModel: FileSyncViewModel?,
        premiumViewModel: PremiumViewModel,
        billingClient: BillingClient,

        activity: Activity,
        owner: LifecycleOwner
    ): MainScreenInput {
        this.activity = activity
        this.context = activity
        this.recentAdapter = recentAdapter
        this.allAdapter = allAdapter

        val application = activity.applicationContext as App
        val scope = owner.lifecycleScope
        val context = activity

        val fileSyncInput = fileSyncViewModel?.let {
            FileSyncLauncherFactory
                .getInstance()
                ?.getInstance(it, activity, owner)
        }

        val onClickSync = FileSyncProLauncherFactory
            .getInstance()
            ?.getInstance(activity, owner)

        val exportImportDbUtil = ExportImportDbKtxUtil(scope, activity)
            .getInstance { loadItems() }

        val onExportCsv: ((deckDbPath: Path) -> Unit)? = if (fileSyncInput != null)
            { deckDbPath -> fileSyncInput.onClickExportCsv(deckDbPath.toString()) }
        else null

        val analytics = FirebaseAnalyticsHelper.getInstance(application)

        return MainScreenInput(
            isDarkTheme = application.darkMode ?: isSystemInDarkTheme(),
            pagerState = rememberPagerState(pageCount = { 3 }),
            recentDecks = getRecentDecks(
                this.recentAdapter,
                onBack,
                fileSyncInput,
                exportImportDbUtil,
                analytics,
                application
            ),
            allDecks = getAllDecks(
                this.allAdapter,
                onBack,
                fileSyncInput,
                exportImportDbUtil,
                analytics,
                premiumViewModel,
                context
            ),
            deckBottomMenu = DeckBottomMenuInput(
                isShown = isShownMoreDeckMenu,
                onSync = if (onClickSync != null)
                    { deckDbPath -> onClickSync(deckDbPath.toString()) { loadItems() } }
                else null,
                onClickExportExcel = if (fileSyncInput != null)
                    { deckDbPath -> fileSyncInput.onClickExportExcel(deckDbPath.toString()) }
                else null,
                onExportCsv = onExportCsv,
                onExportDb = { exportImportDbUtil.launchExportDb(it.toString()) },
                onDeckSettings = { startDeckSettingsActivity(it.toString()) },
            ),
            fileSync = fileSyncInput,
            discover = Discover(
                isPremium = premiumViewModel.isPremium(),
                isPremiumSwitch = premiumViewModel.isPremiumSwitch,
                setPremium = { premiumViewModel.isPremiumSwitch.value = true },
                onClickDiscord = {
                    analytics.discoverOpenDiscord()
                    openDiscord()
                },
                onClickBuyPremium = {
                    scope.launch {
                        if (isPremiumMockEnabled()) {
                            premiumViewModel.enablePremium()
                        } else {
                            billingClient.launch(activity)
                        }
                    }
                },
                onDisableSubscription = {
                    if (isPremiumMockEnabled()) {
                        scope.launch {
                            premiumViewModel.disablePremium()
                        }
                    } else {
                        openSubscriptions()
                    }
                },
                onOpenSubscriptions = {
                    openSubscriptions()
                }
            )
        )
    }

    private fun getRecentDecks(
        adapter: ListRecentDecksAdapter,
        onBack: () -> Unit,
        fileSyncInput: FileSyncLauncherInput?,
        exportImportDbUtil: ExportImportDb,
        analytics: FirebaseAnalyticsHelper,
        application: Application
    ): RecentDecks {
        val folder = adapter.getCurrentFolder()

        val onClickImport = if (fileSyncInput != null) {
            { fileSyncInput.onClickImport(folder.toString()) { loadItems() } }
        } else null

        return RecentDecks(
            onBack = onBack,
            menu = ListRecentDecksMenuData(
                onClickNewDeck = {
                    adapter.deckDialogs.showCreateDeckDialog(folder)
                },
                onClickImportExcel = if (fileSyncInput != null) {
                    { fileSyncInput.onClickImport(folder.toString()) { loadItems() } }
                } else null,
                onClickImportCsv = onClickImport,
                onClickImportDb = {
                    exportImportDbUtil.launchImportDb(folder.toString())
                },
                onClickOpenDiscord = {
                    analytics.menuOpenDiscord()
                    openDiscord()
                },
                onClickOpenSettings = { startAppSettingsActivity() }
            ),
            page = ListRecentDecksPageData(
                isEmptyFolder = adapter.decksViewModel.isEmptyFolder.value,
                recyclerView = getRecyclerView(activity, adapter),
                emptyFolder = EmptyFolderData(
                    onClickNewDeck = {
                        adapter.deckDialogs.showCreateDeckDialog(folder)
                    },
                    onClickCreateSampleDeck = {
                        CreateSampleDeck(application).create(folder) { loadItems() }
                    },
                    onClickImport = onClickImport
                )
            )
        )
    }

    @Composable
    private fun getAllDecks(
        adapter: SearchFoldersDecksAdapter,
        onBack: () -> Unit,
        fileSyncInput: FileSyncLauncherInput?,
        exportImportDbUtil: ExportImportDb,
        analytics: FirebaseAnalyticsHelper,
        premiumViewModel: PremiumViewModel,
        context: Context
    ): AllDecks {
        val application = context.applicationContext as App

        val onClickImport = if (fileSyncInput != null) {
            { onClickImport(adapter, fileSyncInput) }
        } else null

        return AllDecks(
            onBack = onBack,
            folderName = adapter.decksViewModel.folderName.value?.toString(),
            searchBarInput = createSearchBarInput(adapter, premiumViewModel),
            menu = ListAllDecksMenuData(
                onClickSearch = if (premiumViewModel.isPremium().value) {
                    { adapter.searchFoldersDecksViewModel.enableSearch() }
                } else null,
                onClickNewDeck = {
                    val folder = adapter.getCurrentFolder()
                    adapter.deckDialogs.showCreateDeckDialog(folder)
                },
                onClickNewFolder = if (premiumViewModel.isPremium().value) {
                    {
                        val folder = adapter.getCurrentFolder()
                        adapter.folderDialogs.showCreateFolderDialog(folder)
                    }
                } else null,
                onClickImportExcel = if (fileSyncInput != null) {
                    { onClickImport(adapter, fileSyncInput) }
                } else null,
                onClickImportCsv = onClickImport,
                onClickImportDb = {
                    val folder = adapter.getCurrentFolder().toString()
                    exportImportDbUtil.launchImportDb(folder)
                },
                onClickOpenDiscord = {
                    analytics.menuOpenDiscord()
                    openDiscord()
                },
                onClickOpenSettings = { startAppSettingsActivity() }
            ),
            page = ListAllDecksPageData(
                isEmptyFolder = adapter.decksViewModel.isEmptyFolder.value
                        && adapter.foldersViewModel.isEmptyFolder.value,
                recyclerView = getRecyclerView(activity, adapter),
                folderPath = adapter.decksViewModel.folderPath.value,
                emptyFolder = EmptyFolderData(
                    onClickNewDeck = {
                        val folder = adapter.getCurrentFolder()
                        adapter.deckDialogs.showCreateDeckDialog(folder)
                    },
                    onClickNewFolder = if (premiumViewModel.isPremium().value) {
                        {
                            val folder = adapter.getCurrentFolder()
                            adapter.folderDialogs.showCreateFolderDialog(folder)
                        }
                    } else null,
                    onClickCreateSampleDeck = {
                        val folder = adapter.getCurrentFolder()
                        CreateSampleDeck(application).create(folder) { loadItems() }
                    },
                    onClickImport = onClickImport
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
            onSearchChange = { query ->
                adapter.searchItems(query)
            }
        )
    }

    private fun loadItems() {
        recentAdapter.loadItems()
        allAdapter.loadItems()
    }

    private fun onClickImport(
        adapter: ListDecksAdapter,
        fileSyncInput: FileSyncLauncherInput,
    ) {
        val folder = adapter.getCurrentFolder().toString()
        fileSyncInput.onClickImport(folder) { loadItems() }
    }

    private fun openDiscord() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://discord.gg/jYyRnD27JP")
        )
        activity.startActivity(intent)
    }

    private fun openSubscriptions() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/account/subscriptions")
        )
        activity.startActivity(intent)
    }

    private fun startAppSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }

    private fun startDeckSettingsActivity(dbPath: String) {
        val intent = Intent(activity, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.DECK_DB_PATH, dbPath)
        activity.startActivity(intent)
    }

    private fun isPremiumMockEnabled(): Boolean {
        return Config.getInstance(context)
            .isPremiumMockEnabled(context)
    }

    private fun getRecyclerView(
        activity: Activity,
        adapter: ListDecksAdapter
    ): RecyclerView {
        val recyclerView = RecyclerView(
            activity,
            getScrollbarAttributeSet(activity)
        ).apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(activity)
            this.addItemDecoration(createDividerItemDecoration(activity))
        }

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

    private fun createDividerItemDecoration(context: Context): DividerItemDecoration {
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val color = SurfaceColors.SURFACE_5.getColor(context)
        val drawable =
            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(color, color))
        drawable.setSize(1, 1)
        divider.setDrawable(drawable)
        return divider
    }
}