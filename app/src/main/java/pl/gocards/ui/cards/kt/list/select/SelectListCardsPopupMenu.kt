package pl.gocards.ui.cards.kt.list.select

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.runtime.Composable
import pl.gocards.R
import pl.gocards.ui.cards.kt.list.display.IconMenuItem

/**
 * C_02_01 When no card is selected and tap on the card, show the popup menu.
 * @author Grzegorz Ziemski
 */
@Composable
fun SelectListCardsPopupMenu(
    adapter: SelectListCardsAdapter,
    holder: SelectCardViewHolder,
    onDismiss: () -> Unit
) {
    val position = holder.bindingAdapterPosition
    val editingLocked = adapter.editingLocked

    if (adapter.isCardSelected(position)) {
        IconMenuItem(
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            text = R.string.cards_list_popup_select_mode_select_card,
            onClick = { adapter.select(holder) },
            onDismiss = onDismiss
        )
    } else{
        IconMenuItem(
            icon = Icons.Filled.Deselect,
            text = R.string.cards_list_popup_select_mode_unselect_card,
            onClick = { adapter.deselect(holder) },
            onDismiss = onDismiss
        )
    }
    if (!editingLocked) {
        IconMenuItem(
            icon = Icons.Default.DeleteSweep,
            text = R.string.cards_list_popup_select_mode_delete_selected_cards,
            onClick = { adapter.deleteSelected() },
            onDismiss = onDismiss
        )
        if (position == 0) {
            IconMenuItem(
                icon = Icons.Rounded.ContentPaste,
                text = R.string.cards_list_popup_select_mode_paste_card_before,
                onClick = { adapter.pasteBefore(position) },
                onDismiss = onDismiss
            )
            IconMenuItem(
                icon = Icons.Rounded.ContentPaste,
                text = R.string.cards_list_popup_select_mode_paste_card_after,
                onClick = { adapter.pasteAfter(position) },
                onDismiss = onDismiss
            )
        } else {
            IconMenuItem(
                icon = Icons.Rounded.ContentPaste,
                text = R.string.paste,
                onClick = { adapter.pasteAfter(position) },
                onDismiss = onDismiss
            )
        }
    }
}