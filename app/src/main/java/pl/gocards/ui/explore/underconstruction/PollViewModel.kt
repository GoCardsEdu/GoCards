package pl.gocards.ui.explore.underconstruction

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.room.entity.app.AppConfig

/**
 * @author Grzegorz Ziemski
 */
class PollViewModel(
    val appDb: AppDatabase,
    application: Application
): AndroidViewModel(application) {

    val isPollCompleted = mutableStateOf(false)

    init {
        viewModelScope.launch {
            isPollCompleted.value = getExplorePollCompleted()
        }
    }

    private suspend fun getExplorePollCompleted(): Boolean {
        appDb.appConfigKtxDao().deleteByKey(AppConfig.EXPLORE_POLL_COMPLETED)

        return appDb.appConfigKtxDao()
            .getBooleanByKey(AppConfig.EXPLORE_POLL_COMPLETED)
            ?: return false
    }


    suspend fun completePoll() {
        appDb.appConfigKtxDao().update(
            AppConfig.EXPLORE_POLL_COMPLETED,
            true.toString(),
            null
        )
        isPollCompleted.value = true
    }

    companion object {
        fun create(application: Application): PollViewModel {
            val appDb = AppDbUtil.getInstance(application)
                .getDatabase(application)

            return PollViewModel(
                appDb = appDb,
                application = application
            )
        }
    }
}