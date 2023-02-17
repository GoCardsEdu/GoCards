package pl.softfly.flashcards.ui.deck.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.softfly.flashcards.ui.kt.theme.FlashCardsTheme

@Preview(showBackground = true)
@Composable
fun PreviewDarkModeAlertDialog() {
    val isDarkTheme = true
    FlashCardsTheme(isDarkTheme = isDarkTheme, preview = true) {
        FileAutoSyncAlertDialog(
            isDarkTheme = isDarkTheme,
            autoSync = remember { mutableStateOf(true) },
            fileNameDb = "File Name.xlsx",
            autoSyncDb = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileAutoSyncAlertDialog(
    isDarkTheme: Boolean,

    // Current values
    autoSync: State<Boolean>,

    // DB values
    autoSyncDb: Boolean,
    fileNameDb: String?,

    //Actions
    onSelectRadio: (selected: Boolean) -> Unit = {},
    onSave: () -> Unit = {},
    onDismiss: () -> Unit = {},

    ) {
    SettingsAlertDialog(
        title = "Auto-sync with the file",
        body = {
            // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
            Column(Modifier.selectableGroup()) {
                Row(Modifier.padding(bottom = 8.dp)) {
                    Text(text = "If you want to change a file, go to the cards, click on \"Sync\" and choose the auto-sync option.")
                }
                RadioButton(
                    "Off",
                    false,
                    autoSync,
                    onSelectRadio
                )
                if (autoSyncDb) {
                    RadioButton(
                        "On",
                        true,
                        autoSync,
                        onSelectRadio
                    )
                    if (autoSync.value) {
                        Row {
                            OutlinedTextField(
                                value = fileNameDb ?: " ",
                                onValueChange = { },
                                label = { Text("File name") },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        onSave = onSave,
        onDismiss = onDismiss,
    )
}