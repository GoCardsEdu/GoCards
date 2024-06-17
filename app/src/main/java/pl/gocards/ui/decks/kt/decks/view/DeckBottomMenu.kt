package pl.gocards.ui.decks.kt.decks.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.common.popup.menu.IconPopupMenuItem
import java.nio.file.Path

data class DeckBottomMenuInput(
    val isShown: MutableState<Path?> = mutableStateOf(null),
    val onSync: ((deckDbPath: Path) -> Unit)?,
    val onClickExportExcel: ((deckDbPath: Path) -> Unit)?,
    val onExportCsv: ((deckDbPath: Path) -> Unit)?,
    val onExportDb: (deckDbPath: Path) -> Unit,
    val onDeckSettings: (deckDbPath: Path) -> Unit,
) {
    fun show(deckDbPath: Path) {
        isShown.value = deckDbPath
    }
    fun hide() {
        isShown.value = null
    }
}

/**
 * Strange, but the status bar does not change color if added by #addViewToRoot
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DeckBottomMenu(input: DeckBottomMenuInput) {
    val sheetState = rememberModalBottomSheetState()
    val deckDbPath = input.isShown.value
    if (deckDbPath != null) {
        ModalBottomSheet(
            onDismissRequest = {
                input.hide()
            },
            sheetState = sheetState,

            ) {
            Column(modifier = Modifier.padding(bottom = 50.dp)) {
                input.onSync?.let {
                    IconPopupMenuItem(
                        icon = Icons.Rounded.Sync,
                        text = R.string.cards_list_menu_sync,
                        onClick = { it(deckDbPath) },
                        onDismiss = { input.hide() }
                    )
                }
                input.onClickExportExcel?.let {
                    IconPopupMenuItem(
                        icon = Icons.Rounded.Upload,
                        text = R.string.cards_list_menu_export_excel,
                        onClick = { it(deckDbPath) },
                        onDismiss = { input.hide() }
                    )
                }
                input.onExportCsv?.let {
                    IconPopupMenuItem(
                        icon = Icons.Rounded.Upload,
                        text = R.string.cards_list_menu_export_csv,
                        onClick = { it(deckDbPath) },
                        onDismiss = { input.hide() }
                    )
                }
                IconPopupMenuItem(
                    icon = Icons.Rounded.Upload,
                    text = R.string.decks_list_menu_bottom_export_db,
                    onClick = { input.onExportDb(deckDbPath) },
                    onDismiss = { input.hide() }
                )
                IconPopupMenuItem(
                    icon = Icons.Rounded.Settings,
                    text = R.string.decks_list_menu_bottom_deck_settings,
                    onClick = {input.onDeckSettings(deckDbPath)  },
                    onDismiss = { input.hide() }
                )
            }
        }
    }
}