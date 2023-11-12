package pl.gocards.ui.settings.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Suppress("unused")
@Composable
fun SettingsAlertDialog(
    title: String,
    body: @Composable (() -> Unit)? = null,
    onSave: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onSave() })
            { Text(text = "OK") }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() })
            { Text(text = "Cancel") }
        },
        title = { Text(text = title) },
        text = body,
        //https://issuetracker.google.com/issues/221643630?pli=1
        //properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Suppress("unused")
@Composable
fun <T> RadioButton(
    label: String,
    option: T,
    currentValue: State<T>,
    onSelect: (selected: T) -> Unit = {},
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (currentValue.value == option),
                onClick = { onSelect(option) },
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.RadioButton(
            selected = (currentValue.value == option),
            onClick = null // null recommended for accessibility with screen readers
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}