package pl.gocards.ui.common

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import org.apache.commons.lang3.exception.ExceptionUtils
import pl.gocards.R
import pl.gocards.ui.kt.theme.AppTheme


fun showExceptionDialog(
    activity: Activity,
    scope: CoroutineScope,
    isWarning: Boolean,
    message: String?,
    throwable: Throwable?
) {
    addViewToRoot(activity, scope) { onDismiss ->
        AppTheme {
            ExceptionDialog(
                isWarning = isWarning,
                message = message,
                throwable = throwable,
                onDismiss = onDismiss
            )
        }
    }
}

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun ExceptionDialog(
    isWarning: Boolean,
    message: String?,
    throwable: Throwable?,
    onDismiss: () -> Unit
) {
    val showException = remember { mutableStateOf(false) }

    AlertDialog(
        title = {
            Text(text = if (isWarning) "Warning" else "Error")
        },
        text = {
            if (showException.value) {
                ExceptionUtils.getStackTrace(throwable)
            } else {
                Text(text = message ?: throwable?.message ?: "Unrecognized error.")
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            if (showException.value) {
                TextButton(
                    onClick = { showException.value = false }
                ) {
                    Text(stringResource(R.string.exception_dialog_hide))
                }
            } else {
                TextButton(
                    onClick = { showException.value = true }
                ) {
                    Text(stringResource(R.string.exception_dialog_show))
                }
            }
        }
    )
}