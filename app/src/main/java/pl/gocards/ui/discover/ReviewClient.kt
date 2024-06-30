package pl.gocards.ui.discover

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
class ReviewClient(val context: Context) {

    private val manager = ReviewManagerFactory.create(context)
    //private val manager = FakeReviewManager(context)
    private var reviewInfo: ReviewInfo? = null
    init {
        init()
    }

    fun init() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            } else {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context,
                    e, "InAppReview",
                    "Error while loading review client."
                )
            }
        }
    }

    fun launch(activity: Activity) {
        val reviewInfo = reviewInfo ?: return
        val flow = manager.launchReviewFlow(activity, reviewInfo)
        flow.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                val e = task.exception as ReviewException
                getExceptionHandler().saveException(
                    context,
                    e, "InAppReview",
                    "Error while loading review client."
                )
            }
        }
    }

    private fun getExceptionHandler(): ExceptionHandler {
        return ExceptionHandler.getInstance()
    }
}