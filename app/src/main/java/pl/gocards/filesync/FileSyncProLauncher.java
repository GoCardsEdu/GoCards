package pl.gocards.filesync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;

import java.lang.reflect.InvocationTargetException;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import pl.gocards.db.storage.DatabaseException;

/**
 * @author Grzegorz Ziemski
 */
public interface FileSyncProLauncher {

    String FILESYNC_PRO_DECK_DB_PATH = "FILESYNC_PRO_DECK_DB_PATH";

    @Nullable
    static FileSyncProLauncher getInstance(
            @NonNull AppCompatActivity activity,
            @Nullable OnSyncSuccess onSyncSuccess,
            @NonNull CompositeDisposable disposable
    ) {
        try {
            return (FileSyncProLauncher) Class.forName("pl.gocards.filesync_pro.FileSyncProLauncherImpl")
                    .getConstructor(new Class[]{AppCompatActivity.class, OnSyncSuccess.class, CompositeDisposable.class})
                    .newInstance(activity, onSyncSuccess, disposable);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    void launchSyncFile(@NonNull String deckDbPath);

    /**
     * FS_PRO_A Automatically sync with file when deck is opened or closed.
     */
    void autoSync(
            @NonNull String deckDbPath,
            @NonNull CompositeDisposable disposable,
            @NonNull OnSyncSuccess onSyncSuccess,
            long afterTime
    ) throws DatabaseException;

    @Nullable
    String getDeckDbPath();

    void setDeckDbPath(@Nullable String deckDbPath);

    interface OnSyncSuccess {
        /** @noinspection unused*/
        void run(WorkInfo workInfo);
    }
}
