package pl.softfly.flashcards.db.storage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Database file management like delete, open db, close db, check the list of available dbs.
 *
 * @author Grzegorz Ziemski
 */
public abstract class StorageDb<DB extends RoomDatabase> {

    protected Context appContext;

    public StorageDb(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * @param name May be with or without .db at the end.
     */
    @NonNull
    public DB getDatabase(@NonNull File folder, String name) throws DatabaseException {
        return getDatabase(folder.getPath(), name);
    }

    @NonNull
    public DB getDatabase(@NonNull String folder, String name) throws DatabaseException {
        return getDatabase(folder + "/" + name);
    }

    /**
     * @param path May be with or without .db at the end.
     */
    @NonNull
    public DB getDatabase(@NonNull String path) throws DatabaseException {
        path = addDbFilenameExtensionIfRequired(path);
        if (!(new File(path)).isFile())
            throw new DatabaseException("The database does not exist: " + path);
        return getRoomDb(path);
    }

    /**
     * @param path May be with or without .db at the end.
     */
    @NonNull
    public DB createDatabase(@NonNull String path) throws DatabaseException {
        path = addDbFilenameExtensionIfRequired(path);
        if ((new File(path)).isFile()) throw new DatabaseException("The database already exist.");
        return getRoomDb(path);
    }

    protected DB getRoomDb(@NonNull String path) {
        return Room.databaseBuilder(
                appContext,
                getTClass(),
                path
        ).build();
    }

    @NonNull
    @Deprecated
    public List<Path> listFolders(@NonNull File folder) {
        return listFolders(Paths.get(folder.toURI()));
    }

    @NonNull
    public List<Path> listFolders(@NonNull Path folder) {
        try (Stream<Path> stream = Files.list(folder)) {
            return stream
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @NonNull
    @Deprecated
    public List<Path> listDatabases(@NonNull File folder) {
        return listDatabases(Paths.get(folder.toURI()));
    }

    @NonNull
    public List<Path> listDatabases(@NonNull Path folder) {
        try (Stream<Path> stream = Files.list(folder)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString();
                        return fileName.endsWith(".db");
                    })
                    .collect(Collectors.toList());
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Path> searchDatabases(Path path, String query) {
        try (Stream<Path> stream = Files.walk(
                path,
                FileVisitOption.FOLLOW_LINKS
        )) {
            String queryLowerCase = query.toLowerCase();
            return stream
                    .filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString();
                        return fileName.endsWith(".db")
                                && fileName.toLowerCase().contains(queryLowerCase);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace(); // TODO
            return null;
        }
    }

    public List<Path> searchFolders(Path path, String query) {
        try (Stream<Path> stream = Files.walk(
                path,
                FileVisitOption.FOLLOW_LINKS
        )) {
            String queryLowerCase = query.toLowerCase();
            return stream
                    .filter(Files::isDirectory)
                    .filter(folder -> !path.equals(folder))
                    .filter(folder -> {
                        String folderName = folder.getFileName().toString();
                        return folderName.toLowerCase().contains(queryLowerCase);
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace(); // TODO
            return null;
        }
    }

    public boolean exists(@NonNull File folder, @NonNull String name) {
        return exists(folder.getPath(), name);
    }

    public boolean exists(@NonNull String folder, @NonNull String name) {
        return exists(folder + "/" + name);
    }

    public boolean exists(@NonNull String path) {
        return (new File(addDbFilenameExtensionIfRequired(path))).exists();
    }

    public boolean deleteDatabase(@NonNull Path folder) {
        return deleteDatabase(folder.toString());
    }

    public boolean deleteDatabase(@NonNull String path) {
        path = addDbFilenameExtensionIfRequired(path);
        if (exists(path)) {
            (new File(path)).delete();
            (new File(path + "-shm")).delete();
            (new File(path + "-wal")).delete();
            return true;
        }
        return false;
    }

    public String findFreePath(@NonNull File folder, @NonNull String name) {
        return findFreePath(folder.getPath(), name);
    }

    public String findFreePath(@NonNull String folder, @NonNull String name) {
        return findFreePath(folder + "/" + name);
    }

    public String findFreePath(Path path) {
        return findFreePath(path.toString());
    }

    public String findFreePath(String dbPath) {
        String withoutExtension = dbPath.replace(".db", "");
        dbPath = withoutExtension + ".db";
        for (int i = 1; i <= 100; i++) {
            if (!exists(dbPath)) {
                return dbPath;
            }
            dbPath = withoutExtension + " " + i + ".db";
        }
        throw new RuntimeException("No free deck name found.");
    }

    public Path findFreeFolder(Path path) {
        for (int i = 1; i <= 100; i++) {
            if (!Files.exists(path)) {
                return path;
            }
            path = Paths.get(path.toString() + " " + i);
        }
        throw new RuntimeException("No free deck name found.");
    }

    @NonNull
    public String addDbFilenameExtensionIfRequired(@NonNull String deckName) {
        if (!deckName.endsWith(".db")) {
            return deckName + ".db";
        } else if (deckName.toLowerCase().endsWith(".db")) {
            return deckName.substring(0, deckName.length() - 3) + ".db";
        }
        return deckName;
    }

    @NonNull
    public abstract String getDbFolder();

    @NonNull
    public Path getDbFolderPath() {
        return Paths.get(getDbFolder());
    }

    @NonNull
    protected abstract Class<DB> getTClass();

}
