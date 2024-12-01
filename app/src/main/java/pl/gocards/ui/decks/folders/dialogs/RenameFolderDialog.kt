package pl.gocards.ui.decks.folders.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.common.SANITIZE_FILENAME_REGEX

/**
 * F_U_04 Rename the folder
 * @author Grzegorz Ziemski
 */
data class RenameFolderDialogInput(
    val currentName: String,
    val onRenameFolder: (newFolderName: String) -> Unit,
    val onDismiss: () -> Unit
)

/**
 * F_U_04 Rename the folder
 * @author Grzegorz Ziemski
 */
@Composable
fun RenameFolderDialog(
    input: RenameFolderDialogInput
) {
    var newName by remember { mutableStateOf(input.currentName) }
    AlertDialog(
        title = { Text(text = stringResource(R.string.decks_list_folder_rename_dialog_title)) },
        text = {
            Column {
                Text(
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    text = stringResource(R.string.decks_list_folder_rename_dialog_message)
                )
                TextField(
                    value = newName,
                    onValueChange = { newName = it.replace(SANITIZE_FILENAME_REGEX, "") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                )
            }
        },
        onDismissRequest = {
            input.onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newName.isNotEmpty()) {
                        input.onRenameFolder(newName)
                    }
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    input.onDismiss()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}