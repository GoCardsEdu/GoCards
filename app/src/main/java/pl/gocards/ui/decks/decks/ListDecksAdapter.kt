package pl.gocards.ui.decks.decks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.R
import pl.gocards.databinding.ItemDeckBinding
import pl.gocards.db.storage.DatabaseException
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.slider.BrowseCardSliderActivity
import pl.gocards.ui.cards.slider.EditCardSliderActivity
import pl.gocards.ui.cards.slider.StudyCardSliderActivity
import pl.gocards.ui.common.addViewToRoot
import pl.gocards.ui.decks.decks.dialogs.DeckDialogs
import pl.gocards.ui.decks.decks.dialogs.NoCardsToRepeatDialog
import pl.gocards.ui.decks.decks.model.CutPasteDeckViewModel
import pl.gocards.ui.decks.decks.model.ListDecksViewModel
import pl.gocards.ui.decks.decks.model.UiListDeck
import pl.gocards.ui.decks.decks.view.DeckViewHolder
import pl.gocards.ui.theme.AppTheme
import pl.gocards.ui.theme.ExtendedColors
import java.nio.file.Path
import java.util.Locale

/**
 * D_R_02 Show all decks
 * @author Grzegorz Ziemski
 */
open class ListDecksAdapter(
    val decksViewModel: ListDecksViewModel,
    val cutPasteDeckViewModel: CutPasteDeckViewModel?,
    val deckDialogs: DeckDialogs,
    val isShownMoreDeckMenu: MutableState<Path?>,
    val isPremium: Boolean,
    val colors: ExtendedColors,
    val activity: Activity,
    val scope: CoroutineScope,
    val application: App
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        protected const val VIEW_TYPE_DECK = 1
    }

    /* -----------------------------------------------------------------------------------------
     * View
     * ----------------------------------------------------------------------------------------- */

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_DECK
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (VIEW_TYPE_DECK == viewType) {
            onCreateDeckViewHolder(parent)
        } else {
            throw IllegalStateException("Unexpected value: $viewType")
        }
    }

    private fun onCreateDeckViewHolder(parent: ViewGroup): DeckViewHolder {
        return DeckViewHolder(onCreateDeckView(parent), activity, this)
    }

    private fun onCreateDeckView(parent: ViewGroup): ItemDeckBinding {
        return ItemDeckBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, itemPosition: Int) {
        val deckHolder = holder as DeckViewHolder
        val nameTextView = deckHolder.getNameTextView()
        val deck = getDeckItem(itemPosition)

        nameTextView.text = deck.name
        nameTextView.setSelected(true)
        try {
            deckHolder.getTotalTextView().text = calcTotal(deck)
            deckHolder.getNewTextView().text = calcNew(deckHolder, deck)
            deckHolder.getRevTextView().text = calcRev(deckHolder, deck)
        } catch (e: DatabaseException) {
            e.printStackTrace()
        }
    }

    private fun calcTotal(deck: UiListDeck): String {
        val total = deck.total ?: return ""

        return String.format(
            Locale.getDefault(),
            getString(R.string.decks_list_total),
            total
        )
    }

    private fun calcNew(deckViewHolder: DeckViewHolder, deck: UiListDeck): CharSequence? {
        val countByNew = deck.new ?: return ""

        if (countByNew > 0) {
            deckViewHolder.noCardsToRepeat = false
            val intColor = colors.colorItemRememberedCards.toArgb()
            val hexColor = String.format("#%06X", 0xFFFFFF and intColor)
            val html = String.format(
                Locale.getDefault(),
                "<font color=\"%s\">" + getString(R.string.decks_list_new) + "</font>",
                hexColor,
                countByNew
            )
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            return String.format(Locale.getDefault(), getString(R.string.decks_list_new_zero))
        }
    }

    private fun calcRev(deckViewHolder: DeckViewHolder, deck: UiListDeck): CharSequence? {
        val countByForgotten = deck.rev ?: return ""

        if (countByForgotten > 0) {
            deckViewHolder.noCardsToRepeat = false
            val intColor = colors.colorItemForgottenCard.toArgb()
            val hexColor = String.format("#%06X", 0xFFFFFF and intColor)
            val html = String.format(
                Locale.getDefault(),
                "<font color=\"%s\">" + getString(R.string.decks_list_review) + "</font>",
                hexColor,
                countByForgotten
            )
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            return String.format(Locale.getDefault(), getString(R.string.decks_list_review_zero))
        }
    }

    /* -----------------------------------------------------------------------------------------
     * New Activities
     * ----------------------------------------------------------------------------------------- */

    private fun newStudyCardActivity(itemPosition: Int) {
        val intent = Intent(activity, StudyCardSliderActivity::class.java)
        intent.putExtra(
            StudyCardSliderActivity.DECK_DB_PATH,
            getDeckItemPath(itemPosition).toString()
        )
        activity.startActivity(intent)
    }

    fun newBrowseCardsActivity(itemPosition: Int) {
        val intent = Intent(activity, BrowseCardSliderActivity::class.java)
        intent.putExtra(
            BrowseCardSliderActivity.DECK_DB_PATH,
            getDeckItemPath(itemPosition).toString()
        )
        activity.startActivity(intent)
    }


    fun newListCardsActivity(itemPosition: Int) {
        val intent = Intent(activity, ListCardsActivity::class.java)
        intent.putExtra(
            ListCardsActivity.DECK_DB_PATH,
            getDeckItemPath(itemPosition).toString()
        )
        activity.startActivity(intent)
    }

    fun newNewCardActivity(itemPosition: Int) {
        val intent = Intent(activity, EditCardSliderActivity::class.java)
        intent.putExtra(
            EditCardSliderActivity.DECK_DB_PATH,
            getDeckItemPath(itemPosition).toString()
        )
        intent.putExtra(EditCardSliderActivity.ADD_NEW_CARD, true)
        activity.startActivity(intent)
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @SuppressLint("NotifyDataSetChanged")
    open fun loadItems(folder: Path) {
        decksViewModel.loadItems(folder) {
            scope.launch {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * D_R_03 Search decks
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun searchItems(query: String) {
        decksViewModel.searchItems(query) {
            scope.launch {
                notifyDataSetChanged()
            }
        }
    }

    /**
     * D_U_07 Rename the deck
     */
    fun showRenameDeckDialog(itemPosition: Int) {
        val deckDbPath = getDeckItemPath(itemPosition)
        deckDialogs.showRenameDeckDialog(deckDbPath)
    }


    /**
     * D_R_08 Delete the deck
     */
    fun showDeleteDeckDialog(itemPosition: Int) {
        val deckDbPath = getDeckItemPath(itemPosition)
        deckDialogs.showDeleteDeckDialog(deckDbPath)
    }

    /**
     * C_R_31 No more cards to repeat
     */
    fun showNoCardsToRepeatDialog() {
        addViewToRoot(activity, scope) { onDismiss ->
            AppTheme(isDarkTheme = application.darkMode) {
                NoCardsToRepeatDialog(onDismiss)
            }
        }
    }

    fun showMoreDeckMenu(itemPosition: Int) {
        isShownMoreDeckMenu.value = getDeckItemPath(itemPosition)
    }

    fun onItemClick(itemPosition: Int) {
        newStudyCardActivity(itemPosition)
    }

    /* -----------------------------------------------------------------------------------------
     * Cut/paste
     * ----------------------------------------------------------------------------------------- */

    /**
     * D_R_09 Cut the deck
     */
    fun cutDeck(itemPosition: Int) {
        cutPasteDeckViewModel?.cut(getItemPath(itemPosition))
    }


    /**
     * D_U_10 Paste the deck
     */
    open fun paste() {
        paste(getCurrentFolder())
    }

    /**
     * D_U_10 Paste the deck
     */
    private fun paste(toFolder: Path) {
        cutPasteDeckViewModel?.paste(
            toFolder,
            onSuccess = { newDeckName ->
                loadItems(toFolder)
                scope.launch {
                    showToastDeckMoved(newDeckName)
                }
            }
        )
    }

    private fun showToastDeckMoved(deckName: String) {
        showShortToastMessage(
            String.format(
                getString(R.string.decks_list_paste_deck_toast),
                deckName
            )
        )
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    override fun getItemCount(): Int {
        return decksViewModel.decks.size
    }

    open fun getItemPath(itemPosition: Int): Path {
        return getDeckItemPath(itemPosition)
    }

    /* -----------------------------------------------------------------------------------------
     * Deck actions
     * ----------------------------------------------------------------------------------------- */

    protected open fun getDeckItem(itemPosition: Int): UiListDeck {
        return decksViewModel.decks[getDeckItemPosition(itemPosition)]
    }

    private fun getDeckItemPath(itemPosition: Int): Path {
        return getDeckItem(itemPosition).path
    }

    protected open fun getDeckItemPosition(itemPosition: Int): Int {
        return itemPosition
    }

    fun getCurrentFolder(): Path {
        return decksViewModel.currentFolder
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    protected fun getString(@StringRes id: Int): String {
        return application.resources.getString(id)
    }

    @UiThread
    protected fun showShortToastMessage(text: String) {
        Toast.makeText(application, text, Toast.LENGTH_SHORT).show()
    }
}
