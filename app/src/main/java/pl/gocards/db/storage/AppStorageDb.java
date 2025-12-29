package pl.gocards.db.storage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import java.util.Objects;

/**
 * Database files are stored in app-specific storage.
 *
 * @author Grzegorz Ziemski
 */
public abstract class AppStorageDb<DB extends RoomDatabase> extends StorageDb<DB> {

    @NonNull
    @Override
    public String getDbRootFolder(@NonNull Context context) {
        return Objects.requireNonNull(context.getFilesDir().getParentFile()).getPath() + "/databases/decks";
    }

    @NonNull
    @Override
    protected abstract Class<DB> getDbClass();
}