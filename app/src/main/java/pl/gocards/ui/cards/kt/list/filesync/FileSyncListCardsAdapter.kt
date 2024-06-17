package pl.gocards.ui.cards.kt.list.filesync

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import pl.gocards.ui.cards.kt.list.ListCardsActivity
import pl.gocards.ui.cards.kt.list.edge_bar.filesync.FileSyncEdgeBarListCardsAdapter
import pl.gocards.ui.cards.kt.list.edge_bar.filesync.FileSyncEdgeBarViewModel
import pl.gocards.ui.cards.kt.list.edge_bar.learning_progress.LearningProgressViewModel
import pl.gocards.ui.cards.kt.list.search.SearchListCardsViewModel
import pl.gocards.ui.cards.kt.list.select.SelectCardsViewModel
import pl.gocards.ui.kt.theme.ExtendedColors

/**
 * @author Grzegorz Ziemski
 */
class FileSyncListCardsAdapter(
    viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    learningProgressViewModel: LearningProgressViewModel,
    fileSyncEdgeBarViewModel: FileSyncEdgeBarViewModel,
    isSyncProgress: LiveData<Boolean>,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    owner: LifecycleOwner = activity,
): FileSyncEdgeBarListCardsAdapter(
    viewModel,
    selectViewModel,
    learningProgressViewModel,
    fileSyncEdgeBarViewModel,
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