package pl.gocards.ui.cards.kt.list.display

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnDrawListener
import android.widget.TableRow
import androidx.compose.runtime.mutableIntStateOf
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

/**
 * This sets the width of the ID column and the width of the header columns.
 *
 *
 * What problem does this solve?
 * I was unable to use the SDK to set the relative width of the ID column
 * to match with the length of the text.
 *
 * @author Grzegorz Ziemski
 */
class CalcCardIdWidth {

    private var maxIdWidthInternal = 0
    var lastNumChecked = 1
    val maxIdWidth = mutableIntStateOf(0)

    fun calcIdWidth(recyclerView: RecyclerView): OnDrawListener {
        return OnDrawListener {
            val width = findMaxIdsWidth(recyclerView)
            if (width != 0) {
                maxIdWidthInternal = max(width.toDouble(), maxIdWidthInternal.toDouble()).toInt()
                setIdsWidth(recyclerView, maxIdWidthInternal)
                maxIdWidth.intValue = maxIdWidthInternal
            }
        }
    }

    private fun findMaxIdsWidth(recyclerView: RecyclerView): Int {
        var maxIdWidth = 0
        val childCount = recyclerView.childCount
        var i = 0
        while (i < childCount) {
            val holder =
                recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as CardViewHolder
            if (isWidthWrapContent(holder.getIdTextView())) {
                val width = holder.getIdTextView().width
                if (width > maxIdWidth) {
                    maxIdWidth = holder.getIdTextView().width
                }
            }
            ++i
        }
        return maxIdWidth
    }

    private fun isWidthWrapContent(view: View): Boolean {
        return view.layoutParams.width == TableRow.LayoutParams.WRAP_CONTENT
    }

    private fun setIdsWidth(recyclerView: RecyclerView, width: Int) {
        val childCount = recyclerView.childCount
        var i = 0
        while (i < childCount) {
            val holder =
                recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as CardViewHolder
            setIdWidth(holder, width)
            ++i
        }
    }

    fun setIdWidth(holder: CardViewHolder) {
        setIdWidth(holder, maxIdWidthInternal)
    }

    private fun setIdWidth(holder: CardViewHolder, width: Int) {
        val currentWidth = holder.getIdTextView().width
        if (currentWidth != width) {
            holder.getIdTextView().setLayoutParams(
                TableRow.LayoutParams(
                    width,
                    TableRow.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    /**
     * Needed to recalculate the width
     * @see CalcCardIdWidth
     */
    fun resetIdWidth(holder: CardViewHolder) {
        lastNumChecked = holder.getIdTextView().getText().toString().toInt()
        holder.getIdTextView().setLayoutParams(
            TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    companion object {
        private var INSTANCE: CalcCardIdWidth? = null

        @Synchronized
        fun getInstance(): CalcCardIdWidth {
            if (INSTANCE == null) {
                INSTANCE = CalcCardIdWidth()
            }
            return INSTANCE!!
        }
    }
}