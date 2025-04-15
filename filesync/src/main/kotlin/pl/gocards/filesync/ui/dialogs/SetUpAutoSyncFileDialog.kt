package pl.gocards.filesync.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.common.AlertDialog2Buttons

data class SetUpAutoSyncFileDialogInput(
    val onConfirmation: () -> Unit = { },
    val onCancel: () -> Unit = {},
    val onDismiss: () -> Unit =  { }
)

/**
 * FS_PRO_S.5. Show a dialog asking if the deck should be auto-synced.
 * @author Grzegorz Ziemski
 */
@Composable
fun SetUpAutoSyncFileDialog(
    input: SetUpAutoSyncFileDialogInput
) {
    SetUpAutoSyncFileDialog(
        onConfirmation = input.onConfirmation,
        onCancel = input.onCancel,
        onDismiss = input.onDismiss
    )
}

/**
 * FS_PRO_S.5. Show a dialog asking if the deck should be auto-synced.
 * @author Grzegorz Ziemski
 */
@Composable
fun SetUpAutoSyncFileDialog(
    onConfirmation: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog2Buttons(
        dialogTitle = stringResource(R.string.filesyncpro_dialog_auto_sync_setup_title),
        dialogText = stringResource(R.string.filesyncpro_dialog_auto_sync_setup_message),
        confirmationText = stringResource(R.string.yes),
        cancelText = stringResource(R.string.no),
        onConfirmation = onConfirmation,
        onCancel = onCancel,
        onDismiss = onDismiss,
    )
}