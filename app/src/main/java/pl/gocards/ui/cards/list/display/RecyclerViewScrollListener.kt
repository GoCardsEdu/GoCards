package pl.gocards.ui.cards.list.display

import androidx.compose.runtime.MutableState
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewScrollListener(
    private val isScrollingDown: MutableState<Boolean>,
    private val threshold: Int = 30
) : RecyclerView.OnScrollListener() {

    private var accumulatedDy = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy == 0) return

        // Reset accumulator on direction change, otherwise accumulate
        if ((accumulatedDy > 0 && dy < 0) || (accumulatedDy < 0 && dy > 0)) {
            accumulatedDy = dy
        } else {
            accumulatedDy += dy
        }

        if (accumulatedDy > threshold && !isScrollingDown.value) {
            isScrollingDown.value = true
        } else if (accumulatedDy < -threshold && isScrollingDown.value) {
            isScrollingDown.value = false
        }
    }
}