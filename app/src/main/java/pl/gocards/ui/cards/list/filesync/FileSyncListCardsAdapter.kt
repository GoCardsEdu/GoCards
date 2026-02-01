package pl.gocards.ui.cards.list.filesync

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import pl.gocards.ui.cards.list.ListCardsActivity
import pl.gocards.ui.cards.list.edge_bar.ai.AIEdgeBarListCardsAdapter
import pl.gocards.ui.cards.list.edge_bar.ai.AIEdgeBarViewModel
import pl.gocards.ui.cards.list.edge_bar.filesync.FileSyncEdgeBarViewModel
import pl.gocards.ui.cards.list.edge_bar.learning_progress.LearningProgressViewModel
import pl.gocards.ui.cards.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.list.select.SelectCardsViewModel
import pl.gocards.ui.theme.ExtendedColors

/**
 * @author Grzegorz Ziemski
 */
class FileSyncListCardsAdapter(
    viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    learningProgressViewModel: LearningProgressViewModel,
    fileSyncEdgeBarViewModel: FileSyncEdgeBarViewModel,
    aiEdgeBarViewModel: AIEdgeBarViewModel,
    isSyncProgress: LiveData<Boolean>,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    owner: LifecycleOwner = activity,
): AIEdgeBarListCardsAdapter(
    viewModel,
    selectViewModel,
    learningProgressViewModel,
    fileSyncEdgeBarViewModel,
    aiEdgeBarViewModel,
    snackbarHostState,
    colors,
    activity,
    owner
) {
    init {
        isSyncProgress.observe(owner) {
            this.editingLocked = it
        }
    }
}