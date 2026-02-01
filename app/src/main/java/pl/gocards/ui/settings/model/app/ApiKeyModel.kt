package pl.gocards.ui.settings.model.app

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pl.gocards.App
import pl.gocards.db.app.AppDbMainThreadUtil
import pl.gocards.room.dao.app.AppConfigDao
import pl.gocards.room.entity.app.AppConfig

class ApiKeyModel(
    val application: App
) : AndroidViewModel(application) {

    val apiKey: MutableLiveData<String> = MutableLiveData("")
    val hasKey: MutableLiveData<Boolean> = MutableLiveData(false)

    private lateinit var dao: AppConfigDao

    fun init() {
        dao = AppDbMainThreadUtil.getInstance(application)
            .getDatabase(application)
            .appConfigDao()
        hasKey.value = !dao.getStringByKey(AppConfig.OPENAI_API_KEY).isNullOrBlank()
        apiKey.value = dao.getStringByKey(AppConfig.OPENAI_API_KEY) ?: ""
    }

    fun set(key: String) {
        apiKey.value = key
    }

    fun commit() {
        val key = apiKey.value ?: ""
        if (key.isNotBlank()) {
            dao.update(AppConfig.OPENAI_API_KEY, key)
            hasKey.value = true
        }
    }

    fun clear() {
        dao.deleteByKey(AppConfig.OPENAI_API_KEY)
        apiKey.value = ""
        hasKey.value = false
    }
}
