package pl.gocards.ui.decks.folders.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.gocards.R

/**
 * F_D_05 Delete the folder
 * @author Grzegorz Ziemski
 */
data class DeleteFolderDialogInput(
    val onDeleteFolder: () -> Unit,
    val onDismiss: () -> Unit
)

/**
 * F_D_05 Delete the folder
 * @author Grzegorz Ziemski
 */
@Composable
fun DeleteFolderDialog(
    input: DeleteFolderDialogInput
) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.decks_list_folder_delete_dialog_title)) },
        text = {
            Column {
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    text = stringResource(R.string.decks_list_folder_delete_dialog_message)
                )
            }
        },
        onDismissRequest = {
            input.onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = { input.onDeleteFolder() }
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    input.onDismiss()
                }
            ) {
                Text(stringResource(R.string.no))
            }
        }
    )
}