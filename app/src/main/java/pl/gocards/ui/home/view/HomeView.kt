package pl.gocards.ui.home.view

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.gocards.R
import pl.gocards.ui.decks.all.view.AllDecksInput
import pl.gocards.ui.decks.all.view.ListAllDecksPage
import pl.gocards.ui.decks.all.view.ListAllDecksTopBar
import pl.gocards.ui.decks.decks.view.DeckBottomMenu
import pl.gocards.ui.decks.decks.view.DeckBottomMenuInput
import pl.gocards.ui.decks.recent.view.ListRecentDecksPage
import pl.gocards.ui.decks.recent.view.ListRecentDecksPageData
import pl.gocards.ui.decks.recent.view.ListRecentDecksTopBar
import pl.gocards.ui.discover.DiscoverInput
import pl.gocards.ui.discover.DiscoverPage
import pl.gocards.ui.discover.EmptyDecksTopBar

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun HomeView(
    input: HomeInput,
    setCurrentPage: (Int) -> Unit
) {

    LaunchedEffect(input.pagerState.currentPage) {
        snapshotFlow { input.pagerState.currentPage }
            .collect {
                setCurrentPage(input.pagerState.currentPage)
            }
    }

    Scaffold(
        topBar = {
            when (input.pagerState.currentPage) {
                0 -> {
                    ListRecentDecksTopBar(
                        isDarkTheme = input.isDarkTheme,
                        onBack = input.recentDecks.onBack,
                        menu = input.recentDecks.menu,
                    )
                }
                1 -> {
                    ListAllDecksTopBar(
                        isDarkTheme = input.isDarkTheme,
                        onBack = input.allDecks.onBack,
                        folderName = input.allDecks.folderName,
                        search = input.allDecks.searchBarInput,
                        menu = input.allDecks.menu,

                    )
                }
                else -> {
                    EmptyDecksTopBar(
                        isDarkTheme = input.isDarkTheme,
                        onBack = input.allDecks.onBack,
                    )
                }
            }
        },
        content = { innerPadding ->
            HomePager(
                input.pagerState,
                innerPadding,
                input.recentDecks.page,
                input.allDecks,
                input.deckBottomMenu,
                input.discover
            )
        },
        bottomBar = {
            BottomNavigation(input.pagerState)
        }
    )
}

@Composable
private fun HomePager(
    pagerState: PagerState,
    innerPadding: PaddingValues,
    recentDecks: ListRecentDecksPageData,
    allDecks: AllDecksInput,
    deckBottomMenu: DeckBottomMenuInput,
    discover: DiscoverInput
) {
    val userScrollEnabled = (pagerState.settledPage == 0 && recentDecks.isEmptyFolder)
            || (pagerState.settledPage == 1 && allDecks.page.isEmptyFolder)
            || (pagerState.settledPage == 2)

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = userScrollEnabled,
        flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapAnimationSpec = spring(stiffness = Spring.StiffnessHigh),
        ),
    ) {
        when (it) {
            0 -> {
                ListRecentDecksPage(innerPadding, recentDecks)
            }
            1 -> {
                ListAllDecksPage(innerPadding, allDecks.page)
            }
            2 -> {
                DiscoverPage(innerPadding, discover)
            }
        }
        DeckBottomMenu(deckBottomMenu)
    }
}

@Composable
private fun BottomNavigation(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.AccessTime,
                    contentDescription = stringResource(R.string.bottom_nav_menu_bottom_recent)
                )
            },
            label = { Text(stringResource(R.string.bottom_nav_menu_bottom_recent)) },
            selected = pagerState.currentPage == 0,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Dashboard,
                    contentDescription = stringResource(R.string.bottom_nav_menu_all_decks)
                )
            },
            label = { Text(stringResource(R.string.bottom_nav_menu_all_decks)) },
            selected = pagerState.currentPage == 1,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(1)
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Newspaper,
                    contentDescription = stringResource(R.string.bottom_nav_menu_discover)
                )
            },
            label = { Text(stringResource(R.string.bottom_nav_menu_discover)) },
            selected = pagerState.currentPage == 2,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(2)
                }
            }
        )
    }
}