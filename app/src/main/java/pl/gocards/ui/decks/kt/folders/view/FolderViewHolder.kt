package pl.gocards.ui.decks.kt.folders.view

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.R
import pl.gocards.databinding.ItemFolderBinding
import pl.gocards.ui.common.popup.menu.ShowPopupMenuAtPos
import pl.gocards.ui.decks.kt.folders.ListFoldersAdapter

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
class FolderViewHolder(
    binding: ItemFolderBinding,
    val activity: Activity,
    val adapter: ListFoldersAdapter
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        initMoreButton()
        itemView.setOnClickListener(this)
    }

    private fun initMoreButton() {
        val moreTextView = getMoreImageView()
        moreTextView.setOnClickListener { v: View ->
            val location = IntArray(2)
            v.getLocationOnScreen(location)

            ShowPopupMenuAtPos(
                activity
            ).createPopupMenu(
                (location[0] + 100).toFloat(),
                (location[1] + 100).toFloat(),
            ) { onDismiss -> CreatePopupMenu(onDismiss) }
        }
    }

    override fun onClick(v: View?) {
        adapter.openFolder(bindingAdapterPosition)
    }

    @Composable
    private fun CreatePopupMenu(onDismiss: () -> Unit) {
        FolderPopupMenu(
            onDismiss = onDismiss,
            onClickRenameFolder = { adapter.showRenameFolderDialog(bindingAdapterPosition) },
            onClickDeleteFolder = { adapter.showDeleteFolderDialog(bindingAdapterPosition) },
            onClickCutFolder = { adapter.cutFolder(bindingAdapterPosition) },
        )
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for View
     * ----------------------------------------------------------------------------------------- */

    fun getNameTextView(): TextView {
        return itemView.findViewById(R.id.nameTextView)
    }

    private fun getMoreImageView(): View {
        return itemView.findViewById(R.id.moreImageView)
    }
}