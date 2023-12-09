package pl.gocards.filesync.worker;

import static pl.gocards.filesync.sheet.WorkbookFactory.SUPPORTED_MIME_TYPES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.apache.commons.io.FilenameUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import pl.gocards.filesync.sheet.BigWorkbookFactory;
import pl.gocards.filesync.sheet.SheetWarningException;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public abstract class FileWorker extends Worker {
    private String fileDisplayName;
    private String fileMimeType;
    private Long fileModifiedAt;

    public FileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    protected void askPermissions(@NonNull Uri uri) {
        int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        getApplicationContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }

    /**
     * https://developer.android.com/training/data-storage/shared/documents-files
     */
    protected void readFileMetadata(@NonNull Uri fileUri) throws SheetWarningException {
        try (Cursor cursor = getApplicationContext()
                .getContentResolver()
                .query(fileUri, null, null, null)
        ) {
            if (cursor != null && cursor.moveToFirst()) {
                fileDisplayName = getDisplayName(cursor);
                fileMimeType = getMimeType(cursor);
                fileModifiedAt = getLastModified(cursor);
            }
        }
    }

    @NonNull
    protected InputStream openInputStream(@NonNull Uri uri) throws FileNotFoundException {
        return Objects.requireNonNull(getApplicationContext().getContentResolver().openInputStream(uri));
    }

    @NonNull
    protected OutputStream openFileToWrite(@NonNull Uri uri) throws FileNotFoundException {
        return Objects.requireNonNull(getApplicationContext().getContentResolver().openOutputStream(uri));
    }

    @NonNull
    protected String getString(@StringRes int id) {
        return getApplicationContext().getResources().getString(id);
    }

    @SuppressLint("Range")
    private String getDisplayName(@NonNull Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
    }

    @NonNull
    @SuppressLint("Range")
    private String getMimeType(@NonNull Cursor cursor) throws SheetWarningException {
        String mimeType = getMetaString(cursor, DocumentsContract.Document.COLUMN_MIME_TYPE)
                .toLowerCase(Locale.getDefault());

        if (Arrays.asList(SUPPORTED_MIME_TYPES).contains(mimeType)) {
            return mimeType;
        } else {
            return guessMimeType();
        }
    }

    @NonNull
    private String guessMimeType() throws SheetWarningException {
        String removeIncrement = fileDisplayName.replaceAll(" [(]\\d+[)]$", "");
        String extension = FilenameUtils.getExtension(removeIncrement);
        return BigWorkbookFactory.getMimeType(extension);
    }

    @NonNull
    @SuppressLint("Range")
    private Long getLastModified(@NonNull Cursor cursor) {
        return TimeUnit.MILLISECONDS.toSeconds(
                getMetaLong(cursor, DocumentsContract.Document.COLUMN_LAST_MODIFIED)
        );
    }

    @SuppressWarnings("SameParameterValue")
    @SuppressLint("Range")
    private String getMetaString(@NonNull Cursor cursor, String col) {
        return cursor.getString(cursor.getColumnIndex(col));
    }

    @NonNull
    @SuppressLint("Range")
    @SuppressWarnings("SameParameterValue")
    private Long getMetaLong(@NonNull Cursor cursor, String col) {
        return cursor.getLong(cursor.getColumnIndex(col));
    }

    protected String getFileDisplayName() {
        return fileDisplayName;
    }

    protected String getFileMimeType() {
        return fileMimeType;
    }

    protected Long getFileModifiedAt() {
        return fileModifiedAt;
    }

    protected void setFileModifiedAt(Long fileModifiedAt) {
        this.fileModifiedAt = fileModifiedAt;
    }
}
