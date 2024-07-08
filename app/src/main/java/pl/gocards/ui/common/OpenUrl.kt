package pl.gocards.ui.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleCoroutineScope
import pl.gocards.util.ExceptionHandler

/**
 * @author Grzegorz Ziemski
 */
class OpenUrl {
    companion object {

        fun openUrl(
            activity: Activity,
            scope: LifecycleCoroutineScope,
            link: String
        ) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )
                activity.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                showBrowserNotFoundExceptionDialog(
                    activity,
                    scope,
                    link
                )
                ExceptionHandler.getInstance().saveException(activity, ex)
            }
        }

    }
}