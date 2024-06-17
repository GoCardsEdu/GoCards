package pl.gocards.ui.decks.kt.recent.view

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import pl.gocards.ui.decks.kt.all.view.CreateNewDeckButton
import pl.gocards.ui.decks.kt.all.view.DiscordMenuItem
import pl.gocards.ui.decks.kt.all.view.ImportCsvMenuItem
import pl.gocards.ui.decks.kt.all.view.ImportDbMenuItem
import pl.gocards.ui.decks.kt.all.view.ImportExcelMenuItem
import pl.gocards.ui.decks.kt.all.view.MoreButton
import pl.gocards.ui.decks.kt.all.view.SettingsMenuItem

/**
 * @author Grzegorz Ziemski
 */
data class ListRecentDecksMenuData(
    val onClickNewDeck: () -> Unit,
    val onClickImportExcel: (() -> Unit)?,
    val onClickImportCsv: (() -> Unit)?,
    val onClickImportDb: () -> Unit,
    val onClickOpenDiscord: () -> Unit,
    val onClickOpenSettings: () -> Unit
)

@Composable
fun ListRecentDecksMenu(input: ListRecentDecksMenuData) {
    CreateNewDeckButton(input.onClickNewDeck)

    val showDropDown = remember { mutableStateOf(false) }
    MoreButton(showDropDown)
    DropdownMenu(showDropDown.value, { showDropDown.value = false }) {
        input.onClickImportExcel?.let {
            ImportExcelMenuItem(showDropDown, it)
        }
        input.onClickImportCsv?.let {
            ImportCsvMenuItem(showDropDown, it)
        }
        ImportDbMenuItem(showDropDown, input.onClickImportDb)
        DiscordMenuItem(showDropDown, input.onClickOpenDiscord)
        SettingsMenuItem(showDropDown, input.onClickOpenSettings)
    }
}