package pl.gocards.ui.main

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.ui.decks.all.AllDecksAdapterFactory
import pl.gocards.ui.decks.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.decks.model.ListDecksViewModelFactory
import pl.gocards.ui.decks.folders.model.ListFoldersViewModel
import pl.gocards.ui.decks.recent.ListRecentDecksAdapter
import pl.gocards.ui.decks.recent.RecentDecksAdapterFactory
import pl.gocards.ui.decks.search.SearchFoldersDecksAdapter
import pl.gocards.ui.decks.search.SearchFoldersDecksViewModel
import pl.gocards.ui.decks.search.SearchFoldersDecksViewModelFactory
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.theme.AppTheme
import pl.gocards.ui.theme.ExtendedTheme
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path


/**
 * @author Grzegorz Ziemski
 */
@Immutable
class MainActivity : AppCompatActivity() {

    private var currentPage = 0

    var recentAdapter: ListRecentDecksAdapter? = null
        private set

    var allAdapter: SearchFoldersDecksAdapter? = null
        private set

    var fileSyncViewModel: FileSyncViewModel? = null
        private set

    private lateinit var searchFoldersDecksViewModel: SearchFoldersDecksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createDbRootFolder()

        /**
         * It needs to be an AppCompatActivity instead of a ComponentActivity,
         * because dark mode does not work with the XML adapter
         */
        supportActionBar?.hide()

        val application = this.applicationContext as App
        val owner = this

        fileSyncViewModel = FileSyncViewModel.getInstance(owner, application)

        val listDecksViewModelFactory = ListDecksViewModelFactory(application)
        val listDecksViewModel = ViewModelProvider(this, listDecksViewModelFactory)[ListDecksViewModel::class.java]

        val listFoldersViewModel = ListFoldersViewModel(application)

        searchFoldersDecksViewModel = ViewModelProvider(this, SearchFoldersDecksViewModelFactory(application))[SearchFoldersDecksViewModel::class.java]

        setContent {
            val isShownMoreDeckMenu: MutableState<Path?> = remember { mutableStateOf(null) }

            AppTheme {
                recentAdapter = RecentDecksAdapterFactory().create(
                    isShownMoreDeckMenu,
                    { loadDecks() },
                    ExtendedTheme.colors,
                    this,
                    this.lifecycleScope,
                    application
                )
                allAdapter = AllDecksAdapterFactory().create(
                    listDecksViewModel,
                    listFoldersViewModel,
                    searchFoldersDecksViewModel,
                    isShownMoreDeckMenu,
                    { loadDecks() },
                    ExtendedTheme.colors,
                    this,
                    this,
                    application
                )
                CreateView()
            }

            LaunchedEffect(true) {
                if (searchFoldersDecksViewModel.isSearchActive.value) {
                    val query = searchFoldersDecksViewModel.getSearchQuery().value
                    if (query != null) {
                        allAdapter?.searchItems(query)
                    } else {
                        loadDecks()
                    }
                } else {
                    loadDecks()
                }
            }
        }

        this.onBackPressedDispatcher.addCallback(this) {
            this@MainActivity.handleOnBackPressed()
        }
    }

    @Composable
    private fun CreateView() {
        MainScreenScaffold(
            MainScreenInputFactory().create(this),
            setCurrentPage = { currentPage = it },
        )
    }

    fun handleOnBackPressed() {
        if (currentPage == 1) {
            if (searchFoldersDecksViewModel.isSearchActive.value) {
                allAdapter?.clearSearch()
            } else if (allAdapter?.openFolderUp() == false) {
                super.finish()
            }
        } else {
            super.finish()
        }
    }

    @Override
    override fun onResume() {
        super.onResume()
        recentAdapter?.loadItems()
        allAdapter?.loadItems()
    }

    private fun loadDecks() {
        recentAdapter!!.loadItems()
        allAdapter!!.loadItems()
    }


    private fun createDbRootFolder() {
        try {
            Files.createDirectories(
                AppDeckDbUtil.getInstance(applicationContext)
                    .getDbFolder(applicationContext)
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
