package pl.gocards.ui.settings.model.all_decks

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.db.room.AppDatabase
import pl.gocards.ui.settings.model.SettingModel

/**
 * S_U_07 All decks: Show left edge bar
 * @author Grzegorz Ziemski
 */
class ShowLeftEdgeBarModel(
    private val appDb: AppDatabase,
    application: Application
) : SettingModel(application) {

    val leftEdgeBar: MutableLiveData<String> = MutableLiveData(AppConfig.LEFT_EDGE_BAR_DEFAULT)
    private var leftEdgeBarDb: String = AppConfig.LEFT_EDGE_BAR_DEFAULT

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val appConfig = appDb.appConfigKtxDao().getByKey(AppConfig.LEFT_EDGE_BAR)
            if (appConfig != null) {
                set(appConfig.value)
                leftEdgeBarDb = appConfig.value
            }
        }
    }

    fun set(option: String) {
        viewModelScope.launch(Dispatchers.IO) {
            leftEdgeBar.postValue(option)
        }
    }

    fun reset() {
        leftEdgeBar.value = leftEdgeBarDb
    }

    fun commit() {
        leftEdgeBar.value?.let {
            if (it.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    leftEdgeBarDb = it
                    appDb.appConfigKtxDao().update(
                        AppConfig.LEFT_EDGE_BAR,
                        it,
                        AppConfig.LEFT_EDGE_BAR_DEFAULT
                    )
                }
            }
        }
    }
}