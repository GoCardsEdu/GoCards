package pl.gocards.ui.common

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author Grzegorz Ziemski
 */
fun addViewToRoot(
    activity: Activity,
    scope: CoroutineScope,
    content: @Composable (onDismiss: () -> Unit) -> Unit
) {
    scope.launch {
        val root: View = activity.findViewById(android.R.id.content)
        val rootView: ViewGroup = root.rootView as ViewGroup
        val view = ComposeView(activity)
        view.setContent {
            content {
                rootView.removeView(view)
            }
        }
        rootView.addView(view)
    }
}