package pl.gocards.db.deck;

import android.content.Context;

import androidx.annotation.NonNull;

import pl.gocards.db.room.DeckDatabase;

/**
 * Service locator to maintain and caching only one connection per database.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class AppDeckDbUtil extends FolderDeckDbUtil<DeckDatabase> {

    private static AppDeckDbUtil INSTANCE;

    protected AppDeckDbUtil(@NonNull Context context) {
        super(context);
    }

    @NonNull
    public static synchronized AppDeckDbUtil getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppDeckDbUtil(context);
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    protected Class<DeckDatabase> getDbClass() {
        return DeckDatabase.class;
    }
}
