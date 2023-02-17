package pl.softfly.flashcards.ui.deck.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.softfly.flashcards.ExceptionHandler
import pl.softfly.flashcards.db.room.AppDatabase
import pl.softfly.flashcards.db.room.DeckDatabase
import pl.softfly.flashcards.entity.app.AppConfig
import pl.softfly.flashcards.entity.deck.DeckConfig
import pl.softfly.flashcards.entity.filesync.FileSynced

/**
 * @author Grzegorz Ziemski
 */
class DeckSettingsViewModel(
    private val appDb: AppDatabase,
    private val deckDb: DeckDatabase,
    application: Application
) : AndroidViewModel(application) {

    val autoSync: MutableLiveData<Boolean> = MutableLiveData(false)

    var fileSyncedDb : FileSynced? = null;

    val maxForgottenCards: MutableLiveData<String> = MutableLiveData(DeckConfig.MAX_FORGOTTEN_CARDS_DEFAULT.toString())

    var maxForgottenCardsDb: Int = DeckConfig.MAX_FORGOTTEN_CARDS_DEFAULT;

    val leftEdgeBar: MutableLiveData<String> = MutableLiveData(AppConfig.LEFT_EDGE_BAR_DEFAULT)

    var leftEdgeBarDb: String = AppConfig.LEFT_EDGE_BAR_DEFAULT;

    val rightEdgeBar: MutableLiveData<String> = MutableLiveData(AppConfig.RIGHT_EDGE_BAR_DEFAULT)

    var rightEdgeBarDb: String = AppConfig.RIGHT_EDGE_BAR_DEFAULT;

    init {
        AutoSync().init()
        MaxForgottenCards().init()
        ShowLeftEdgeBar().init()
        ShowRightEdgeBar().init()
    }

    inner class AutoSync {

        fun init() {
            deckDb.fileSyncedDao().findByAutoSyncTrue()
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe {
                    set(it.isAutoSync)
                    fileSyncedDb = it
                }
        }

        fun set(value: Boolean) {
            viewModelScope.launch(Dispatchers.IO) {
                autoSync.postValue(value)
            }
        }

        fun reset() {
            fileSyncedDb?.let { set(it.isAutoSync) }
        }

        fun commit() {
            fileSyncedDb?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    it.isAutoSync = autoSync.value == true
                    deckDb.fileSyncedDao().updateAll(fileSyncedDb)
                        .subscribeOn(Schedulers.io())
                        .doOnError(Throwable::printStackTrace)
                        .subscribe()
                }
            }
        }
    }

    inner class MaxForgottenCards {

        fun init() {
            deckDb.deckConfigAsyncDao().getByKey(DeckConfig.MAX_FORGOTTEN_CARDS)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe {
                    set(it.value)
                    maxForgottenCardsDb = it.value.toInt()
                }
        }

        fun set(newValue: Any) {
            viewModelScope.launch(Dispatchers.IO) {
                if (newValue is Float) {
                    maxForgottenCards.postValue(newValue.toInt().toString())
                } else if (newValue is String) {
                    if (newValue.isNotEmpty()) {
                        maxForgottenCards.postValue(newValue.toInt().toString())
                    } else {
                        maxForgottenCards.postValue("")
                    }
                }
            }
        }

        fun reset() {
            maxForgottenCards.value = maxForgottenCardsDb.toString();
        }

        fun commit() {
            viewModelScope.launch(Dispatchers.IO) {
                maxForgottenCards.value?.let {
                    maxForgottenCardsDb = it.toInt()
                    deckDb.deckConfigAsyncDao().update(
                        DeckConfig.MAX_FORGOTTEN_CARDS,
                        it,
                        DeckConfig.MAX_FORGOTTEN_CARDS_DEFAULT.toString()
                    )
                }
            }
        }
    }

    inner class ShowLeftEdgeBar {

        fun init() {
            appDb.appConfigAsync().getByKey(AppConfig.LEFT_EDGE_BAR)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe {
                    if (it.value != null) set(it.value)
                }
        }

        fun set(option: String) {
            viewModelScope.launch(Dispatchers.IO) {
                leftEdgeBar.postValue(option)
            }
        }

        fun reset() {
            leftEdgeBar.value = leftEdgeBarDb;
        }

        fun commit() {
            viewModelScope.launch(Dispatchers.IO) {
                leftEdgeBar.value?.let {
                    leftEdgeBarDb = it
                    appDb.appConfigAsync().update(
                        AppConfig.LEFT_EDGE_BAR,
                        it,
                        AppConfig.LEFT_EDGE_BAR_DEFAULT
                    )
                }
            }
        }
    }

    inner class ShowRightEdgeBar {

        fun init() {
            appDb.appConfigAsync().getByKey(AppConfig.RIGHT_EDGE_BAR)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .subscribe {
                    if (it.value != null) set(it.value)
                }
        }

        fun set(option: String) {
            viewModelScope.launch(Dispatchers.IO) {
                rightEdgeBar.postValue(option)
            }
        }

        fun reset() {
            rightEdgeBar.value = rightEdgeBarDb;
        }

        fun commit() {
            viewModelScope.launch(Dispatchers.IO) {
                rightEdgeBar.value?.let {
                    rightEdgeBarDb = it
                    appDb.appConfigAsync().update(
                        AppConfig.RIGHT_EDGE_BAR,
                        it,
                        AppConfig.RIGHT_EDGE_BAR_DEFAULT
                    )
                }
            }
        }
    }
}