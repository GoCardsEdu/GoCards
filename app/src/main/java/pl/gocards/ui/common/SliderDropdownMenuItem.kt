package pl.gocards.ui.common

import androidx.annotation.StringRes
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun SliderDropdownMenuItem(
    icon: ImageVector,
    @StringRes text: Int,
    showDropDown: MutableState<Boolean> = mutableStateOf(false),
    @SuppressWarnings("unused")
    onClick: () -> Unit = {}
) {
    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(text),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingIcon = { Icon(icon, stringResource(text)) },
        onClick = {
            showDropDown.value = false
            onClick()
        }
    )
}