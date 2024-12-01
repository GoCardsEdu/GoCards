package pl.gocards.ui.decks.folders.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.R
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.ui.common.addViewToRoot
import pl.gocards.ui.decks.folders.model.EditFoldersViewModel
import pl.gocards.ui.theme.AppTheme
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class FolderDialogs(
    private val foldersViewModel: EditFoldersViewModel,
    private val onSuccess: () -> Unit,
    private val activity: Activity,
    private val scope: CoroutineScope,
    private val application: App
) {

    /**
     * F_C_03 Create a folder
     */
    fun showCreateFolderDialog(currentFolder: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = CreateFolderDialogInput(
                onCreateFolder = {
                    createFolder(currentFolder, it)
                    onSuccess()
                    onDismiss()
                },
                onDismiss = onDismiss
            )
            AppTheme(isDarkTheme = application.getDarkMode()) {
                CreateFolderDialog(input)
            }
        }
    }

    /**
     * F_C_03 Create a folder
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun createFolder(currentFolder: Path, newFolderName: String) {
        foldersViewModel.createFolder(
            currentFolder,
            newFolderName,
            onSuccess = {
                showShortToastMessage(
                    String.format(
                        getString(R.string.decks_list_folder_create_dialog_toast_created),
                        newFolderName
                    )
                )
            },
            onExists = {
                showShortToastMessage(
                    String.format(
                        getString(R.string.decks_list_folder_create_dialog_toast_exists),
                        newFolderName
                    )
                )
            }
        )
    }

    /**
     * F_U_04 Rename the folder
     */
    fun showRenameFolderDialog(folderPath: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = RenameFolderDialogInput(
                currentName = AppDeckDbUtil.getDeckName(folderPath),
                onRenameFolder = { newFolderName ->
                    renameFolder(folderPath, newFolderName)
                    onSuccess()
                    onDismiss()
                },
                onDismiss = onDismiss
            )
            AppTheme(isDarkTheme = application.getDarkMode()) {
                RenameFolderDialog(input)
            }
        }
    }

    /**
     * F_U_04 Rename the folder
     */
    private fun renameFolder(
        folderPath: Path,
        newFolderName: String
    ) {
        foldersViewModel.renameFolder(
            folderPath,
            newFolderName,
            onRenamed = {
                scope.launch {
                    showShortToastMessage(
                        String.format(
                            getString(R.string.decks_list_folder_rename_dialog_toast_renamed),
                            newFolderName
                        )
                    )
                }
            },
            onMerged = {
                showShortToastMessage(
                    String.format(
                        getString(R.string.decks_list_folder_rename_dialog_toast_merged),
                        newFolderName
                    )
                )
            }
        )
    }

    /**
     * F_D_05 Delete the folder
     */
    fun showDeleteFolderDialog(folderPath: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = DeleteFolderDialogInput(
                onDeleteFolder = {
                    deleteFolder(folderPath) {
                        onSuccess()
                    }
                    onDismiss()
                },
                onDismiss = onDismiss
            )

            AppTheme(isDarkTheme = application.getDarkMode()) {
                DeleteFolderDialog(input)
            }
        }
    }

    /**
     * F_D_05 Delete the folder
     */
    private fun deleteFolder(folderPath: Path, onSuccess: () -> Unit) {
        foldersViewModel.deleteFolder(folderPath) {
            onSuccess()
            scope.launch {
                showShortToastMessage(R.string.decks_list_folder_delete_dialog_toast_deleted)
            }
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    @UiThread
    @Suppress("SameParameterValue")
    private fun showShortToastMessage(@StringRes resId: Int) {
        Toast.makeText(
            activity,
            activity.getString(resId),
            Toast.LENGTH_SHORT
        ).show()
    }

    @UiThread
    private fun showShortToastMessage(text: String) {
        Toast.makeText(
            activity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getString(@StringRes id: Int): String {
        return application.resources.getString(id)
    }
}