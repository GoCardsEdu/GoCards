package pl.gocards.ui.cards.kt.slider.page.edit

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Save
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
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
@Composable
fun EditCardPageMenu(
    onClickMenuSaveCard: () -> Unit = {},
    onClickMenuDeleteCard: () -> Unit = {},
    onClickMenuNewCard: () -> Unit = {},
) {
    IconButton(onClick = onClickMenuSaveCard) {
        Icon(Icons.Outlined.Save, stringResource(R.string.save))
    }
    IconButton(onClick = onClickMenuDeleteCard) {
        Icon(Icons.Filled.Delete, stringResource(R.string.delete))
    }
    val showDropDown = remember { mutableStateOf(false) }
    IconButton(onClick = { showDropDown.value = true }) {
        Icon(Icons.Filled.MoreVert, null)
    }
    DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
        SliderDropdownMenuItem(
            icon = Icons.Filled.Add,
            text = R.string.cards_slider_new_card,
            showDropDown = showDropDown,
            onClick = onClickMenuNewCard
        )
    }
}