package pl.gocards.ui.settings.model.deck

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.ui.settings.model.SettingModel
import pl.gocards.ui.settings.model.all_decks.MaxLinesModel

/**
 * S_U_04 This deck: Max lines
 * @author Grzegorz Ziemski
 */
class DeckMaxLinesModel(
    private val appDb: AppDatabase,
    private val deckDb: DeckDatabase,
    application: Application
) : SettingModel(application) {

    lateinit var appMaxLinesModel: MaxLinesModel

    val maxLines: MutableLiveData<String> = MutableLiveData(DeckConfig.MAX_LINES_DEFAULT.toString())

    private var deckMaxLinesDb: Int = DeckConfig.MAX_LINES_DEFAULT

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val deckConfig = deckDb.deckConfigKtxDao().getByKey(DeckConfig.MAX_LINES)
            if (deckConfig == null) {
                initApp()
            } else {
                set(deckConfig.value)
                deckMaxLinesDb = deckConfig.value.toInt()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun initApp() {
        viewModelScope.launch(Dispatchers.IO) {
            val appConfig = appDb.appConfigKtxDao().getByKey(DeckConfig.MAX_LINES)
            if (appConfig == null) {
                initDefault()
            } else {
                set(appConfig.value)
                deckMaxLinesDb = appConfig.value.toInt()
            }
        }
    }

    private fun initDefault() {
        set(DeckConfig.MAX_LINES_DEFAULT)
        deckMaxLinesDb = DeckConfig.MAX_LINES_DEFAULT
    }

    fun set(newValue: Any) {
        set(maxLines, newValue, "1")
    }

    fun reset() {
        maxLines.value = deckMaxLinesDb.toString()
    }

    fun commit() {
        maxLines.value?.let {
            if (it.isNotEmpty()) {
                viewModelScope.launch(Dispatchers.IO) {
                    deckMaxLinesDb = it.toInt()
                    deckDb.deckConfigKtxDao().update(
                        DeckConfig.MAX_LINES,
                        it,
                        appMaxLinesModel.maxLinesDb.toString()
                    )
                }
            }
        }
    }
}