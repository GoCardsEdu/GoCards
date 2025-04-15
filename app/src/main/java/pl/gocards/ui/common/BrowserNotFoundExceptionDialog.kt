package pl.gocards.ui.common

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.linkifytext.LinkifyText
import kotlinx.coroutines.CoroutineScope
import pl.gocards.R
import pl.gocards.ui.theme.AppTheme
import pl.gocards.util.fromHtmlToAnnotatedString


fun showBrowserNotFoundExceptionDialog(
    activity: Activity,
    scope: CoroutineScope,
    link: String
) {
    addViewToRoot(activity, scope) { onDismiss ->
        AppTheme {
            MessageDialog(
                stringResource(R.string.exception_no_browser_title),
                onDismiss,
                content = {
                    Text(text = stringResource(R.string.exception_no_browser_description) + "\n")
                    SelectionContainer {
                        LinkifyText("<a href='\"$link\"'>$link</a>".fromHtmlToAnnotatedString())
                    }
                }
            )
        }
    }
}

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun MessageDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Column {
                content()
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
        }
    )
}