package pl.gocards.ui.common.popup.menu

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import pl.gocards.App
import pl.gocards.ui.kt.theme.AppTheme

/**
 * @author Grzegorz Ziemski
 */
class ShowPopupMenuAtPos(
    val activity: Activity,
    val onDismiss: () -> Unit = {}
) {

    /**
     * Show popup at last tap location.
     */
    fun createPopupMenu(
        view: View,
        content: @Composable (onDismiss: () -> Unit) -> Unit
    ) {
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        val x = (location[0] + view.width).toFloat()
        val y = (location[1] + view.height).toFloat()

        createPopupMenu(x,y, content)
    }

    /**
     * Show popup at last tap location.
     */
    fun createPopupMenu(
        x: Float,
        y: Float,
        content: @Composable (onDismiss: () -> Unit) -> Unit
    ) {
        val root: View = activity.findViewById(android.R.id.content)
        val rootView: ViewGroup = root.rootView as ViewGroup

        val popupView = createPopupMenu(rootView, x, y, content)
        rootView.addView(popupView)
    }

    /**
     * Show popup at last tap location.
     */
    private fun createPopupMenu(
        rootView: ViewGroup,
        x: Float,
        y: Float,
        content: @Composable (onDismiss: () -> Unit) -> Unit
    ): ComposeView {
        val view = createView(x, y)
        val application = activity.application as App
        view.setContent {
            AppTheme(isDarkTheme = application.darkMode) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {
                            rootView.removeView(view)
                            onDismiss()
                        }
                    ) {
                        content {
                            rootView.removeView(view)
                            onDismiss()
                        }
                    }
                }
            }
        }
        return view
    }

    /**
     * A view that allows to display a popup with coordinates.
     */
    private fun createView(x: Float, y: Float): ComposeView {
        val view = ComposeView(activity)
        view.layoutParams = ViewGroup.LayoutParams(1, 1)
        view.x = x
        view.y = y
        return view
    }
}