package pl.gocards.filesync

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import pl.gocards.filesync.worker.ExportToFileWorker
import pl.gocards.filesync.worker.FileSyncWorker
import pl.gocards.filesync.worker.ImportFromFileWorker
import pl.gocards.room.entity.filesync.FileSynced

/**
 * @author Grzegorz Ziemski
 */
class FileSyncWorkFactory {

    companion object {
        private const val TAG = "FileSync"
    }

    /**
     * FS_I Import the file as a new deck.
     */
    fun importWorkRequest(
        context: Context,
        owner: LifecycleOwner,
        fileSynced: FileSynced,
        importToFolderPath: String,
        onSuccess: () -> Unit
    ) {
        val request: WorkRequest = OneTimeWorkRequest.Builder(ImportFromFileWorker::class.java)
            .setInputData(importInputData(fileSynced, importToFolderPath))
            .addTag(TAG)
            .build()

        val workManager = WorkManager.getInstance(context)

        workManager.enqueue(request)
        workManager.getWorkInfoByIdLiveData(request.id)
            .observe(owner)
            { workInfo: WorkInfo ->
                doOnSuccess(
                    owner.lifecycle,
                    workInfo,
                    onSuccess
                )
            }
    }

    private fun doOnSuccess(
        lifecycle: Lifecycle,
        workInfo: WorkInfo,
        onSuccess: () -> Unit
    ) {
        if (!isWorkSucceeded(workInfo) || !isActivityResumed(lifecycle)) {
            return
        }
        onSuccess()
    }

    private fun isWorkSucceeded(workInfo: WorkInfo): Boolean {
        return workInfo.state == WorkInfo.State.SUCCEEDED
    }

    private fun isActivityResumed(lifecycle: Lifecycle): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    private fun importInputData(
        fileSynced: FileSynced,
        importToFolderPath: String
    ): Data {
        return Data.Builder()
            .putString(ImportFromFileWorker.IMPORT_TO_FOLDER_PATH, importToFolderPath)
            .putString(ImportFromFileWorker.FILE_URI, fileSynced.uri)
            .putBoolean(ImportFromFileWorker.AUTO_SYNC, fileSynced.autoSync)
            .build()
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    fun exportWorkRequest(
        deckDbPath: String,
        fileSynced: FileSynced,
        context: Context
    ) {
        val request: WorkRequest = OneTimeWorkRequest.Builder(ExportToFileWorker::class.java)
            .setInputData(exportInputData(deckDbPath, fileSynced))
            .addTag(TAG)
            .build()

        WorkManager
            .getInstance(context)
            .enqueue(request)
    }

    private fun exportInputData(deckDbPath: String, fileSynced: FileSynced): Data {
        return Data.Builder()
            .putString(FileSyncWorker.DECK_DB_PATH, deckDbPath)
            .putString(FileSyncWorker.FILE_URI, fileSynced.uri)
            .putBoolean(FileSyncWorker.AUTO_SYNC, fileSynced.autoSync)
            .build()
    }
}
