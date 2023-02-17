package pl.softfly.flashcards.filesync;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.softfly.flashcards.db.storage.DatabaseException;

public interface FileSyncPro extends FileSync {

    @Nullable
    static FileSyncPro getInstance() {
        try {
            return (FileSyncPro) Class.forName("pl.softfly.flashcards.filesync_pro.FileSyncProBean")
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * FS_S Synchronize the deck with an Excel file.
     */
    void syncFile(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Uri fileUri
    ) throws DatabaseException;

    /**
     * FS_A Automatically sync with Excel file when deck is opened or closed.
     */
    void autoSync(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            long autoSyncAfterTime
    ) throws DatabaseException;
}
