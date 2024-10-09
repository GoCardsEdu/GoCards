package pl.gocards.ui.common.pager.dynamic

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun <E> DynamicPagerWrapper(
    input: DynamicPagerDto<E>,
    content: @Composable (PagerState) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = input.settledPage.value ?: 0,
        pageCount = { input.getSize() }
    )

    LaunchedEffect(pagerState.settledPage) {
        snapshotFlow { pagerState.settledPage }
            .collect {
                input.setSettledPage(it)
            }
    }

    LaunchedEffect(input.animateScrollToPage.value) {
        snapshotFlow { input.animateScrollToPage.value }
            .collect {
                if (it != null) {
                    pagerState.animateScrollToPage(it)
                    input.clearAnimateScrollToPage()
                }
            }
    }

    LaunchedEffect(input.changePagerPage.value) {
        snapshotFlow { input.changePagerPage.value }
            .collect {
                if (it != null) {
                    pagerState.scrollToPage(it)
                    input.clearChangePagerPage()
                }
            }
    }

    content(pagerState)
}