package pl.gocards.ui.cards.kt.list.display

import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.databinding.ItemCardBinding
import pl.gocards.ui.common.popup.menu.ShowPopupMenuAtPos
import pl.gocards.ui.kt.theme.ExtendedColors

/**
 * @author Grzegorz Ziemski
 */
open class CardViewHolder(
    private val binding: ItemCardBinding,
    val colors: ExtendedColors,
    val activity: Activity
) : RecyclerView.ViewHolder(binding.root), GestureDetector.OnGestureListener, View.OnTouchListener {

    /**
     * Needed to define where the popup menu should be displayed.
     */
    private var lastTouchX = 0f

    /**
     * Needed to define where the popup menu should be displayed.
     */
    private var lastTouchY = 0f

    @Suppress("LeakingThis")
    private val gestureDetector: GestureDetector = GestureDetector(itemView.context, this)
    init {
        @Suppress("LeakingThis")
        itemView.setOnTouchListener(this)
    }

    /* -----------------------------------------------------------------------------------------
     * Background / Status
     * ----------------------------------------------------------------------------------------- */

    fun focus() {
        //itemView.isActivated = true
        itemView.setBackgroundColor(colors.colorItemActive.toArgb())
    }

    fun unfocus() {
        //itemView.isSelected = false
        itemView.setBackgroundColor(0)
    }

    /* -----------------------------------------------------------------------------------------
     * OnGestureListener
     * ----------------------------------------------------------------------------------------- */

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) { }

    /**
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     */
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        lastTouchX = e.rawX
        lastTouchY = e.rawY
        focus()
        showPopupMenu()
        return false
    }

    /**
     * Show popup at last tap location.
     */
    fun showPopupMenu() {
        ShowPopupMenuAtPos(
            activity,
            onDismiss = { unfocus() }
        ).createPopupMenu(
            lastTouchX,
            lastTouchY,
        ) { onDismiss -> CreatePopupMenu(onDismiss) }
    }

    @Composable
    protected open fun CreatePopupMenu(onDismiss: () -> Unit) {

    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        throw UnsupportedOperationException("Not implemented")
    }

    /* -----------------------------------------------------------------------------------------
     * OnTouchListener
     * ----------------------------------------------------------------------------------------- */

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        lastTouchX = event.rawX
        lastTouchY = event.rawY
        gestureDetector.onTouchEvent(event)
        itemView.performClick()
        return false
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    fun getIdTextView(): TextView {
        return binding.id
    }

    fun getTermTextView(): TextView {
        return binding.term
    }

    fun getDefinitionTextView(): TextView {
        return binding.definition
    }

    fun getLeftEdgeBarView(): View {
        return binding.leftEdgeBar
    }

    fun getRightEdgeBarView(): View {
        return binding.rightEdgeBar
    }
}