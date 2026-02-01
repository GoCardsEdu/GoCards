package pl.gocards.ui.settings.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun PreviewOpenAiApiKeyDialog() {
    AppTheme(isDarkTheme = false, preview = true) {
        OpenAiApiKeyDialog(
            ApiKeyDialogEntity(
                apiKey = remember { mutableStateOf("") },
                hasKey = remember { mutableStateOf(false) }
            )
        )
    }
}

data class ApiKeyDialogEntity(
    val apiKey: State<String>,
    val hasKey: State<Boolean>,
    val onValueChange: (String) -> Unit = {},
    val onSave: () -> Unit = {},
    val onClear: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

@Composable
fun OpenAiApiKeyDialog(entity: ApiKeyDialogEntity) {
    AlertDialog(
        onDismissRequest = { entity.onDismiss() },
        confirmButton = {
            TextButton(onClick = { entity.onSave() }) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            if (entity.hasKey.value) {
                TextButton(onClick = { entity.onClear() }) {
                    Text(text = stringResource(R.string.settings_ai_api_key_clear))
                }
            } else {
                TextButton(onClick = { entity.onDismiss() }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        },
        title = {
            Text(text = stringResource(R.string.settings_ai_api_key_dialog_title))
        },
        text = {
            Column {
                ApiKeyDescription()
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = entity.apiKey.value,
                    onValueChange = { entity.onValueChange(it) },
                    label = { Text(stringResource(R.string.settings_ai_api_key_dialog_hint)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun ApiKeyDescription() {
    val url = "https://platform.openai.com/settings/organization/api-keys"
    val linkLabel = "platform.openai.com"
    val fullText = stringResource(R.string.settings_ai_api_key_dialog_description, linkLabel)
    val linkStart = fullText.indexOf(linkLabel)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val linkColor = MaterialTheme.colorScheme.primary

    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = textColor)) {
            append(fullText.substring(0, linkStart))
        }
        withLink(
            LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(
                    style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
                )
            )
        ) {
            append(linkLabel)
        }
        val afterLink = linkStart + linkLabel.length
        if (afterLink < fullText.length) {
            withStyle(SpanStyle(color = textColor)) {
                append(fullText.substring(afterLink))
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall
    )
}
