package pl.gocards.ui.settings.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.kt.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun PreviewAutoSyncAlertDialog() {
    val isDarkTheme = true
    AppTheme(isDarkTheme = isDarkTheme, preview = true) {
        AutoSyncAlertDialog(
            AutoSyncDialogEntity(
                autoSync = remember { mutableStateOf(true) },
                fileNameDb = remember { mutableStateOf("File Name.xlsx") },
                autoSyncDb = remember { mutableStateOf(true) }
            )
        )
    }
}

data class AutoSyncDialogEntity(
    val autoSync: State<Boolean>,

    val autoSyncDb: State<Boolean>,
    val fileNameDb: State<String?>,

    val onSelectRadio: (selected: Boolean) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

/**
 * S_U_02 This deck: Auto-sync with the file
 * @author Grzegorz Ziemski
 */
@Composable
@SuppressLint("PrivateResource")
fun AutoSyncAlertDialog(entity: AutoSyncDialogEntity) {

    SettingsAlertDialog(
        title = stringResource(R.string.settings_auto_sync_title),
        body = {
            // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
            Column(Modifier.selectableGroup()) {
                Row(Modifier.padding(bottom = 8.dp)) {
                    Text(text = stringResource(R.string.settings_auto_sync_desc))
                }
                RadioButton(
                    stringResource(R.string.off),
                    false,
                    entity.autoSync,
                    entity.onSelectRadio
                )
                if (entity.autoSyncDb.value) {
                    RadioButton(
                        stringResource(R.string.on),
                        true,
                        entity.autoSync,
                        entity.onSelectRadio
                    )
                    if (entity.autoSync.value) {
                        Row {
                            OutlinedTextField(
                                value = entity.fileNameDb.value ?: stringResource(R.string.settings_auto_sync_no_file),
                                onValueChange = { },
                                label = { Text(stringResource(R.string.settings_auto_sync_file_name)) },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        onSave = entity.onSave,
        onDismiss = entity.onDismiss,
    )
}