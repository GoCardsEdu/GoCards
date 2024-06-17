package pl.gocards.ui.cards.kt.list.select

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
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
data class SelectListCardsMenuData(
    val onClickSearch: () -> Unit,
    val onClickDeleteSelected: () -> Unit
)

@Composable
fun SelectListCardsMenu(
    input: SelectListCardsMenuData,
    editingLocked: Boolean
) {
    IconButton(onClick = input.onClickSearch) {
        Icon(
            Icons.Filled.Search,
            stringResource(R.string.search)
        )
    }
    if (!editingLocked) {
        val showDropDown = remember { mutableStateOf(false) }
        IconButton(
            onClick = { showDropDown.value = true }) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(R.string.more)
            )
        }
        DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
            SliderDropdownMenuItem(
                icon = Icons.Filled.DeleteSweep,
                text = R.string.cards_list_menu_delete_cards,
                showDropDown = showDropDown,
                onClick = input.onClickDeleteSelected
            )
        }
    }
}