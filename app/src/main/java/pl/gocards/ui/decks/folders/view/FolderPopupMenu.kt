package pl.gocards.ui.decks.folders.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import pl.gocards.R
import pl.gocards.ui.common.popup.menu.IconPopupMenuItem

@Composable
fun FolderPopupMenu(
    onDismiss: () -> Unit = {},
    onClickRenameFolder: () -> Unit,
    onClickDeleteFolder: () -> Unit,
    onClickCutFolder: () -> Unit,
) {
    IconPopupMenuItem(
        icon = Icons.Rounded.Edit,
        text = R.string.rename,
        onClick = onClickRenameFolder,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.Delete,
        text = R.string.delete,
        onClick = onClickDeleteFolder,
        onDismiss = onDismiss
    )
    IconPopupMenuItem(
        icon = Icons.Rounded.ContentCut,
        text = R.string.cut,
        onClick = onClickCutFolder,
        onDismiss = onDismiss
    )
}