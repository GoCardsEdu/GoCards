package pl.gocards.ui.cards.list.edge_bar.learning_progress

import android.view.View
import androidx.annotation.UiThread
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.display.CardViewHolder
import pl.gocards.ui.cards.list.search.SearchListCardsAdapter
import pl.gocards.ui.cards.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.list.select.SelectCardsViewModel
import pl.gocards.ui.theme.ExtendedColors


/**
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
open class LearningProgressListCardsAdapter(
    viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    private val learningProgressViewModel: LearningProgressViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    owner: LifecycleOwner = activity,
    scope: CoroutineScope = owner.lifecycleScope
): SearchListCardsAdapter(
    viewModel,
    selectViewModel,
    snackbarHostState,
    colors,
    activity,
    scope
) {

    override fun loadCards() {
        learningProgressViewModel.loadCards {
            super.loadCards()
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        setStatusEdgeBar(holder, position)
    }

    @UiThread
    private fun setStatusEdgeBar(holder: CardViewHolder, position: Int) {
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS == learningProgressViewModel.leftEdgeBar) {
            val view = holder.getLeftEdgeBarView()
            setStatusEdgeBar(view, position)
        }
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS == learningProgressViewModel.rightEdgeBar) {
            val view = holder.getRightEdgeBarView()
            setStatusEdgeBar(view, position)
        }
    }

    @UiThread
    private fun setStatusEdgeBar(view: View, position: Int) {
        val cardId = getCard(position).id
        view.setBackgroundColor(0)
        if (setColorIfContains(
                view,
                learningProgressViewModel.disabledCards,
                cardId,
                colors.colorItemDisabledCard
            )
        ) return
        if (setColorIfContains(
                view,
                learningProgressViewModel.forgottenCards,
                cardId,
                colors.colorItemForgottenCard
            )
        ) return
        if (setColorIfContains(
                view,
                learningProgressViewModel.rememberedCards,
                cardId,
                colors.colorItemRememberedCards
            )
        ) return
    }

    @UiThread
    protected fun setColorIfContains(
        itemView: View,
        cards: Set<Int>,
        cardId: Int,
        color: Color
    ): Boolean {
        if (cards.contains(cardId)) {
            itemView.setBackgroundColor(color.toArgb())
            return true
        }
        return false
    }
}