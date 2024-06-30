package pl.gocards.ui.settings.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun PreviewDarkModeAlertDialog() {
    AppTheme(isDarkTheme = true, preview = true) {
        DarkModeAlertDialog(
            DarkModeAlertDialogEntity(
                darkMode = remember { mutableStateOf(AppConfig.DARK_MODE_OPTIONS[1]) },
                darkModeDb = remember { mutableStateOf(AppConfig.DARK_MODE_OPTIONS[1]) }
            )
        )
    }
}

@SuppressWarnings("unused")
data class DarkModeAlertDialogEntity(
    val darkMode: State<String>,
    val darkModeDb: State<String>,
    val onSelect: (selected: String) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

/**
 * S_U_09 App: Dark mode
 * @author Grzegorz Ziemski
 */
@Composable
fun DarkModeAlertDialog(entity: DarkModeAlertDialogEntity){
    AlertDialog(
        onDismissRequest = { entity.onDismiss() },
        confirmButton = {
            TextButton(onClick = { entity.onSave() })
            {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { entity.onDismiss() })
            {
                Text(
                    text = stringResource(R.string.cancel),
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.app_settings_dark_mode))
        },
        text = {
            // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
            Column(Modifier.selectableGroup()) {
                AppConfig.DARK_MODE_OPTIONS.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == entity.darkMode.value),
                                onClick = { entity.onSelect(text) },
                                role = Role.RadioButton
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == entity.darkMode.value),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    )
}