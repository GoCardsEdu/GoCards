package pl.gocards.ui.decks.all.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.cards.list.search.SearchTextField
import pl.gocards.ui.theme.AppBar

/**
 * @author Grzegorz Ziemski
 */
data class SearchBarInput(
    val searchQuery: State<String?>,
    val isSearchActive: State<Boolean>,
    val onSearchEnd: () -> Unit = {},
    val onSearchChange: (String) -> Unit = {},
)

/**
 * @author Grzegorz Ziemski
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun ListAllDecksTopBar(
    onBack: () -> Unit,
    folderName: String?,
    search: SearchBarInput,
    menu: ListAllDecksMenuData,
    isDarkTheme: Boolean
) {
    AppBar(
        isDarkTheme = isDarkTheme,
        title = {
            if (search.isSearchActive.value) {
                SearchTextField(search.searchQuery, search.onSearchChange)
            } else {
                Text(
                    text = folderName ?: stringResource(R.string.app_name),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            }
        },
        onBack = {
            if (search.isSearchActive.value) {
                search.onSearchEnd()
            } else {
                onBack()
            }
        },
        actions = {
            ListAllDecksMenu(menu)
        },
        modifier = Modifier
    )
}