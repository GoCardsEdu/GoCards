package pl.gocards.ui.decks.decks.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.gocards.R

/**
 * D_R_04 No cards to repeat
 * @author Grzegorz Ziemski
 */
@Composable
fun NoCardsToRepeatDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.decks_list_no_more_cards_repeat)) },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}