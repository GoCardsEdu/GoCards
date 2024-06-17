package pl.gocards.ui.decks.kt.decks.dialogs

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
 * D_R_08 Delete the deck
 * @author Grzegorz Ziemski
 */
data class DeleteDeckDialogInput(
    val onDeleteDeck: () -> Unit,
    val onDismiss: () -> Unit
)

/**
 * D_R_08 Delete the deck
 * @author Grzegorz Ziemski
 */
@Composable
fun DeleteDeckDialog(
    input: DeleteDeckDialogInput
) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.decks_list_deck_delete_dialog_title)) },
        text = {
            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp),
                text = stringResource(R.string.decks_list_deck_delete_dialog_message)
            )
        },
        onDismissRequest = {
            input.onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    input.onDeleteDeck()
                }
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