package pl.gocards.ui.decks.all.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import pl.gocards.R
import pl.gocards.ui.common.SliderDropdownMenuItem

/**
 * @author Grzegorz Ziemski
 */
data class ListAllDecksMenuData(
    val onClickSearch: (() -> Unit)?,
    val onClickNewDeck: () -> Unit,
    val onClickNewFolder: (() -> Unit)?,
    val onClickImportExcel: (() -> Unit)?,
    val onClickImportCsv: (() -> Unit)?,
    val onClickImportDb: () -> Unit,
    val onClickOpenDiscord: () -> Unit,
    val onClickOpenSettings: () -> Unit
)

@Composable
fun ListAllDecksMenu(input: ListAllDecksMenuData) {
    input.onClickSearch?.let { SearchButton(it) }
    CreateNewDeckButton(input.onClickNewDeck)

    val showDropDown = remember { mutableStateOf(false) }
    MoreButton(showDropDown)
    DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
        input.onClickNewFolder?.let {
            CreateNewFolderMenuItem(showDropDown, it)
        }
        input.onClickImportExcel?.let {
            ImportExcelMenuItem(showDropDown, it)
        }
        input.onClickImportCsv?.let {
            ImportCsvMenuItem(showDropDown, it)
        }
        ImportDbMenuItem(showDropDown, input.onClickImportDb)
        DiscordMenuItem(showDropDown, input.onClickOpenDiscord)
        SettingsMenuItem(showDropDown, input.onClickOpenSettings)
    }
}

@Composable
fun SearchButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Filled.Search, stringResource(R.string.search))
    }
}

@Composable
fun CreateNewDeckButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Outlined.Add, stringResource(R.string.decks_list_menu_new_deck))
    }
}

@Composable
fun MoreButton(showDropDown: MutableState<Boolean>) {
    IconButton(
        onClick = { showDropDown.value = true }) {
        Icon(Icons.Filled.MoreVert, stringResource(R.string.more))
    }
}

@Composable
fun CreateNewFolderMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.Rounded.CreateNewFolder,
        text = R.string.decks_list_menu_new_folder,
        showDropDown = showDropDown,
        onClick = onClick
    )
}

@Composable
fun ImportExcelMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.Rounded.Download,
        text = R.string.decks_list_menu_import_excel,
        showDropDown = showDropDown,
        onClick = onClick
    )
}

@Composable
fun ImportCsvMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.Rounded.Download,
        text = R.string.decks_list_menu_import_csv,
        showDropDown = showDropDown,
        onClick = onClick
    )
}

@Composable
fun ImportDbMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.Rounded.Download,
        text = R.string.decks_list_menu_import_db,
        showDropDown = showDropDown,
        onClick = onClick
    )
}

@Composable
fun DiscordMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = ImageVector.vectorResource(id = R.drawable.discord),
        text = R.string.discord,
        showDropDown = showDropDown,
        onClick = onClick
    )
}

@Composable
fun SettingsMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.Rounded.Settings,
        text = R.string.decks_list_menu_app_settings,
        showDropDown = showDropDown,
        onClick = onClick
    )
}