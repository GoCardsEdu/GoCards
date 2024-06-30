package pl.gocards.ui.cards.slider.page.study.ui.definition

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import pl.gocards.R
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_FONT_SIZE_DEFAULT
import pl.gocards.ui.cards.slider.page.study.model.StudyCardUi

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun ShowDefinitionBox(
    @SuppressWarnings("unused")
    onClick: () -> Unit = {},
    studyCard: StudyCardUi,
    minSlideToY: Int,
    maxSlideToY: Int,
    sliderTouchSpace: Int
) {
    Box(
        modifier = if (sliderTouchSpace != 0) {
            Modifier.pointerInput(
                studyCard.id,
                showDefinitionSlideDividerGesture(
                    termHeightPx = studyCard.termHeightPx,
                    minSlideToY = minSlideToY,
                    maxSlideToY = maxSlideToY,
                    sliderTouchSpace = sliderTouchSpace
                )
            )
        } else {
            Modifier
        }
            .fillMaxSize()
            .pointerInput(studyCard.id) { detectTapGestures(onTap = { onClick() }) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.card_study_show_definition),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = STUDY_CARD_FONT_SIZE_DEFAULT.sp,
                fontSize = STUDY_CARD_FONT_SIZE_DEFAULT.sp,
            )
        )
    }
}

@SuppressLint("ReturnFromAwaitPointerEventScope")
private fun showDefinitionSlideDividerGesture(
    termHeightPx: MutableState<Float?>,
    minSlideToY: Int,
    maxSlideToY: Int,
    sliderTouchSpace: Int
): suspend PointerInputScope.() -> Unit {
    return {
        var enabled = false
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val termHeightPxVal = termHeightPx.value ?: 0f
                val y = event.changes.first().position.y + termHeightPxVal

                if (event.type == PointerEventType.Release) {
                    enabled = false
                } else if (enabled) {
                    val isInAllowedSpace = minSlideToY < y && y < maxSlideToY
                    val forbiddenTermHeightPx =
                        termHeightPxVal < minSlideToY || maxSlideToY < termHeightPxVal
                    if (isInAllowedSpace || forbiddenTermHeightPx) {
                        event.changes.forEach { it.consume() }
                        termHeightPx.value = y
                    }
                } else if (event.type == PointerEventType.Press) {
                    val sliderTouchSpaceY = termHeightPxVal + sliderTouchSpace
                    if (y < sliderTouchSpaceY) {
                        enabled = true
                    }
                }
            }
        }
    }
}