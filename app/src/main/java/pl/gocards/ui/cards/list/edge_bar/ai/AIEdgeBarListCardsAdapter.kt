package pl.gocards.ui.cards.list.edge_bar.ai

import android.view.View
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.display.CardViewHolder
import pl.gocards.ui.cards.list.edge_bar.filesync.FileSyncEdgeBarListCardsAdapter
import pl.gocards.ui.cards.list.edge_bar.filesync.FileSyncEdgeBarViewModel
import pl.gocards.ui.cards.list.edge_bar.learning_progress.LearningProgressViewModel
import pl.gocards.ui.cards.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.list.select.SelectCardsViewModel
import pl.gocards.ui.theme.ExtendedColors

/**
 * Show AI-created / AI-updated edge bar status.
 * @author Grzegorz Ziemski
 */
open class AIEdgeBarListCardsAdapter(
    viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    learningProgressViewModel: LearningProgressViewModel,
    fileSyncEdgeBarViewModel: FileSyncEdgeBarViewModel,
    private val aiEdgeBarViewModel: AIEdgeBarViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    owner: LifecycleOwner = activity,
) : FileSyncEdgeBarListCardsAdapter(
    viewModel,
    selectViewModel,
    learningProgressViewModel,
    fileSyncEdgeBarViewModel,
    snackbarHostState,
    colors,
    activity,
    owner
) {

    override fun loadCards(onLoaded: (() -> Unit)?) {
        aiEdgeBarViewModel.load {
            super.loadCards(onLoaded)
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        setAIEdgeBar(holder, position)
    }

    private fun setAIEdgeBar(holder: CardViewHolder, position: Int) {
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == aiEdgeBarViewModel.leftEdgeBar) {
            val view = holder.getLeftEdgeBarView()
            setAIEdgeBar(view, position)
        }
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == aiEdgeBarViewModel.rightEdgeBar) {
            val view = holder.getRightEdgeBarView()
            setAIEdgeBar(view, position)
        }
    }

    private fun setAIEdgeBar(view: View, position: Int) {
        val cardId = getCard(position).id
        view.setBackgroundColor(0)
        if (setColorIfContains(
                view,
                aiEdgeBarViewModel.aiCreatedCards,
                cardId,
                colors.colorItemAICreatedCard
            )
        ) return
        if (setColorIfContains(
                view,
                aiEdgeBarViewModel.aiUpdatedCards,
                cardId,
                colors.colorItemAIUpdatedCard
            )
        ) return
    }
}
