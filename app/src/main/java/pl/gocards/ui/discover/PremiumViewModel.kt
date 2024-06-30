package pl.gocards.ui.discover

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.gocards.db.room.AppDatabase
import pl.gocards.room.entity.app.AppConfig
import java.time.ZonedDateTime

/**
 * @author Grzegorz Ziemski
 */
class PremiumViewModel(
    val appDb: AppDatabase,
    application: Application
): AndroidViewModel(application) {

    val isPremium = mutableStateOf(false)
    init {
        viewModelScope.launch {
            reset()
        }
    }

    suspend fun reset() {
        isPremium.value = getIsPremium()
    }

    private suspend fun getIsPremium(): Boolean {
        val expiryAt = appDb.appConfigKtxDao()
            .getZonedDateTimeByKey(AppConfig.PREMIUM)
            ?: return false

        if (expiryAt > ZonedDateTime.now()) {
            return true
        } else {
            disablePremium()
            return false
        }
    }

    suspend fun savePremium(enabled: Boolean) {
        if (enabled) {
            enablePremium()
        } else {
            disablePremium()
        }
    }

    suspend fun enablePremium() {
        appDb.appConfigKtxDao().update(
            AppConfig.PREMIUM,
            ZonedDateTime.now().plusDays(7).toString(),
            null
        )
    }

    private suspend fun disablePremium() {
        appDb.appConfigKtxDao().deleteByKey(AppConfig.PREMIUM)
    }
}