package pl.gocards.ui.discover.review

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import pl.gocards.util.ExceptionHandler

/**
 * https://developer.android.com/guide/playcore/in-app-review/kotlin-java
 *
 * @author Grzegorz Ziemski
 */
class InAppReviewClient(
    private val activity: Activity,
    private val context: Context
) {

    private val manager = ReviewManagerFactory.create(context)
    //private val manager = FakeReviewManager(context)

    fun launch(
        onSuccess: () -> Unit,
        onFailure: (() -> Unit)? = null
    ) {
        initInAppReview(onSuccess, onFailure)
    }

    private fun initInAppReview(
        onSuccess: () -> Unit,
        onFailure: (() -> Unit)?
    ) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                launchInAppReview(reviewInfo, onSuccess, onFailure)
            } else {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context, e, TAG,
                    "Error while loading review client."
                )
                onFailure?.invoke()
            }
        }
    }

    private fun launchInAppReview(
        reviewInfo: ReviewInfo,
        onSuccess: () -> Unit,
        onFailure: (() -> Unit)?
    ) {
        val flow = manager.launchReviewFlow(activity, reviewInfo)

        flow.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context, e, TAG,
                    "Error while loading review client."
                )
                onFailure?.invoke()
            }
        }
    }

    private fun getExceptionHandler(): ExceptionHandler {
        return ExceptionHandler.getInstance()
    }

    companion object {
        const val TAG = "InAppReview"
    }
}