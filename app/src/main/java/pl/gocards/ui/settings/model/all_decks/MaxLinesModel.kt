package pl.gocards.ui.settings.model.all_decks

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.db.room.AppDatabase
import pl.gocards.ui.settings.model.SettingModel
import pl.gocards.ui.settings.model.deck.DeckMaxLinesModel

/**
 * S_U_06 All decks: Max lines
 * @author Grzegorz Ziemski
 */
class MaxLinesModel(
    private val appDb: AppDatabase,
    application: Application
) : SettingModel(application) {

    var deckMaxLinesModel: DeckMaxLinesModel? = null

    val maxLines: MutableLiveData<String> = MutableLiveData(DeckConfig.MAX_LINES_DEFAULT.toString())
    var maxLinesDb: Int = DeckConfig.MAX_LINES_DEFAULT

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val appConfig = appDb.appConfigKtxDao().getByKey(DeckConfig.MAX_LINES)
            if (appConfig != null) {
                set(appConfig.value)
                maxLinesDb = appConfig.value.toInt()
            }
        }
    }

    fun set(newValue: Any) {
        set(maxLines, newValue, "1")
    }

    fun reset() {
        maxLines.value = maxLinesDb.toString()
    }

    @SuppressLint("CheckResult")
    fun commit() {
        maxLines.value?.let {
            if (it.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    maxLinesDb = it.toInt()
                    appDb.appConfigKtxDao().update(
                        DeckConfig.MAX_LINES,
                        it,
                        DeckConfig.MAX_LINES_DEFAULT.toString()
                    )
                    deckMaxLinesModel?.init()
                }
            }
        }
    }
}