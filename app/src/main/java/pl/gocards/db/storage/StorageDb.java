package pl.gocards.db.storage;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * Database file management like delete, open db, close db, check the list of available DBs.
 *
 * @author Grzegorz Ziemski
 */
public abstract class StorageDb<DB extends RoomDatabase> {

    @NonNull
    public DB getRoomDb(@NonNull Context context, @NonNull String path) {
        RoomDatabase.Builder<DB> dbBuilder = Room.databaseBuilder(
                context,
                getDbClass(),
                path
        );
        // turnOnDbLogs(dbBuilder);
        var db = dbBuilder.build();
        try {
            db.getOpenHelper().getWritableDatabase();
            return db;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @SuppressWarnings("unused")
    protected void turnOnDbLogs(@NonNull RoomDatabase.Builder<DB> dbBuilder) {
        dbBuilder.setQueryCallback(
                (sqlQuery, bindArgs) -> Log.i("Room", "SQL Query: " + sqlQuery + " SQL Args: " + bindArgs),
                Executors.newSingleThreadExecutor()
        );
    }

    @NonNull
    public abstract String getDbRootFolder(@NonNull Context context);

    @NonNull
    public Path getDbRootFolderPath(@NonNull Context context) {
        return Paths.get(getDbRootFolder(context));
    }

    @NonNull
    protected abstract Class<DB> getDbClass();
}