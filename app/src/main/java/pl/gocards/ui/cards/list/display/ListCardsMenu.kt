package pl.gocards.ui.cards.list.display

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.sharp.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.common.SliderDropdownMenuItem

/**
 * @author Grzegorz Ziemski
 */
data class ListCardsMenuData(
    val onClickSearch: () -> Unit = {},
    val onClickNewCard: () -> Unit = {},
    val onClickSync: (() -> Unit)? = {},
    val onClickExportExcel: (() -> Unit)? = {},
    val onClickExportCsv: (() -> Unit)? = {},
    val onClickSettings: () -> Unit = {},
)

@Composable
fun ListCardsMenu(
    input: ListCardsMenuData,
    editingLocked: Boolean
) {
    IconButton(onClick = input.onClickSearch) {
        Icon(Icons.Filled.Search, stringResource(R.string.search))
    }
    if (!editingLocked) {
        IconButton(onClick = input.onClickNewCard) {
            Icon(Icons.Outlined.Add, stringResource(R.string.cards_list_menu_new_card))
        }
    }
    val showDropDown = remember { mutableStateOf(false) }
    IconButton(
        onClick = { showDropDown.value = true }) {
        Icon(Icons.Filled.MoreVert, stringResource(R.string.more))
    }
    DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
        if (!editingLocked) {
            input.onClickSync?.let {
                SliderDropdownMenuItem(
                    icon = Icons.Sharp.Sync,
                    text = R.string.cards_list_menu_sync,
                    showDropDown = showDropDown,
                    onClick = it
                )
            }
            input.onClickExportExcel?.let {
                SliderDropdownMenuItem(
                    icon = Icons.Rounded.Upload,
                    text = R.string.cards_list_menu_export_excel,
                    showDropDown = showDropDown,
                    onClick = it
                )
            }
            input.onClickExportCsv?.let {
                SliderDropdownMenuItem(
                    icon = Icons.Rounded.Upload,
                    text = R.string.cards_list_menu_export_csv,
                    showDropDown = showDropDown,
                    onClick = it
                )
            }
        }
        SliderDropdownMenuItem(
            icon = Icons.Filled.Settings,
            text = R.string.cards_list_menu_deck_settings,
            showDropDown = showDropDown,
            onClick = input.onClickSettings
        )
    }
}