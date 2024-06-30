package pl.gocards.ui.decks.decks.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.runtime.Composable
import pl.gocards.R
import pl.gocards.ui.common.popup.menu.IconPopupMenuItem

@Composable
fun DeckPopupMenu(
    onDismiss: () -> Unit = {},
    onClickListCards: () -> Unit,
    onClickNewCard: () -> Unit,
    onClickCutDeck: () -> Unit,
    onClickRenameDeck: () -> Unit,
    onClickDeleteDeck: () -> Unit,
    onClickShowMenuBottom: () -> Unit,
) {
    IconPopupMenuItem(
        icon = Icons.AutoMirrored.Rounded.List,
        text = R.string.decks_list_popup_deck_list_cards,
        onClick = onClickListCards,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.AddCard,
        text = R.string.decks_list_popup_deck_add_card,
        onClick = onClickNewCard,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.ContentCut,
        text = R.string.cut,
        onClick = onClickCutDeck,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.Edit,
        text = R.string.rename,
        onClick = onClickRenameDeck,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.Delete,
        text = R.string.delete,
        onClick = onClickDeleteDeck,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.MoreHoriz,
        text = R.string.more,
        onClick = onClickShowMenuBottom,
        onDismiss = onDismiss
    )
}