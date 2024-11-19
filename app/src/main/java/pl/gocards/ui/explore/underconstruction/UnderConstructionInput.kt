package pl.gocards.ui.explore.underconstruction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * @author Grzegorz Ziemski
 */
data class UnderConstructionInput(
    val showPoll: State<Boolean>,
    val onClickYes: () -> Unit,
    val onClickNo: () -> Unit,
)

class UnderConstructionInputFactory {

    @Composable
    fun create(
        exploreViewModel: PollViewModel,
        analytics: FirebaseAnalyticsHelper,
        scope: LifecycleCoroutineScope
    ): UnderConstructionInput {
        val showPoll = remember { mutableStateOf(true) }

        LaunchedEffect(exploreViewModel.isPollCompleted.value) {
            showPoll.value = !exploreViewModel.isPollCompleted.value
        }

        return UnderConstructionInput(
            showPoll = showPoll,
            onClickYes = {
                showPoll.value = false
                analytics.explorePollYes()
                scope.launch {
                    exploreViewModel.completePoll()
                }
            },
            onClickNo = {
                showPoll.value = false
                analytics.explorePollNo()
                scope.launch {
                    exploreViewModel.completePoll()
                }
            }
        )
    }

}