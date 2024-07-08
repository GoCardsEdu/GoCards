package pl.gocards.ui.decks.decks.view.actions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.decks.decks.service.ExportImportDb
import pl.gocards.ui.decks.decks.service.ExportImportDbKtxUtil

/**
 * @author Grzegorz Ziemski
 */
class ImportDbAction(
    private var adapter: ListDecksAdapter,
    private var onRefreshItems: () -> Unit,
    private var activity: Activity,
    private var scope: CoroutineScope
) {

    private var exportImportDbUtil: ExportImportDb? = null

    @Composable
    fun getExportImportDbUtil(): ExportImportDb {
        if (exportImportDbUtil == null) {
            exportImportDbUtil = ExportImportDbKtxUtil(scope, activity)
                .getInstance { onRefreshItems() }
        }
        return exportImportDbUtil!!
    }

    @Composable
    @SuppressLint("ComposableNaming")
    fun onClickImportDb(): () -> Unit {
        val util = getExportImportDbUtil()
        return {
            val folder = adapter.getCurrentFolder().toString()
            util.launchImportDb(folder)
        }
    }
}