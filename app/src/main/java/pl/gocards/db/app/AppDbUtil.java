package pl.gocards.db.app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import java.lang.ref.WeakReference;

import kotlin.Suppress;
import pl.gocards.db.room.AppDatabase;

/**
 * Service locator to maintain only one database connection.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class AppDbUtil {

    protected final String APP_DB_NAME = "App.db";

    private static WeakReference<AppDbUtil> INSTANCE;

    private WeakReference<AppDatabase> appDatabase;

    @Suppress(names = "unused")
    protected AppDbUtil(Context context) {
    }

    public static synchronized AppDbUtil getInstance(@NonNull Context context) {
        if (INSTANCE == null || INSTANCE.get() == null) {
            INSTANCE = new WeakReference<>(new AppDbUtil(context));
        }
        return INSTANCE.get();
    }

    public AppDatabase getDatabase(@NonNull Context context) {
        if (appDatabase == null || appDatabase.get() == null) {
            appDatabase = new WeakReference<>(getRoomDb(context));
        }
        return appDatabase.get();
    }

    @NonNull
    protected AppDatabase getRoomDb(@NonNull Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                APP_DB_NAME
        ).build();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteDatabase(@NonNull Context context) {
        return context.deleteDatabase(APP_DB_NAME);
    }
}
