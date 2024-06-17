package pl.gocards.filesync.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.common.AlertDialog2Buttons

/**
 * 1.1 If Yes, display a message that deck editing is blocked and end use case.
 * @author Grzegorz Ziemski
 */
@Composable
fun EditingDeckLockedDialog(onDismiss: () -> Unit = {}) {
    AlertDialog2Buttons(
        dialogTitle = stringResource(R.string.filesync_editing_deck_locked_dialog_title),
        dialogText = stringResource(R.string.filesync_editing_deck_locked_dialog_message),
        confirmationText = stringResource(R.string.ok),
        cancelText = null,
        onConfirmation = onDismiss,
        onCancel = onDismiss,
        onDismiss = onDismiss,
    )
}