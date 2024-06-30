package pl.gocards.ui.decks.decks.service

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.gocards.R


data class ExportImportDb(
    val launchExportDb: (dbPath: String) -> Unit,
    val launchImportDb: (importToFolder: String) -> Unit
)

/**
 * @author Grzegorz Ziemski
 */
class ExportImportDbKtxUtil(
    private val scope: CoroutineScope,
    activity: Activity
) : ExportImportDbUtil(activity) {

    @Composable
    fun getInstance(onSuccess: () -> Unit): ExportImportDb {
        val exportDbLauncher = initExportDbLauncher()
        val importDbLauncher = initImportDbLauncher(onSuccess)

        return ExportImportDb(
            launchImportDb = { importToFolder ->
                launchImportDb(importDbLauncher, importToFolder)
            },
            launchExportDb = { dbPath ->
                launchExportDb(exportDbLauncher, dbPath)
            }
        )
    }

    /**
     * D_R_12 Export database
     */
    @Composable
    private fun initExportDbLauncher(): ActivityResultLauncher<String> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/vnd.sqlite3"),
            onResult = { uri -> uri?.let { exportDb(uri, path) } }
        )
    }

    /**
     * D_C_13 Import database
     */
    @Composable
    private fun initImportDbLauncher(onSuccess: () -> Unit): ActivityResultLauncher<Array<String>> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri -> uri?.let { importDb(path, uri) { onSuccess() } } }
    }

    /**
     * D_R_12 Export database
     */
    override fun exportDb(exportToUri: Uri, dbPath: String) {
        scope.launch {
            withContext(Dispatchers.IO) {
                super.exportDb(exportToUri, dbPath)

                scope.launch {
                    showShortToastMessage(
                        String.format(
                            context.getString(R.string.decks_list_export_db_toast),
                            getDeckName(dbPath)
                        )
                    )
                }
            }
        }
    }

    /**
     * D_C_13 Import database
     */
    @SuppressLint("Range", "CheckResult")
    override fun importDb(
        importToFolder: String,
        importedDbUri: Uri,
        onSuccess: (deckDbPath: String) -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                super.importDb(importToFolder, importedDbUri) {
                    scope.launch {
                        onSuccess(it)
                        showShortToastMessage(
                            String.format(
                                context.getString(R.string.decks_list_import_db_toast),
                                getDeckName(it)
                            )
                        )
                    }
                }
            }
        }
    }
}