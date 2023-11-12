package pl.gocards.ui.settings.model.all_decks

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.db.room.AppDatabase

/**
 * S_U_08 All decks: Show right edge bar
 * @author Grzegorz Ziemski
 */
class ShowRightEdgeBarModel (
    private val appDb: AppDatabase,
    application: Application
) : AndroidViewModel(application) {

    val rightEdgeBar: MutableLiveData<String> = MutableLiveData(AppConfig.RIGHT_EDGE_BAR_DEFAULT)
    private var rightEdgeBarDb: String = AppConfig.RIGHT_EDGE_BAR_DEFAULT

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val appConfig = appDb.appConfigKtxDao().getByKey(AppConfig.RIGHT_EDGE_BAR)
            if (appConfig != null) {
                set(appConfig.value)
                rightEdgeBarDb = appConfig.value
            }
        }
    }

    fun set(option: String) {
        viewModelScope.launch(Dispatchers.IO) {
            rightEdgeBar.postValue(option)
        }
    }

    fun reset() {
        rightEdgeBar.value = rightEdgeBarDb
    }

    fun commit() {
        rightEdgeBar.value?.let {
            if (it.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    rightEdgeBarDb = it
                    appDb.appConfigKtxDao().update(
                        AppConfig.RIGHT_EDGE_BAR,
                        it,
                        AppConfig.RIGHT_EDGE_BAR_DEFAULT
                    )
                }
            }
        }
    }
}