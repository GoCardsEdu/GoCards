package pl.gocards.ui.main.kt

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
import kotlinx.coroutines.CoroutineScope
import org.xmlpull.v1.XmlPullParser
import pl.gocards.App
import pl.gocards.R
import pl.gocards.ui.decks.kt.all.view.ListAllDecksMenuData
import pl.gocards.ui.decks.kt.all.view.ListAllDecksPageData
import pl.gocards.ui.decks.kt.all.view.SearchBarInput
import pl.gocards.ui.decks.kt.decks.ListDecksAdapter
import pl.gocards.ui.decks.kt.decks.service.CreateSampleDeck
import pl.gocards.ui.decks.kt.decks.service.ExportImportDb
import pl.gocards.ui.decks.kt.decks.service.ExportImportDbKtxUtil
import pl.gocards.ui.decks.kt.decks.view.DeckBottomMenuInput
import pl.gocards.ui.decks.kt.decks.view.EmptyFolderData
import pl.gocards.ui.decks.kt.recent.ListRecentDecksAdapter
import pl.gocards.ui.decks.kt.recent.view.ListRecentDecksMenuData
import pl.gocards.ui.decks.kt.recent.view.ListRecentDecksPageData
import pl.gocards.ui.decks.kt.search.SearchFoldersDecksAdapter
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.filesync_pro.FileSyncProLauncherFactory
import pl.gocards.ui.settings.SettingsActivity
import java.nio.file.Path

@Immutable
@OptIn(ExperimentalFoundationApi::class)
data class MainScreenInput(
    val isDarkTheme: Boolean,
    val recentDecks: RecentDecks,
    val allDecks: AllDecks,
    val deckBottomMenu: DeckBottomMenuInput,
    val fileSync: FileSyncLauncherInput?,
    val pagerState: PagerState
)

data class RecentDecks(
    val onBack: () -> Unit,
    val menu: ListRecentDecksMenuData,
    val page: ListRecentDecksPageData
)

data class AllDecks(
    val onBack: () -> Unit,
    val folderName: String?,
    val searchBarInput: SearchBarInput,
    val menu: ListAllDecksMenuData,
    val page: ListAllDecksPageData
)

/**
 * @author Grzegorz Ziemski
 */
class MainScreenInputFactory {

    private lateinit var activity: Activity
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

        activity: Activity,
        owner: LifecycleOwner,
        scope: CoroutineScope = owner.lifecycleScope
    ): MainScreenInput {
        this.activity = activity
        this.recentAdapter = recentAdapter
        this.allAdapter = allAdapter

        val application = activity.applicationContext as App
        val context = activity

        val fileSyncInput = fileSyncViewModel?.let {
            FileSyncLauncherFactory
                .getInstance()
                ?.getInstance(it, activity, scope)
        }

        val onClickSync = FileSyncProLauncherFactory
            .getInstance()
            ?.getInstance(activity, owner)

        val exportImportDbUtil = ExportImportDbKtxUtil(scope, activity)
            .getInstance { loadItems() }

        return MainScreenInput(
            isDarkTheme = application.darkMode ?: isSystemInDarkTheme(),
            pagerState = rememberPagerState(pageCount = { 3 }),
            recentDecks = getRecentDecks(
                this.recentAdapter,
                onBack,
                fileSyncInput,
                exportImportDbUtil,
                application
            ),
            allDecks = getAllDecks(
                this.allAdapter,
                onBack,
                fileSyncInput,
                exportImportDbUtil,
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
                onExportCsv = if (fileSyncInput != null)
                    { deckDbPath -> fileSyncInput.onClickExportCsv(deckDbPath.toString()) }
                else null,
                onExportDb = { exportImportDbUtil.launchExportDb(it.toString()) },
                onDeckSettings = { startDeckSettingsActivity(it.toString()) },
            ),
            fileSync = fileSyncInput
        )
    }

    private fun getRecentDecks(
        adapter: ListRecentDecksAdapter,
        onBack: () -> Unit,
        fileSyncInput: FileSyncLauncherInput?,
        exportImportDbUtil: ExportImportDb,
        application: Application
    ): RecentDecks {
        val folder = adapter.getCurrentFolder()

        return RecentDecks(
            onBack = onBack,
            menu = ListRecentDecksMenuData(
                onClickNewDeck = {
                    adapter.deckDialogs.showCreateDeckDialog(folder)
                },
                onClickImportExcel = if (fileSyncInput != null) {
                    { fileSyncInput.onClickImport(folder.toString()) { loadItems() } }
                } else null,
                onClickImportCsv = if (fileSyncInput != null) {
                    { fileSyncInput.onClickImport(folder.toString()) { loadItems() } }
                } else null,
                onClickImportDb = {
                    exportImportDbUtil.launchImportDb(folder.toString())
                },
                onClickOpenDiscord = { openDiscord() },
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
                    onClickImport = if (fileSyncInput != null) {
                        { onClickImport(adapter, fileSyncInput) }
                    } else null
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
        context: Context
    ): AllDecks {
        val application = context.applicationContext as App


        return AllDecks(
            onBack = onBack,
            folderName = adapter.decksViewModel.folderName.value?.toString(),
            searchBarInput = SearchBarInput(
                searchQuery = adapter.searchFoldersDecksViewModel.getSearchQuery(),
                isSearchActive = adapter.searchFoldersDecksViewModel.isSearchActive(),
                onSearchEnd = { adapter.clearSearch() },
                onSearchChange = { query ->
                    adapter.searchItems(query)
                },
            ),
            menu = ListAllDecksMenuData(
                onClickSearch = { adapter.searchFoldersDecksViewModel.enableSearch() },
                onClickNewDeck = {
                    val folder = adapter.getCurrentFolder()
                    adapter.deckDialogs.showCreateDeckDialog(folder)
                },
                onClickNewFolder = {
                    val folder = adapter.getCurrentFolder()
                    adapter.folderDialogs.showCreateFolderDialog(folder)
                },
                onClickImportExcel = if (fileSyncInput != null) {
                    { onClickImport(adapter, fileSyncInput) }
                } else null,
                onClickImportCsv = if (fileSyncInput != null) {
                    { onClickImport(adapter, fileSyncInput) }
                } else null,
                onClickImportDb = {
                    val folder = adapter.getCurrentFolder().toString()
                    exportImportDbUtil.launchImportDb(folder)
                },
                onClickOpenDiscord = { openDiscord() },
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
                    onClickNewFolder = {
                        val folder = adapter.getCurrentFolder()
                        adapter.folderDialogs.showCreateFolderDialog(folder)
                    },
                    onClickCreateSampleDeck = {
                        val folder = adapter.getCurrentFolder()
                        CreateSampleDeck(application).create(folder) { loadItems() }
                    },
                    onClickImport = if (fileSyncInput != null) {
                        { onClickImport(adapter, fileSyncInput) }
                    } else null
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

    private fun startAppSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }

    private fun startDeckSettingsActivity(dbPath: String) {
        val intent = Intent(activity, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.DECK_DB_PATH, dbPath)
        activity.startActivity(intent)
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