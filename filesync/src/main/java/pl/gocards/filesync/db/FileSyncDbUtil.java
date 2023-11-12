package pl.gocards.filesync.db;

import android.content.Context;

import androidx.annotation.NonNull;

import pl.gocards.db.deck.FolderDeckDbUtil;
import pl.gocards.db.storage.DatabaseException;

/**
 * Service locator to maintain and caching only one connection per database.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class FileSyncDbUtil extends FolderDeckDbUtil<FileSyncDeckDatabase> {

    private static FileSyncDbUtil INSTANCE;

    protected FileSyncDbUtil(@NonNull Context context) {
        super(context);
    }

    @NonNull
    public static synchronized FileSyncDbUtil getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FileSyncDbUtil(context);
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    protected Class<FileSyncDeckDatabase> getDbClass() {
        return FileSyncDeckDatabase.class;
    }

    /**
     * @deprecated Use getFreshDatabase
     * Don't use the cache. Because perhaps the DB was removed and new DB created with this same name in the UI.
     */
    @NonNull
    @Override
    @Deprecated
    public synchronized FileSyncDeckDatabase getDatabase(@NonNull Context context, @NonNull String dbPath)
            throws DatabaseException {
        return getFreshDatabase(context, dbPath);
    }
}
