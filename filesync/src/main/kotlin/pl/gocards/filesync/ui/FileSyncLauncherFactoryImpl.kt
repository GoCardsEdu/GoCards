package pl.gocards.filesync.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import pl.gocards.App
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.filesync.FileSyncWorkFactory
import pl.gocards.filesync.sheet.WorkbookFactory
import pl.gocards.filesync.ui.dialogs.EditingDeckLockedDialog
import pl.gocards.filesync.ui.dialogs.SetUpAutoSyncFileDialog
import pl.gocards.filesync.ui.dialogs.SetUpAutoSyncFileDialogInput
import pl.gocards.room.entity.filesync.FileSynced
import pl.gocards.ui.common.addViewToRoot
import pl.gocards.ui.filesync.FileSyncAction
import pl.gocards.ui.filesync.FileSyncLauncherFactory
import pl.gocards.ui.filesync.FileSyncLauncherInput
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.theme.AppTheme

/**
 * @author Grzegorz Ziemski
 */
@Suppress("unused")
open class FileSyncLauncherFactoryImpl: FileSyncLauncherFactory {

    private val fileSyncWorkFactory = FileSyncWorkFactory()

    private lateinit var viewModel: FileSyncViewModel
    private lateinit var deckDbPath: String
    private lateinit var importToFolderPath: String
    private lateinit var onSuccess: () -> Unit

    private lateinit var activity: Activity
    private lateinit var owner: LifecycleOwner
    private lateinit var scope: CoroutineScope
    private lateinit var context: Context
    private lateinit var application: App


    @Composable
    override fun getInstance(
        fileSyncViewModel: FileSyncViewModel,
        activity: Activity,
        scope: CoroutineScope
    ): FileSyncLauncherInput {
        this.viewModel = fileSyncViewModel
        this.activity = activity
        this.scope = scope
        this.context = activity
        this.application = activity.application as App

        val importPicker = initImportFileLauncher()
        val exportExcelPicker = initExportLauncher(WorkbookFactory.MIME_TYPE_XLSX)
        val exportCsvPicker = initExportLauncher(WorkbookFactory.MIME_TYPE_CSV)

        return FileSyncLauncherInput(
            onClickExportExcel = { deckDbPath ->
                this.deckDbPath = deckDbPath
                onClickExport(
                    exportExcelPicker,
                    deckDbPath,
                    WorkbookFactory.FILE_EXTENSION_XLSX
                )
            },
            onClickExportCsv = { deckDbPath ->
                this.deckDbPath = deckDbPath
                onClickExport(
                    exportCsvPicker,
                    deckDbPath,
                    WorkbookFactory.FILE_EXTENSION_CSV
                )
            },
            onClickImport = { importToFolderPath, onSuccess ->
                onClickImport(importPicker, importToFolderPath, onSuccess)
            }
        )
    }

    @Composable
    protected fun initImportFileLauncher(): ManagedActivityResultLauncher<Array<String>, Uri?> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
            onResult = { uri -> uri?.let { onFileSyncedNotExists(FileSyncAction.IMPORT, uri) } }
        )
    }

    @Composable
    private fun initExportLauncher(mimeType: String): ManagedActivityResultLauncher<String, Uri?> {
        return rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(mimeType),
            onResult = { uri -> uri?.let { onExportPathSelected(deckDbPath, uri) } }
        )
    }

    private fun onExportPathSelected(deckDbPath: String, uri: Uri) {
        viewModel.findFileSynced(context, deckDbPath, uri.toString())
        { fileSynced ->
            if (fileSynced == null) {
                onFileSyncedNotExists(FileSyncAction.EXPORT, uri)
            } else {
                onFileSyncedExists(deckDbPath, fileSynced)
            }
        }
    }

    private fun onFileSyncedNotExists(action: FileSyncAction, uri: Uri) {
        val fileSynced = FileSynced()
        fileSynced.uri = uri.toString()
        showSetUpAutoSyncFileDialog(action, fileSynced)
    }

    private fun onFileSyncedExists(
        deckDbPath: String,
        fileSynced: FileSynced
    ) {
        if (fileSynced.autoSync) {
            exportWorkRequest(deckDbPath, fileSynced)
        } else {
            showSetUpAutoSyncFileDialog(FileSyncAction.EXPORT, fileSynced)
        }
    }

    private fun showSetUpAutoSyncFileDialog(
        action: FileSyncAction,
        fileSynced: FileSynced
    ) {
        addViewToRoot(activity, scope) { onDismiss ->

            val setUpAutoSyncFileDialog = SetUpAutoSyncFileDialogInput(
                onConfirmation = {
                    fileSynced.autoSync = true
                    onSuccessSetUpAutoSyncFileDialog(action, fileSynced)
                    onDismiss()
                },
                onCancel = {
                    fileSynced.autoSync = false
                    onSuccessSetUpAutoSyncFileDialog(action, fileSynced)
                    onDismiss()
                },
                onDismiss = {
                    onDismiss()
                }
            )

            AppTheme(isDarkTheme = application.darkMode) {
                SetUpAutoSyncFileDialog(setUpAutoSyncFileDialog)
            }
        }
    }

    private fun onSuccessSetUpAutoSyncFileDialog(
        action: FileSyncAction,
        fileSynced: FileSynced
    ) {
        if (action == FileSyncAction.IMPORT) {
            importWorkRequest(importToFolderPath, fileSynced, onSuccess)
        } else if (action == FileSyncAction.EXPORT) {
            exportWorkRequest(deckDbPath, fileSynced)
        }
    }

    private fun onClickExport(
        exportPicker: ManagedActivityResultLauncher<String, Uri?>,
        deckDbPath: String,
        extension: String
    ) {
        viewModel.checkIfDeckEditingIsLocked(
            context,
            deckDbPath,
            onSuccess = {
                exportPicker.launch(getFileName(deckDbPath, extension))
            },
            onError = {
                showEditingDeckLockedDialog()
            }
        )
    }

    private fun onClickImport(
        importPicker: ManagedActivityResultLauncher<Array<String>, Uri?>,
        importToFolderPath: String,
        onSuccess: () -> Unit
    ) {
        this.importToFolderPath = importToFolderPath
        this.onSuccess = onSuccess
        importPicker.launch(WorkbookFactory.SUPPORTED_MIME_TYPES)
    }

    private fun exportWorkRequest(deckDbPath: String, fileSynced: FileSynced) {
        viewModel.lockDeckEditing(
            context,
            deckDbPath,
            onSuccess = {
                fileSyncWorkFactory.exportWorkRequest(
                    deckDbPath,
                    fileSynced,
                    context.applicationContext
                )
            },
            onError = {
                showEditingDeckLockedDialog()
            }
        )
    }

    private fun importWorkRequest(
        importToFolderPath: String,
        fileSynced: FileSynced,
        onSuccess: () -> Unit
    ) {
        fileSyncWorkFactory.importWorkRequest(
            context,
            owner,
            fileSynced,
            importToFolderPath,
            onSuccess
        )
    }

    private fun showEditingDeckLockedDialog() {
        addViewToRoot(activity, scope) { onDismiss ->
            AppTheme(isDarkTheme = application.darkMode) {
                EditingDeckLockedDialog { onDismiss() }
            }
        }
    }

    private fun getFileName(deckDbPath: String, extension: String): String {
        return getDeckName(deckDbPath) + "." + extension
    }

    private fun getDeckName(dbPath: String): String {
        return AppDeckDbUtil.getDeckName(dbPath)
    }
}