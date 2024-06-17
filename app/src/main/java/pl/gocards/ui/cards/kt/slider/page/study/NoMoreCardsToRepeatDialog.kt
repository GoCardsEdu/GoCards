package pl.gocards.ui.cards.kt.slider.page.study

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.gocards.R

/**
 * C_R_31 No more cards to repeat
 * @author Grzegorz Ziemski
 */
@Composable
@SuppressWarnings("unused")
fun NoMoreCardsToRepeatDialog(onDismissRequest: () -> Unit = {}) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.card_study_no_more_cards_repeat))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        },
    )
}