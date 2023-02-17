package pl.softfly.flashcards.ui.deck.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.softfly.flashcards.entity.app.AppConfig
import pl.softfly.flashcards.ui.kt.theme.FlashCardsTheme

@Preview(showBackground = true)
@Composable
fun PreviewShowEdgeBarAlertDialog() {
    val isDarkTheme = true
    FlashCardsTheme(isDarkTheme = isDarkTheme, preview = true) {
        EdgeBarAlertDialog(
            isDarkTheme = isDarkTheme,
            title = "Show left edge bar",
            edgeBar = remember { mutableStateOf(AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED) },
        )
    }
}

@Composable
fun EdgeBarAlertDialog(
    isDarkTheme: Boolean,
    title: String,
    edgeBar: State<String>,

    //Actions
    onSelectRadio: (selected: Any) -> Unit = {},
    onSave: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    SettingsAlertDialog(
        title = title,
        body = {
            // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
            Column(Modifier.selectableGroup()) {
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_Off),
                    AppConfig.EDGE_BAR_Off,
                    edgeBar, onSelectRadio
                )
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS),
                    AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS,
                    edgeBar, onSelectRadio
                )
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED),
                    AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED,
                    edgeBar, onSelectRadio
                )
            }
        },
        onSave = onSave,
        onDismiss = onDismiss,
    )
}

fun getShowEdgeBarLabel(value: String): String {
    when (value) {
        AppConfig.EDGE_BAR_Off -> return "Off"
        AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS -> return "Show the learning status"
        AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED -> return "Show recently synced"
    }
    return ""
}