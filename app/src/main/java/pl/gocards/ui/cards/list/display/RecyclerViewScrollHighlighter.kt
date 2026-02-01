package pl.gocards.ui.cards.list.display

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun RecyclerView.scrollToCardsAndHighlight(cardIds: List<Int>) {
    val adapter = adapter as? ListCardsAdapter ?: return
    val layoutManager = layoutManager as? LinearLayoutManager ?: return
    val cards = adapter.getCards()

    val positions = cardIds.mapNotNull { id ->
        val pos = cards.indexOfFirst { it.id == id }
        if (pos >= 0) pos else null
    }
    if (positions.isEmpty()) return

    val lastPosition = positions.last()
    val offset = height / 3

    val smoothScroller = object : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        override fun calculateDtToFit(
            viewStart: Int, viewEnd: Int,
            boxStart: Int, boxEnd: Int,
            snapPreference: Int
        ): Int = (boxStart + offset) - viewStart
    }
    smoothScroller.targetPosition = lastPosition
    layoutManager.startSmoothScroll(smoothScroller)

    suspendCancellableCoroutine { cont ->
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv.removeOnScrollListener(this)
                    if (cont.isActive) cont.resume(Unit)
                }
            }
        }
        addOnScrollListener(listener)
        cont.invokeOnCancellation { removeOnScrollListener(listener) }
    }

    val highlightColor = 0x4400B0FF
    val transparent = 0x00000000
    for (position in positions) {
        val vh = findViewHolderForAdapterPosition(position)
        vh?.itemView?.let { view ->
            ValueAnimator.ofObject(ArgbEvaluator(), highlightColor, transparent).apply {
                duration = 3000
                addUpdateListener { view.setBackgroundColor(it.animatedValue as Int) }
                start()
            }
        }
    }
}
