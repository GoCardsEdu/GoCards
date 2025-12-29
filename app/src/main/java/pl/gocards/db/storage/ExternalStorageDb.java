package pl.gocards.db.storage;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import pl.gocards.R;

/**
 * Database files are stored in shared storage.
 * Only used for testing / development purposes.
 *
 * @author Grzegorz Ziemski
 * @deprecated API level 30 (Android 11) blocks access to shared storage.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public abstract class ExternalStorageDb<DB extends RoomDatabase> extends StorageDb<DB> {

    @NonNull
    @Override
    public String getDbRootFolder(@NonNull Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getResources().getString(R.string.app_name) + "/";
    }

    @NonNull
    @Override
    protected abstract Class<DB> getDbClass();

}
