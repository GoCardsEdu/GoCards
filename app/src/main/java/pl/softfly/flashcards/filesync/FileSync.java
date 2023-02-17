package pl.softfly.flashcards.filesync;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pl.softfly.flashcards.db.storage.DatabaseException;

public interface FileSync {

    String TAG = "FileSync";
    String TYPE_XLS = "application/vnd.ms-excel";
    String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Nullable
    static FileSync getInstance() {
        try {
            return (FileSync) Class.forName("pl.softfly.flashcards.filesync.FileSyncBean")
                    .getConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * FS_I Import the Excel file as a new deck.
     */
    void importFile(
            @NonNull AppCompatActivity activity,
            @NonNull String importToFolderPath,
            @NonNull Uri fileUri
    );

    /**
     * FS_E Export the deck to a new Excel file.
     */
    void exportFile(
            @NonNull AppCompatActivity activity,
            @NonNull String deckDbPath,
            @NonNull Uri fileUri
    ) throws DatabaseException;
}
