package pl.gocards.ui.settings.model.app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.room.entity.app.AppConfig
import pl.gocards.db.room.AppDatabase

/**
 * S_U_09 App: Dark mode
 * @author Grzegorz Ziemski
 */
class DarkModeModel(
    private val appDb: AppDatabase,
    private val isSystemInDarkTheme: Boolean,
    val application: App
) : AndroidViewModel(application) {

    val darkMode: MutableLiveData<String> = MutableLiveData(AppConfig.DARK_MODE_DEFAULT)
    val darkModeDb: MutableLiveData<String> = MutableLiveData(AppConfig.DARK_MODE_DEFAULT)
    val isDarkTheme: MutableLiveData<Boolean> = MutableLiveData(isSystemInDarkTheme)

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val appConfig = appDb.appConfigKtxDao().getByKey(AppConfig.DARK_MODE)
            if (appConfig != null) {
                set(appConfig.value)
                darkModeDb.postValue(appConfig.value)
            }
        }
    }

    fun set(option: String) {
        viewModelScope.launch(Dispatchers.IO) {
            darkMode.postValue(option)
            when (option) {
                AppConfig.DARK_MODE_DEFAULT -> {
                    when (AppCompatDelegate.getDefaultNightMode()) {
                        AppCompatDelegate.MODE_NIGHT_NO -> isDarkTheme.postValue(false)
                        AppCompatDelegate.MODE_NIGHT_YES -> isDarkTheme.postValue(true)
                        else -> isDarkTheme.postValue(isSystemInDarkTheme)
                    }
                }
                AppConfig.DARK_MODE_ON -> isDarkTheme.postValue(true)
                AppConfig.DARK_MODE_OFF -> isDarkTheme.postValue(false)
            }
        }
    }

    fun reset() {
        set(darkModeDb.value ?: AppConfig.DARK_MODE_DEFAULT)
    }

    fun commit() {
        darkMode.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                appDb.appConfigKtxDao().update(
                    AppConfig.DARK_MODE,
                    it, AppConfig.DARK_MODE_DEFAULT
                )
                darkModeDb.postValue(darkMode.value)
            }
            when (darkMode.value) {
                AppConfig.DARK_MODE_ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                AppConfig.DARK_MODE_OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}