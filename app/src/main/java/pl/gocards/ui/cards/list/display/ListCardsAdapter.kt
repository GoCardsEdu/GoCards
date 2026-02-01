package pl.gocards.ui.cards.list.display

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.R
import pl.gocards.databinding.ItemCardBinding
import pl.gocards.room.util.HtmlUtil
import pl.gocards.room.util.TimeUtil
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.model.ListCardsViewModel
import pl.gocards.ui.cards.list.model.UiListCard
import pl.gocards.ui.common.CommonAdapter
import pl.gocards.ui.theme.ExtendedColors
import pl.gocards.util.fromHtmlToHtmlCompat

/**
 * C_R_01 Display all cards
 * @author Grzegorz Ziemski
 */
open class ListCardsAdapter(
    private val calcCardIdWidth: CalcCardIdWidth = CalcCardIdWidth.getInstance(),
    protected open val viewModel: ListCardsViewModel,
    snackbarHostState: SnackbarHostState,
    val colors: ExtendedColors,
    override val activity: ListCardsActivity,
    protected val scope: CoroutineScope
    ) : CommonAdapter<CardViewHolder>(activity, snackbarHostState) {

    var editingLocked: Boolean = false

    protected val htmlUtil = HtmlUtil.getInstance()

    /* -----------------------------------------------------------------------------------------
     * Methods overridden
     * ----------------------------------------------------------------------------------------- */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(onCreateView(parent), colors, activity)
    }

    protected fun onCreateView(parent: ViewGroup): ItemCardBinding {
        return ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = getCard(position)
        holder.getIdTextView().text = String.format(card.ordinal.toString())

        if (calcCardIdWidth.lastNumChecked < card.ordinal) {
            calcCardIdWidth.resetIdWidth(holder)
        } else {
            calcCardIdWidth.setIdWidth(holder)
        }

        setText(holder.getTermTextView(), card.term)
        setText(holder.getDefinitionTextView(), card.definition)

        holder.getTermTextView().maxLines = viewModel.maxLines
        holder.getDefinitionTextView().maxLines = viewModel.maxLines
    }

    open fun setText(textView: TextView, value: String) {
        if (htmlUtil.isSimpleHtml(value)) {
            textView.text = value.fromHtmlToHtmlCompat()
        } else {
            textView.text = value
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Model
     * ----------------------------------------------------------------------------------------- */

    @SuppressLint("NotifyDataSetChanged")
    open fun loadCards(onLoaded: (() -> Unit)? = null) {
        viewModel.loadCards {
            scope.launch {
                notifyDataSetChanged()
                onLoaded?.invoke()
            }
        }
    }

    /**
     * C_D_25 Delete the card
     */
    fun delete(position: Int) {
        val card = getCard(position)
        viewModel.delete(card.id, TimeUtil.getNowEpochSec()) {
            scope.launch {
                doOnSuccessDelete(position, card)
            }
        }
    }

    /**
     * C_D_25 Delete the card
     */
    @UiThread
    protected open suspend fun doOnSuccessDelete(position: Int, card: UiListCard) {
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)

        showSnackbar(
            R.string.cards_list_toast_deleted_card,
            R.string.restore,
        ) { restore(card) }
    }

    /**
     * C_U_26 Undo card deletion
     */
    open fun restore(card: UiListCard) {
        viewModel.restore(card.id) {
            scope.launch {
                doOnSuccessRestore(card)
            }
        }
    }

    /**
     * C_U_26 Undo card deletion
     */
    @UiThread
    protected open suspend fun doOnSuccessRestore(card: UiListCard) {
        notifyItemInserted(card.ordinal - 1)

        val positionStart = card.ordinal - 1
        val itemCount = itemCount - card.ordinal + 2
        notifyItemRangeChanged(positionStart, itemCount)

        showShortToastMessage(R.string.cards_list_toast_restore_card)
    }

    /* -----------------------------------------------------------------------------------------
     * New activity
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_C_24 Edit the card
     */
    fun startEditCardActivity(position: Int) {
        activity.startEditCardActivity(getCard(position).id)
    }

    /**
     * C_R_07 Add a new card here
     */
    fun startNewCardActivity(position: Int) {
        activity.startNewCardActivity(getCard(position).id)
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/sets
     * ----------------------------------------------------------------------------------------- */

    override fun getItemCount(): Int {
        return getCards().size
    }

    fun getCards(): List<UiListCard> {
        return viewModel.items
    }

    fun getCard(position: Int): UiListCard {
        return getCards()[position]
    }
}