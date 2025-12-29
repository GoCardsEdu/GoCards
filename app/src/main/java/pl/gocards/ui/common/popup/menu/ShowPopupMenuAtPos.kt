package pl.gocards.ui.common.popup.menu

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import pl.gocards.App
import pl.gocards.ui.theme.AppTheme

/**
 * @author Grzegorz Ziemski
 */
class ShowPopupMenuAtPos(
    val activity: Activity,
    private val isSelectionMode: Boolean = false,
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

        createPopupMenu(x, y, content)
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
        val view = ComposeView(activity)
        val application = activity.application as App
        view.setContent {
            AppTheme(
                isDarkTheme = application.getDarkMode(),
                isSelectionMode = isSelectionMode
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                        .offset { IntOffset(x.toInt(), y.toInt()) }
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
}