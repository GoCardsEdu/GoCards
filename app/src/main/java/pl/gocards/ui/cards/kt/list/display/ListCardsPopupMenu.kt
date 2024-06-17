package pl.gocards.ui.cards.kt.list.display

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.cards.kt.list.select.SelectCardViewHolder
import pl.gocards.ui.cards.kt.list.select.SelectListCardsAdapter

/**
 * C_02_01 When no card is selected and tap on the card, show the popup menu.
 * @author Grzegorz Ziemski
 */
@Composable
fun ListCardsPopupMenu(
    adapter: SelectListCardsAdapter,
    holder: SelectCardViewHolder,
    onDismiss: () -> Unit
) {
    val position = holder.bindingAdapterPosition
    val editingLocked = adapter.editingLocked

    if (!editingLocked) {
        IconMenuItem(
            icon = Icons.Rounded.Edit,
            text = R.string.edit,
            onClick = {
                adapter.startEditCardActivity(position)
            },
            onDismiss = onDismiss
        )
    }
    if (!editingLocked) {
        IconMenuItem(
            icon = Icons.Rounded.Delete,
            text = R.string.delete,
            onClick = {
                adapter.delete(position)
            },
            onDismiss = onDismiss
        )
    }
    IconMenuItem(
        icon = Icons.Rounded.ContentCut,
        text = R.string.cards_list_popup_select_card,
        onClick = {
            adapter.select(holder)
        },
        onDismiss = onDismiss
    )
    if (!editingLocked) {
        IconMenuItem(
            icon = Icons.Rounded.AddCard,
            text = R.string.cards_list_popup_add_card_here,
            onClick = { adapter.startNewCardActivity(position) },
            onDismiss = onDismiss
        )
    }
}

@Composable
fun IconMenuItem(
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