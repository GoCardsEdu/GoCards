package pl.gocards.room.util;

import androidx.annotation.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * @author Grzegorz Ziemski
 */
public class DbUtil {

    @NonNull
    public static String removeDbExtension(@NonNull String deckName) {
        if (deckName.toLowerCase(Locale.getDefault()).endsWith(".db")) {
            return deckName.substring(0, deckName.length() - 3);
        }
        return deckName;
    }

    @NonNull
    public static String addDbExtension(@NonNull String dbPath) {
        if (!dbPath.endsWith(".db")) {
            return dbPath + ".db";
        } else if (dbPath.toLowerCase(Locale.getDefault()).endsWith(".db")) {
            return dbPath.substring(0, dbPath.length() - 3) + ".db";
        }
        return dbPath;
    }

    public static Path addDbExtension(@NonNull Path dbPath) {
        String dbPathString = dbPath.toString();
        String dbPathLowerCase = dbPathString.toLowerCase(Locale.getDefault());

        if (dbPathString.endsWith(".db")) {
            return dbPath;
        } else if (dbPathLowerCase.endsWith(".db")) {
            return Paths.get(dbPath.toString().substring(0, dbPath.toString().length() - 3) + ".db");
        } else {
            return Paths.get(dbPath + ".db");
        }
    }

    @NonNull
    @SuppressWarnings("unused")
    public static String getDeckName(@NonNull Path dbPath) {
        return getDeckName(dbPath.toString());
    }

    @NonNull
    public static String getDeckName(@NonNull String dbPath) {
        return removeDbExtension(dbPath).substring(dbPath.lastIndexOf("/") + 1);
    }

}
