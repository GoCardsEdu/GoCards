package pl.gocards.ui.explore

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.common.SliderDropdownMenuItem

/**
 * @author Grzegorz Ziemski
 */
data class ExploreMenuData(
    val onClickLogOut: () -> Unit,
)

@Composable
fun ExploreMenu(input: ExploreMenuData) {
    val showDropDown = remember { mutableStateOf(false) }
    MoreButton(showDropDown)
    DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
        LogOutMenuItem(showDropDown, input.onClickLogOut)
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
fun LogOutMenuItem(
    showDropDown: MutableState<Boolean>,
    onClick: () -> Unit
) {
    SliderDropdownMenuItem(
        icon = Icons.AutoMirrored.Rounded.Logout,
        text = R.string.decks_list_menu_log_out,
        showDropDown = showDropDown,
        onClick = onClick
    )
}