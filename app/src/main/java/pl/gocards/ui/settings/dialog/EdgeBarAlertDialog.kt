package pl.gocards.ui.settings.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.gocards.R
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.ui.theme.AppTheme

@Preview(showBackground = true)
@Composable
fun PreviewShowEdgeBarAlertDialog() {
    val isDarkTheme = true
    AppTheme(isDarkTheme = isDarkTheme, preview = true) {
        EdgeBarAlertDialog(
            EdgeBarAlertDialogEntity(
                title = "Show left edge bar",
                edgeBar = remember { mutableStateOf(AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED) },
            )
        )
    }
}

data class EdgeBarAlertDialogEntity(
    val title: String = "",
    val edgeBar: State<String>,
    val onSelectRadio: (selected: Any) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

/**
 * S_U_07 All decks: Show left edge bar
 * S_U_08 All decks: Show right edge bar
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 *
 * @author Grzegorz Ziemski
 */
@Composable
fun EdgeBarAlertDialog(entity: EdgeBarAlertDialogEntity) {
    SettingsAlertDialog(
        title = entity.title,
        body = {
            // https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#RadioButton(kotlin.Boolean,kotlin.Function0,androidx.compose.ui.Modifier,kotlin.Boolean,androidx.compose.foundation.interaction.MutableInteractionSource,androidx.compose.material.RadioButtonColors)
            Column(Modifier.selectableGroup()) {
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_OFF),
                    AppConfig.EDGE_BAR_OFF,
                    entity.edgeBar, entity.onSelectRadio
                )
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS),
                    AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS,
                    entity.edgeBar, entity.onSelectRadio
                )
                RadioButton(
                    getShowEdgeBarLabel(AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED),
                    AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED,
                    entity.edgeBar, entity.onSelectRadio
                )
            }
        },
        onSave = entity.onSave,
        onDismiss = entity.onDismiss,
    )
}

@SuppressLint("PrivateResource")
@Composable
fun getShowEdgeBarLabel(value: String): String {
    when (value) {
        AppConfig.EDGE_BAR_OFF -> return stringResource(R.string.off)
        AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS -> return stringResource(R.string.settings_edge_bar_show_learning_status)
        AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED -> return stringResource(R.string.settings_edge_bar_show_recently_synced)
    }
    return ""
}