package pl.gocards.ui.cards.slider.page.study.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import pl.gocards.ui.theme.ExtendedTheme

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
@Composable
fun SlidingDivider(
    termHeightPx: MutableState<Float?>,
    minSlideToY: Int,
    maxSlideToY: Int
) {
    Box(
        modifier = Modifier
            .background(ExtendedTheme.colors.colorListDivider)
            .alpha(0.1f)
            .fillMaxWidth()
            .height(5.dp)
            .padding(0.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    val termHeightPxVal = termHeightPx.value ?: 0f
                    val y = termHeightPxVal + delta
                    val isInAllowedSpace = minSlideToY < y && y < maxSlideToY
                    val forbiddenTermHeightPx = termHeightPxVal < minSlideToY || maxSlideToY < termHeightPxVal
                    if (isInAllowedSpace || forbiddenTermHeightPx) {
                        termHeightPx.value = y
                    }
                }
            )
    )
}