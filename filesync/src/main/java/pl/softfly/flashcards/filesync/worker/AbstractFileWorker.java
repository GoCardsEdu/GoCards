package pl.softfly.flashcards.filesync.worker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author Grzegorz Ziemski
 */
public abstract class AbstractFileWorker extends Worker {

    private String fileDisplayName;
    private String fileMimeType;
    private Long fileLastModifiedAt;

    public AbstractFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected void askPermissions(Uri uri) {
        try {
            int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getApplicationContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (SecurityException e) {

        }
    }

    /**
     * https://developer.android.com/training/data-storage/shared/documents-files
     */
    protected void readMetaData(Uri fileUri) {
        try (Cursor cursor = getApplicationContext()
                .getContentResolver()
                .query(fileUri, null, null, null)
        ) {
            if (cursor != null && cursor.moveToFirst()) {
                fileDisplayName = getDisplayName(cursor);
                fileMimeType = getMimeType(cursor);
                fileLastModifiedAt = getLastModified(cursor);
            }
        }
    }

    protected InputStream openInputStream(Uri uri) throws FileNotFoundException {
        return getApplicationContext().getContentResolver().openInputStream(uri);
    }

    protected OutputStream openFileToWrite(Uri uri) throws FileNotFoundException {
        return getApplicationContext().getContentResolver().openOutputStream(uri);
    }

    @SuppressLint("Range")
    private String getDisplayName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
    }

    @SuppressLint("Range")
    private String getMimeType(Cursor cursor) {
        return getMetaString(cursor, DocumentsContract.Document.COLUMN_MIME_TYPE).toLowerCase();
    }

    @SuppressLint("Range")
    private Long getLastModified(Cursor cursor) {
        return TimeUnit.MILLISECONDS.toSeconds(getMetaLong(cursor, DocumentsContract.Document.COLUMN_LAST_MODIFIED));
    }

    @SuppressLint("Range")
    private String getMetaString(Cursor cursor, String col) {
        return cursor.getString(cursor.getColumnIndex(col));
    }

    @SuppressLint("Range")
    private Long getMetaLong(Cursor cursor, String col) {
        return cursor.getLong(cursor.getColumnIndex(col));
    }

    protected String getFileDisplayName() {
        return fileDisplayName;
    }

    protected String getFileMimeType() {
        return fileMimeType;
    }

    protected Long getFileLastModifiedAt() {
        return fileLastModifiedAt;
    }
}
