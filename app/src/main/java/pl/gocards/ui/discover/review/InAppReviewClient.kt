package pl.gocards.ui.discover.review

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import pl.gocards.ui.common.OpenUrl
import pl.gocards.util.Config
import pl.gocards.util.ExceptionHandler
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * https://developer.android.com/guide/playcore/in-app-review/kotlin-java
 *
 * @author Grzegorz Ziemski
 */
class InAppReviewClient(
    private val analytics: FirebaseAnalyticsHelper,
    private val activity: Activity,
    private val scope: LifecycleCoroutineScope,
    private val context: Context
) {

    private val manager = ReviewManagerFactory.create(context)
    //private val manager = FakeReviewManager(context)

    fun launch() {
        analytics.discoverOpenReview()
        initInAppReview()
    }

    private fun initInAppReview() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                launchInAppReview(reviewInfo)
            } else {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context, e, TAG,
                    "Error while loading review client."
                )
                openAppPage()
            }
        }
    }

    private fun launchInAppReview(reviewInfo: ReviewInfo) {
        val flow = manager.launchReviewFlow(activity, reviewInfo)

        flow.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                analytics.discoverOpenReviewInApp()
                openAppPage()
            } else {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context, e, TAG,
                    "Error while loading review client."
                )
                openAppPage()
            }
        }
    }

    private fun openAppPage() {
        analytics.discoverOpenReviewFullPage()
        openUrl(Config.getInstance(activity).appPageUrl(activity))
    }

    private fun openUrl(link: String) {
        OpenUrl.openUrl(activity, scope, link)
    }

    private fun getExceptionHandler(): ExceptionHandler {
        return ExceptionHandler.getInstance()
    }

    companion object {
        const val TAG = "InAppReview"
    }
}