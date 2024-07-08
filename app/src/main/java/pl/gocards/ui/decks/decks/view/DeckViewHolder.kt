package pl.gocards.ui.decks.decks.view

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.R
import pl.gocards.databinding.ItemDeckBinding
import pl.gocards.ui.common.popup.menu.ShowPopupMenuAtPos
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.decks.recent.ListRecentDecksAdapter

/**
 * D_R_02 Show all decks
 * @author Grzegorz Ziemski
 */
class DeckViewHolder(
    binding: ItemDeckBinding,
    val activity: Activity,
    val adapter: ListDecksAdapter
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    var noCardsToRepeat = true

    init {
        initMoreButton()
        binding.root.setOnClickListener(this)
    }

    private fun initMoreButton() {
        val moreTextView = getMoreImageView()
        moreTextView.setOnClickListener { view: View ->
            ShowPopupMenuAtPos(activity).createPopupMenu(view)
            { onDismiss -> CreatePopupMenu(onDismiss) }
        }
    }

    @Composable
    private fun CreatePopupMenu(onDismiss: () -> Unit) {
        DeckPopupMenu(
            onDismiss = onDismiss,
            onClickBrowseCards = if (adapter.isPremium) {
                { adapter.newBrowseCardsActivity(bindingAdapterPosition) }
            } else null,
            onClickListCards = { adapter.newListCardsActivity(bindingAdapterPosition) },
            onClickNewCard = { adapter.newNewCardActivity(bindingAdapterPosition) },
            onClickCutDeck = if (adapter is ListRecentDecksAdapter) null else {
                { adapter.cutDeck(bindingAdapterPosition) }
            },
            onClickRenameDeck = { adapter.showRenameDeckDialog(bindingAdapterPosition) },
            onClickDeleteDeck = { adapter.showDeleteDeckDialog(bindingAdapterPosition) },
            onClickShowMenuBottom = { adapter.showMoreDeckMenu(bindingAdapterPosition) }
        )
    }

    override fun onClick(view: View) {
        if (noCardsToRepeat) {
            adapter.showNoCardsToRepeatDialog()
        } else {
            adapter.onItemClick(getBindingAdapterPosition())
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for View
     * ----------------------------------------------------------------------------------------- */

    fun getNameTextView(): TextView {
        return itemView.findViewById(R.id.nameTextView)
    }

    fun getTotalTextView(): TextView {
        return itemView.findViewById(R.id.totalTextView)
    }

    fun getNewTextView(): TextView {
        return itemView.findViewById(R.id.newTextView)
    }

    fun getRevTextView(): TextView {
        return itemView.findViewById(R.id.revTextView)
    }

    private fun getMoreImageView(): View {
        return itemView.findViewById(R.id.moreImageView)
    }
}