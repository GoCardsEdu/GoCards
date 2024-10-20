package pl.gocards.ui.discover.review

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.util.Config
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.TemporalAmount

/**
 * @author Grzegorz Ziemski
 */
class ReviewViewModel(
    val appDb: AppDatabase,
    private val application: Application
) : AndroidViewModel(application) {

    val discoverCanReview = mutableStateOf(false)

    val studyCanReview = mutableStateOf(false)

    init {
        viewModelScope.launch {
            discoverCanReview.value = canReview(
                userUsagePeriod = Period.ofDays(3),
                exceptionFreePeriod = Period.ofMonths(1)
            )
            studyCanReview.value = canReview(
                userUsagePeriod = Period.ofWeeks(1),
                exceptionFreePeriod = Period.ofMonths(1)
            )
        }
    }

    private suspend fun canReview(
        userUsagePeriod: TemporalAmount,
        exceptionFreePeriod: TemporalAmount
    ): Boolean {
        return isLongTimeUser(userUsagePeriod)
                && isLongTimeExceptionFree(exceptionFreePeriod)
                || isReviewMockEnabled()
    }

    private suspend fun isLongTimeUser(period: TemporalAmount): Boolean {
        return appDb.appConfigKtxDao().getFirstUsedAt() < ZonedDateTime.now().minus(period)
    }

    private suspend fun isLongTimeExceptionFree(period: TemporalAmount): Boolean {
        val lastExceptionAt = appDb.appConfigKtxDao().getLastExceptionAt() ?: return true
        return lastExceptionAt < ZonedDateTime.now().minus(period)
    }

    private fun isReviewMockEnabled(): Boolean {
        return Config.getInstance(application).isReviewMockEnabled(application)
    }

    companion object {
        fun create(application: Application): ReviewViewModel {
            val appDb = AppDbUtil
                .getInstance(application)
                .getDatabase(application)

            return ReviewViewModel(
                appDb = appDb,
                application = application
            )
        }
    }
}