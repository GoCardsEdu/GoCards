package pl.gocards.ui.decks.folders

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.R
import pl.gocards.databinding.ItemFolderBinding
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.decks.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.folders.dialogs.FolderDialogs
import pl.gocards.ui.decks.folders.model.CutPasteFolderViewModel
import pl.gocards.ui.decks.folders.model.ListFoldersViewModel
import pl.gocards.ui.decks.folders.view.FolderViewHolder
import pl.gocards.ui.theme.ExtendedColors
import java.nio.file.Path

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
open class ListFoldersAdapter(
    decksViewModel: ListDecksViewModel,
    cutPasteDeckViewModel: CutPasteDeckViewModel?,
    val foldersViewModel: ListFoldersViewModel,
    val cutPasteFolderViewModel: CutPasteFolderViewModel?,
    deckDialogs: DeckDialogs,
    val folderDialogs: FolderDialogs,
    isShownMoreDeckMenu: MutableState<Path?>,
    isPremium: Boolean,
    colors: ExtendedColors,
    activity: Activity,
    scope: CoroutineScope,
    application: App,
) : ListDecksAdapter(
    decksViewModel,
    cutPasteDeckViewModel,
    deckDialogs,
    isShownMoreDeckMenu,
    isPremium,
    colors,
    activity,
    scope,
    application
) {
    companion object {
        private const val VIEW_TYPE_FOLDER = 2
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    override fun getItemViewType(position: Int): Int {
        return if (isFolder(position)) {
            VIEW_TYPE_FOLDER
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (VIEW_TYPE_FOLDER == viewType) {
            onCreateFolderViewHolder(parent)
        } else {
            super.onCreateViewHolder(parent, viewType)
        }
    }

    private fun onCreateFolderViewHolder(parent: ViewGroup): FolderViewHolder {
        return FolderViewHolder(onCreateFolderView(parent), activity, this)
    }

    private fun onCreateFolderView(parent: ViewGroup): ItemFolderBinding {
        return ItemFolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, itemPosition: Int) {
        if (VIEW_TYPE_FOLDER == getItemViewType(itemPosition)) {
            val folderViewHolder = holder as FolderViewHolder
            folderViewHolder.getNameTextView().text =
                foldersViewModel.folders[itemPosition].fileName.toString()
            folderViewHolder.getNameTextView().isSelected = true // fixes marquee
        } else {
            super.onBindViewHolder(holder, itemPosition)
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    fun loadItems() {
        foldersViewModel.loadItems(getCurrentFolder()) {
            super.loadItems(getCurrentFolder())
        }
    }

    override fun loadItems(folder: Path) {
        foldersViewModel.loadItems(folder) {
            super.loadItems(folder)
        }
    }

    override fun searchItems(query: String) {
        foldersViewModel.searchItems(query) {
            super.searchItems(query)
        }
    }

    /**
     * F_R_02 Open folder
     */
    @SuppressLint("NotifyDataSetChanged")
    fun openFolder(position: Int) {
        foldersViewModel.openFolder(position) { folder ->
            decksViewModel.loadItems(folder) {
                scope.launch {
                    notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun openFolderUp(): Boolean {
        if (foldersViewModel.isRootFolder()) {
            return false
        } else {
            foldersViewModel.openFolderUp { folder ->
                decksViewModel.loadItems(folder) {
                    scope.launch {
                        notifyDataSetChanged()
                    }
                }
            }
            return true
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Dialogs
     * ----------------------------------------------------------------------------------------- */

    /**
     * F_U_04 Rename the folder
     */
    fun showRenameFolderDialog(itemPosition: Int) {
        val folderPath = getFolderItemPath(itemPosition)
        folderDialogs.showRenameFolderDialog(folderPath)
    }

    /**
     * F_D_05 Delete the folder
     */
    fun showDeleteFolderDialog(itemPosition: Int) {
        val folderPath = getFolderItemPath(itemPosition)
        folderDialogs.showDeleteFolderDialog(folderPath)
    }

    /* -----------------------------------------------------------------------------------------
     * Cut/paste
     * ----------------------------------------------------------------------------------------- */

    /**
     * F_C_06 Cut the folder
     */
    fun cutFolder(itemPosition: Int) {
        cutPasteFolderViewModel?.cut(getItemPath(itemPosition))
    }

    /**
     * D_U_10 Paste the deck
     */
    override fun paste() {
        paste(getCurrentFolder())
        super.paste()
    }

    /**
     * D_U_10 Paste the deck
     */
    private fun paste(toFolder: Path) {
        cutPasteFolderViewModel?.paste(
            toFolder,
            onSuccess = { fileName, merged ->
                loadItems(toFolder)
                scope.launch {
                    showToastFolderMoved(fileName, merged)
                }
            }
        )
    }

    /**
     * TODO Add a number instead of merging.
     */
    private fun showToastFolderMoved(folderName: String, merged: Boolean) {
        showShortToastMessage(
            String.format(
                getString(
                    if (merged) {
                        R.string.decks_list_paste_folder_toast_merged
                    } else {
                        R.string.decks_list_paste_folder_toast_moved
                    }
                ),
                folderName
            )
        )
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    override fun getItemCount(): Int {
        val decksSize = super.getItemCount()
        val foldersSize = getFoldersSize()
        return decksSize + foldersSize
    }

    override fun getItemPath(itemPosition: Int): Path {
        return if (isFolder(itemPosition)) {
            getFolderItemPath(itemPosition)
        } else {
            super.getItemPath(itemPosition)
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Decks actions
     * ----------------------------------------------------------------------------------------- */

    override fun getDeckItemPosition(itemPosition: Int): Int {
        return itemPosition - getFoldersSize()
    }

    /* -----------------------------------------------------------------------------------------
     * Folder actions
     * ----------------------------------------------------------------------------------------- */

    private fun isFolder(position: Int): Boolean {
        return position < foldersViewModel.folders.size
    }

    private fun getFoldersSize(): Int {
        return foldersViewModel.folders.size
    }

    private fun getFolderItemPath(itemPosition: Int): Path {
        return foldersViewModel.folders[itemPosition]
    }
}