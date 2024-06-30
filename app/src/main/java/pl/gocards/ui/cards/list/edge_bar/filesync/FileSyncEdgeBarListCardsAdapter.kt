package pl.gocards.ui.cards.list.edge_bar.filesync

import android.view.View
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.display.CardViewHolder
import pl.gocards.ui.cards.list.edge_bar.learning_progress.LearningProgressListCardsAdapter
import pl.gocards.ui.cards.list.edge_bar.learning_progress.LearningProgressViewModel
import pl.gocards.ui.cards.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.list.select.SelectCardsViewModel
import pl.gocards.ui.theme.ExtendedColors

/**
 * C_R_06 Show card last sync status: added, updated on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
open class FileSyncEdgeBarListCardsAdapter(
    viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    learningProgressViewModel: LearningProgressViewModel,
    private val fileSyncEdgeBarViewModel: FileSyncEdgeBarViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    owner: LifecycleOwner = activity,
): LearningProgressListCardsAdapter(
    viewModel,
    selectViewModel,
    learningProgressViewModel,
    snackbarHostState,
    colors,
    activity,
    owner
) {

    override fun loadCards() {
        fileSyncEdgeBarViewModel.load {
            super.loadCards()
        }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        setRecentlySyncedEdgeBar(holder, position)
    }

    /**
     * C_R_06 Show card last sync status: added, updated on the right/left edge bar.
     */
    private fun setRecentlySyncedEdgeBar(holder: CardViewHolder, position: Int) {
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == fileSyncEdgeBarViewModel.leftEdgeBar) {
            val view = holder.getLeftEdgeBarView()
            setRecentlySyncedEdgeBar(view, position)
        }
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED == fileSyncEdgeBarViewModel.rightEdgeBar) {
            val view = holder.getRightEdgeBarView()
            setRecentlySyncedEdgeBar(view, position)
        }
    }

    private fun setRecentlySyncedEdgeBar(view: View, position: Int) {
        val cardId = getCard(position).id
        view.setBackgroundColor(0)
        if (setColorIfContains(
                view,
                fileSyncEdgeBarViewModel.recentlyAddedCards,
                cardId,
                colors.colorItemRecentlyAddedDeckCard
            )
        ) return
        if (setColorIfContains(
                view,
                fileSyncEdgeBarViewModel.recentlyUpdatedCards,
                cardId,
                colors.colorItemRecentlyUpdatedDeckCard
            )
        ) return
        if (setColorIfContains(
                view,
                fileSyncEdgeBarViewModel.recentlyAddedFileCards,
                cardId,
                colors.colorItemRecentlyAddedFileCard
            )
        ) return
        if (setColorIfContains(
                view,
                fileSyncEdgeBarViewModel.recentlyUpdatedFileCards,
                cardId,
                colors.colorItemRecentlyUpdatedFileCard
            )
        ) return
    }
}