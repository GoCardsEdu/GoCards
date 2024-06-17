package pl.gocards.ui.decks.kt.decks.service

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.UiThread
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.storage.DatabaseException
import pl.gocards.room.util.DbUtil
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Objects

/**
 * @author Grzegorz Ziemski
 */
open class ExportImportDbUtil(protected var context: Context) {
    
    protected lateinit var path: String

    /**
     * D_R_12 Export database
     */
    protected fun launchExportDb(
        exportDbLauncher: ActivityResultLauncher<String>,
        dbPath: String
    ) {
        path = dbPath
        exportDbLauncher.launch(DbUtil.addDbExtension(getDeckName(dbPath)))
    }

    /**
     * D_C_13 Import database
     */
    protected fun launchImportDb(
        importDbLauncher: ActivityResultLauncher<Array<String>>,
        importToFolder: String
    ) {
        path = importToFolder
        importDbLauncher.launch(
            arrayOf(
                "application/vnd.sqlite3",
                "application/octet-stream"
            )
        )
    }

    /**
     * D_R_12 Export database
     */
    @SuppressLint("CheckResult")
    protected open fun exportDb(
        exportToUri: Uri,
        dbPath: String
    ) {
        flushDatabase(dbPath)

        context
            .contentResolver
            .openOutputStream(exportToUri)
            .use { output ->
                FileInputStream(dbPath)
                    .use { input ->
                        copyData(input, output!!)
                    }
            }
    }

    private fun flushDatabase(dbPath: String) {
        try {
            getAppDeckDbUtil()
                .flushDatabase(dbPath)
        } catch (e: DatabaseException) {
            throw RuntimeException(e)
        }
    }

    private fun copyData(input: FileInputStream, output: OutputStream) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(input, Objects.requireNonNull(output))
        } else {
            val inChannel = input.channel
            val outChannel = (output as FileOutputStream).channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
        }
    }

    /**
     * D_C_13 Import database
     */
    @SuppressLint("Range", "CheckResult")
    protected open fun importDb(
        importToFolder: String,
        importedDbUri: Uri,
        onSuccess: (deckDbPath: String) -> Unit
    ) {
        try {
            var deckDbPath: String = getDeckDbPath(importedDbUri)!!
            deckDbPath = findFreePath(importToFolder, deckDbPath)

            context.contentResolver.openInputStream(importedDbUri)!!
                .use { input ->
                    FileOutputStream(deckDbPath)
                        .use { output ->
                            copyData(input, output)
                        }
                }

            onSuccess(deckDbPath)
        } catch (e: IOException) {
            onErrorImportDb(e)
        }
    }

    protected open fun onErrorImportDb(e: Throwable) {
        e.printStackTrace()
    }

    private fun copyData(input: InputStream, output: FileOutputStream) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtils.copy(input, output)
        } else {
            val inChannel = (input as FileInputStream?)!!.channel
            val outChannel = output.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
        }
    }

    @SuppressLint("Range")
    private fun getDeckDbPath(importedDbUri: Uri): String? {
        context
            .contentResolver
            .query(importedDbUri, null, null, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }

        return null
    }

    private fun findFreePath(
        importToFolder: String,
        deckDbPath: String
    ): String {
        return getAppDeckDbUtil().findFreePath(importToFolder, deckDbPath)
    }

    protected fun getDeckName(dbPath: String): String {
        return AppDeckDbUtil.getDeckName(dbPath)
    }

    private fun getAppDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(context)
    }

    @UiThread
    protected fun showShortToastMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
