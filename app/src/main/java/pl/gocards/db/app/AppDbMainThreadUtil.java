package pl.gocards.db.app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import java.lang.ref.WeakReference;

import pl.gocards.db.room.AppDatabase;

/**
 * Service locator to maintain only one database connection.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class AppDbMainThreadUtil extends AppDbUtil {

    private static WeakReference<AppDbMainThreadUtil> INSTANCE;

    protected AppDbMainThreadUtil(Context context) {
        super(context);
    }

    public static synchronized AppDbMainThreadUtil getInstance(@NonNull Context context) {
        if (INSTANCE == null || INSTANCE.get() == null) {
            INSTANCE = new WeakReference<>(new AppDbMainThreadUtil(context));
        }
        return INSTANCE.get();
    }

    @SuppressWarnings("unused")
    @NonNull
    @Override
    protected AppDatabase getRoomDb(@NonNull Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                APP_DB_NAME
        ).allowMainThreadQueries().build();
    }
}
