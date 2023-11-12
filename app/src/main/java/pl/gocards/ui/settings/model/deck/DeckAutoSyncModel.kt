package pl.gocards.ui.settings.model.deck

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.room.entity.filesync.FileSynced
import pl.gocards.db.room.DeckDatabase

/**
 * S_U_02 This deck: Auto-sync with the file
 * @author Grzegorz Ziemski
 */
class DeckAutoSyncModel(
    private val deckDb: DeckDatabase,
    application: Application
) : AndroidViewModel(application) {

    val autoSync: MutableLiveData<Boolean> = MutableLiveData(false)

    private var fileSyncedDb : FileSynced? = null
    val autoSyncDb: MutableLiveData<Boolean> = MutableLiveData(false)
    val deckFileNameDb: MutableLiveData<String?> = MutableLiveData(null)

    @SuppressLint("CheckResult")
    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            fileSyncedDb = deckDb.fileSyncedKtxDao().findByAutoSyncTrue()
            fileSyncedDb?.let {
                autoSync.postValue(it.autoSync)
                autoSyncDb.postValue(it.autoSync)
                deckFileNameDb.postValue(it.displayName)
            }
        }
    }

    fun set(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            autoSync.postValue(value)
        }
    }

    fun reset() {
        set(autoSyncDb.value ?: false)
    }

    fun commit() {
        fileSyncedDb?.let {
            viewModelScope.launch(Dispatchers.IO) {
                it.autoSync = autoSync.value == true
                autoSyncDb.postValue(it.autoSync)
                deckDb.fileSyncedKtxDao().updateAll(it)
            }
        }
    }
}