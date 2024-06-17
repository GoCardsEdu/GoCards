package pl.gocards.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun AlertDialog2Buttons(
    dialogTitle: String,
    dialogText: String,
    confirmationText: String = "Confirm",
    cancelText: String? = "Dismiss",
    onConfirmation: () -> Unit = {},
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmationText)
            }
        },
        dismissButton = {
            if (cancelText != null) {
                TextButton(
                    onClick = {
                        onCancel()
                    }
                ) {
                    Text(cancelText)
                }
            }
        }
    )
}
