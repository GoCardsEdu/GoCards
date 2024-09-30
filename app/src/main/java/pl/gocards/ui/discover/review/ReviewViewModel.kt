package pl.gocards.ui.discover.review

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.util.Config
import java.time.ZonedDateTime

/**
 * @author Grzegorz Ziemski
 */
class ReviewViewModel(
    val appDb: AppDatabase,
    private val application: Application
): AndroidViewModel(application) {

    val canReview = mutableStateOf(false)

    init {
        viewModelScope.launch {
            canReview.value = isLongTimeUser() && isLongTimeExceptionFree() || isReviewMockEnabled()
        }
    }

    private suspend fun isLongTimeUser(): Boolean {
        return appDb.appConfigKtxDao().getFirstUsedAt() < ZonedDateTime.now().minusWeeks(2)
    }

    private suspend fun isLongTimeExceptionFree(): Boolean {
        val lastExceptionAt = appDb.appConfigKtxDao().getLastExceptionAt() ?: return true
        return lastExceptionAt < ZonedDateTime.now().minusMonths(1)
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