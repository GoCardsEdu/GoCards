package pl.gocards.filesync.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.db.storage.DatabaseException
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.room.entity.filesync.FileSynced
import pl.gocards.ui.filesync.FileSyncViewModel

/**
 * @author Grzegorz Ziemski
 */
open class FileSyncViewModelImpl(
    private val owner: LifecycleOwner,
    private val application: Application
) : AndroidViewModel(application), FileSyncViewModel {

    @Suppress("RedundantIf")
    override fun inProgress(deckDbPath: String): MutableLiveData<Boolean> {
        val inProgress: MutableLiveData<Boolean> = MutableLiveData(false)

        getDeckDb(application, deckDbPath)
            .deckConfigLiveDataDao()
            .getLongByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)
            .observe(owner) {
                if (it == null || isNoWorkerWorking(application)) {
                    inProgress.value = false
                } else {
                    inProgress.value = true
                }
            }

        return inProgress
    }

    /**
     * FS_PRO_S.1. Check that deck editing is not locked by another export/import/sync process.
     */
    @SuppressLint("CheckResult")
    override fun checkIfDeckEditingIsLocked(
        context: Context,
        deckDbPath: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // The db connector must be the same as in the UI app,
                // otherwise LiveData in the UI will not work.
                // TODO maybe checking working is enough?
                val blockedAt = getDeckDb(context, deckDbPath)
                    .deckConfigKtxDao()
                    .getLongByKey(DeckConfig.FILE_SYNC_EDITING_BLOCKED_AT)

                if (blockedAt == null || isNoWorkerWorking(context)) {
                    onSuccess()
                } else {
                    onError()
                }
            } catch (e: DatabaseException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun isNoWorkerWorking(context: Context): Boolean {
        val workManager = WorkManager.getInstance(context)
        val statuses = workManager.getWorkInfosByTag(FileSyncViewModel.TAG).get()
        return statuses.none {
            it.state == WorkInfo.State.BLOCKED
                    || it.state == WorkInfo.State.ENQUEUED
                    || it.state == WorkInfo.State.RUNNING
        }
    }

    /**
     * FS_PRO_S.6. Lock the deck editing.
     */
    override fun lockDeckEditing(
        context: Context,
        deckDbPath: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // The db connector must be the same as in the UI app,
                // otherwise LiveData in the UI will not work.
                val locked = getDeckDb(context, deckDbPath)
                    .deckConfigKtxDao()
                    .lockDeckEditing()

                if (locked || isNoWorkerWorking(context)) {
                    onSuccess()
                } else {
                    onError()
                }

            } catch (e: DatabaseException) {
                throw java.lang.RuntimeException(e)
            }
        }
    }

    override fun findFileSynced(
        context: Context,
        deckDbPath: String,
        fileUri: String,
        onSuccess: (fileSynced: FileSynced?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileSynced = getDeckDb(context, deckDbPath)
                .fileSyncedKtxDao()
                .findByUri(fileUri)
            onSuccess(fileSynced)
        }
    }

    @Throws(DatabaseException::class)
    protected fun getDeckDb(context: Context, dbPath: String): DeckDatabase {
        return AppDeckDbUtil.getInstance(context)
            .getDatabase(context, dbPath)
    }
}