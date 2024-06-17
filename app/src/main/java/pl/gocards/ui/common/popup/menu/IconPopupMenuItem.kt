package pl.gocards.ui.common.popup.menu

import androidx.annotation.StringRes
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun IconPopupMenuItem(
    icon: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text = stringResource(text)) },
        leadingIcon = { Icon(icon, stringResource(text)) },
        onClick = {
            onDismiss()
            onClick()
        }
    )
}