package pl.gocards.filesync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * @author Grzegorz Ziemski
 */
public interface FileSyncLauncher {

    String TAG = "FileSync";

    String FILESYNC_DECK_DB_PATH = "FILESYNC_DECK_DB_PATH";

    @Nullable
    static FileSyncLauncher getInstance(
            @NonNull AppCompatActivity activity,
            @NonNull CompositeDisposable disposable
    ) {
        try {
            return (FileSyncLauncher) Class.forName("pl.gocards.filesync.FileSyncLauncherImpl")
                    .getConstructor(new Class[]{AppCompatActivity.class, CompositeDisposable.class})
                    .newInstance(activity, disposable);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    void launchExportToExcel(@NonNull String deckDbPath);

    /**
     * FS_E Export the deck to to a new file.
     */
    void launchExportToCsv(@NonNull String deckDbPath);

    /**
     * FS_I Import the file as a new deck.
     */
    void launchImportFile(@NonNull String importToFolder);

    @Nullable
    String getDeckDbPath();

    void setDeckDbPath(@Nullable String deckDbPath);
}
