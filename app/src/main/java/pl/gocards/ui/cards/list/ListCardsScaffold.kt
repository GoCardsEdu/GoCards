package pl.gocards.ui.cards.list

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import pl.gocards.R
import pl.gocards.ui.ai.AIChatLauncherFactory
import pl.gocards.ui.cards.list.display.scrollToCardsAndHighlight
import pl.gocards.ui.cards.list.display.ListCardsMenu
import pl.gocards.ui.cards.list.display.ListCardsMenuData
import pl.gocards.ui.cards.list.display.RecyclerViewScrollListener
import pl.gocards.ui.cards.list.search.SearchTextField
import pl.gocards.ui.cards.list.select.SelectListCardsMenu
import pl.gocards.ui.cards.list.select.SelectListCardsMenuData
import pl.gocards.ui.theme.AppBar
import pl.gocards.ui.theme.Blue800
import pl.gocards.ui.theme.ExtendedTheme
import pl.gocards.ui.theme.SelectBarColorsTheme
import java.util.Locale

/**
 * @author Grzegorz Ziemski
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ListCardsScaffold(
    input: ListCardsScaffoldInput
) {
    val isSelectionMode: Boolean = input.countSelectedCard.value > 0
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ListCardsTopBar(
                isDarkTheme = input.isDarkTheme,
                isSelectionMode = isSelectionMode,
                countSelectedCard = input.countSelectedCard,
                editingLocked = remember { mutableStateOf(false) },
                input = input.topBar
            )
        },
        snackbarHost = { SnackbarHost(hostState = input.snackbarHostState) },
        content = { innerPadding ->
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (recyclerViewRef, editingLockedRef) = createRefs()

                Box(modifier = Modifier
                    .padding(innerPadding)
                    .constrainAs(recyclerViewRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }) {
                    AndroidView(
                        factory = { input.recyclerView },
                        modifier = Modifier.fillMaxSize(),
                        update = { recyclerView ->
                            recyclerView.clearOnScrollListeners()
                            input.aiChat?.let { aiChat ->
                                val scrollListener = RecyclerViewScrollListener(
                                    isScrollingDown = aiChat.fabVisible as MutableState
                                )
                                recyclerView.addOnScrollListener(scrollListener)
                            }
                        }
                    )

                    input.aiChat?.let { aiChatInput ->
                        AIChatLauncherFactory.getInstance()?.ScaffoldSection(
                            aiChatInput = aiChatInput,
                        )
                        if (!aiChatInput.showBottomSheet.value) {
                            val pendingCardIds = aiChatInput.pendingScrollToCardIds.value
                            if (pendingCardIds.isNotEmpty()) {
                                LaunchedEffect(pendingCardIds) {
                                    input.recyclerView.scrollToCardsAndHighlight(pendingCardIds)
                                    aiChatInput.pendingScrollToCardIds.value = emptyList()
                                }
                            }
                        }
                    }
                }

                if (input.isSyncInProgress?.value == true) {
                    EditingLocked(Modifier.constrainAs(editingLockedRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    })
                }
                if (!input.preview) {
                    SelectBarColorsTheme(input.isDarkTheme, MaterialTheme.colorScheme, isSelectionMode)
                }
            }
        }
    )
}

data class ListCardsTopBarInput(
    val onBack: () -> Unit = {},
    val deckName: String,

    val idWidth: State<Int>,

    val searchQuery: State<String?>,
    val isSearchActive: State<Boolean>,
    val onSearchEnd: () -> Unit = {},
    val onSearchChange: (String) -> Unit = {},

    val onDeselectAll: () -> Unit = {},

    val selectListCardsMenu: SelectListCardsMenuData,
    val listCardsMenu: ListCardsMenuData
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ListCardsTopBar(
    isDarkTheme: Boolean,
    isSelectionMode: Boolean,
    countSelectedCard: State<Int>,
    editingLocked: State<Boolean>,
    input: ListCardsTopBarInput
) {
    Column(Modifier.shadow(elevation = 3.dp)) {
        AppBar(
            isDarkTheme = isDarkTheme,
            title = {
                Column {
                    if (input.isSearchActive.value) {
                        SearchTextField(input.searchQuery, input.onSearchChange)
                    } else if (isSelectionMode) {
                        TopBarTitle(
                            String.format(
                                Locale.getDefault(),
                                stringResource(R.string.cards_list_cards_selected),
                                countSelectedCard.value
                            )
                        )
                    } else {
                        TopBarTitle(input.deckName)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (isSelectionMode)
                    ExtendedTheme.colors.colorItemSelected
                else
                    MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                titleContentColor = if (isDarkTheme) Color.White else Color.Black,
            ),
            onBack = {
                if (input.isSearchActive.value) {
                    input.onSearchEnd()
                } else if (isSelectionMode) {
                    input.onDeselectAll()
                } else {
                    input.onBack()
                }
            },
            actions = {
                if (!input.isSearchActive.value) {
                    if (isSelectionMode) {
                        SelectListCardsMenu(input.selectListCardsMenu, editingLocked.value)
                    } else {
                        ListCardsMenu(input.listCardsMenu, editingLocked.value)
                    }
                }
            }
        )
        TableHeader(input.idWidth.value)
    }
}

@Composable
private fun TopBarTitle(text: String) {
    Text(
        text = text,
        maxLines = 1,
        modifier = Modifier.basicMarquee()
    )
}

@Composable
private fun TableHeader(idWidth: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
            .zIndex(2f)
    ) {
        Text(
            text = stringResource(R.string.cards_list_table_header_id),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier
                .padding(8.dp)
                .weight(.2f)
                .width(idWidth.dp)
        )
        TableCell(
            text = stringResource(R.string.cards_list_table_header_term),
            Modifier.weight(1f)
        )
        TableCell(
            text = stringResource(R.string.cards_list_table_header_definition),
            Modifier.weight(1f)
        )
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun RowScope.TableCell(
    text: String,
    modifier: Modifier,
    maxLines: Int = 4,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        maxLines = maxLines,
        modifier = modifier.padding(8.dp)
    )
}

@Composable
private fun EditingLocked(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = Blue800,
            shape = RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                modifier = Modifier.padding(10.dp, 8.dp),
                text = stringResource(R.string.cards_list_editing_locked),
                color = Color.White
            )
        }
    }
}