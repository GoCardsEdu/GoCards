package pl.softfly.flashcards.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.Config;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.db.room.AppDatabase;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.AppStorageDb;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.db.storage.ExternalStorageDb;
import pl.softfly.flashcards.db.storage.StorageDb;

/**
 * Service locator to maintain and caching only one connection per database.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
public class DeckDatabaseUtil {

    private static DeckDatabaseUtil INSTANCE;

    /**
     * key = Db path with .db extension
     */
    private final Map<String, DeckDatabase> decks = new WeakHashMap<>();

    @NonNull
    private final StorageDb<DeckDatabase> storageDb;

    private final Context context;

    protected DeckDatabaseUtil(@NonNull Context context) {
        this.context = context;
        this.storageDb = Config.getInstance(context).isDatabaseExternalStorage() ?
                new ExternalStorageDb<DeckDatabase>(context) {
                    @NonNull
                    @Override
                    protected Class<DeckDatabase> getTClass() {
                        return DeckDatabase.class;
                    }
                } :
                new AppStorageDb<DeckDatabase>(context) {
                    @NonNull
                    @Override
                    protected Class<DeckDatabase> getTClass() {
                        return DeckDatabase.class;
                    }
                };
    }

    public static synchronized DeckDatabaseUtil getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DeckDatabaseUtil(context);
        }
        return INSTANCE;
    }

    @NonNull
    public DeckDatabase getDatabase(@NonNull File folder, @NonNull String name)
            throws DatabaseException {
        return getDatabase(folder.getPath(), name);
    }

    @NonNull
    public synchronized DeckDatabase getDatabase(@NonNull String folder, @NonNull String name)
            throws DatabaseException {
        return getDatabase(folder + "/" + name);
    }

    @NonNull
    public synchronized DeckDatabase getDatabase(@NonNull String dbPath)
            throws DatabaseException {

        Objects.nonNull(dbPath);
        checkDbExtension(dbPath);

        DeckDatabase db = decks.get(dbPath);
        if (db == null) {
            db = storageDb.getDatabase(dbPath);
            decks.put(dbPath, db);
        } else if (!db.isOpen()) {
            db = storageDb.getDatabase(dbPath);
            decks.put(dbPath, db);
        }
        return db;
    }

    @NonNull
    public synchronized DeckDatabase createDatabase(@NonNull String dbPath)
            throws DatabaseException {
        Objects.nonNull(dbPath);
        dbPath = addDbExtension(dbPath);
        DeckDatabase db = storageDb.createDatabase(dbPath);
        decks.put(dbPath, db);
        return db;
    }

    public synchronized void closeDatabase(String dbPath)
            throws DatabaseException {

        Objects.nonNull(dbPath);
        checkDbExtension(dbPath);

        DeckDatabase db = getDatabase(dbPath);
        if (db != null) {
            decks.remove(dbPath);
            Cursor c = db.query(new SimpleSQLiteQuery("pragma wal_checkpoint(full)"));
            if (c.moveToFirst() && c.getInt(0) == 1)
                throw new RuntimeException("Checkpoint was blocked from completing");
            db.close();
        }
    }

    protected Completable closeDatabaseCompletable(String dbPath) {
        return Completable.fromRunnable(() -> {
            try {
                closeDatabase(dbPath);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    public Single<String> renameDatabase(Path dbPath, String newName) {
        Objects.nonNull(dbPath);
        Objects.nonNull(newName);
        checkDbExtension(dbPath.toString());

        newName = storageDb.addDbFilenameExtensionIfRequired(newName);

        return moveDatabaseHelper(
                dbPath,
                Paths.get(dbPath.getParent().toString(), newName)
        );
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    @Deprecated
    public Single<String> moveDatabase(String dbPath, String toFolder) {
        return moveDatabase(Paths.get(dbPath), Paths.get(toFolder));
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    public Single<String> moveDatabase(Path dbPath, Path toFolder) {
        Objects.nonNull(dbPath);
        Objects.nonNull(toFolder);
        checkDbExtension(dbPath.toString());

        Path newPath = Paths.get(toFolder.toString(), dbPath.getFileName().toString());
        return moveDatabaseHelper(dbPath, newPath);
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    protected Single<String> moveDatabaseHelper(Path oldDbPath, Path newDbPath) {
        newDbPath = Paths.get(storageDb.findFreePath(newDbPath));

        return closeDatabaseCompletable(oldDbPath.toString())
                .andThen(moveDatabaseFile(oldDbPath, newDbPath))
                .andThen(getAppDatabase().deckDaoAsync().updatePathByPath(
                        oldDbPath.toString(),
                        newDbPath.toString()
                ))
                .andThen(Single.just(getDbName(newDbPath.toString())))
                .subscribeOn(Schedulers.io());
    }

    protected Completable moveDatabaseFile(Path oldDbPath, Path newDbPath) {
        return Completable.fromRunnable(() -> {
            try {
                Files.move(oldDbPath, newDbPath);
                Files.deleteIfExists(Paths.get(oldDbPath + "-shm"));
                Files.deleteIfExists(Paths.get(oldDbPath + "-wal"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * @return true if the folder already exists
     */
    @SuppressLint("CheckResult")
    public Single<Boolean> renameFolder(Path fromFolderPath, String newFolderName)
            throws IOException {

        Path newFolderPath = Paths.get(
                fromFolderPath.getParent().toString(),
                newFolderName
        );

        return renameFolder(fromFolderPath, newFolderPath);
    }

    /**
     * @return true if the folder already exists
     */
    @SuppressLint("CheckResult")
    protected Single<Boolean> renameFolder(Path fromFolderPath, Path toFolderPath)
            throws IOException {

        return moveFolder(fromFolderPath, toFolderPath);
    }

    protected CompletableSource renameFolderInFiles(Path oldFolderPath, Path newFolderPath) {
        return Completable.fromRunnable(() -> {
            try {
                Files.move(oldFolderPath, newFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected Completable renameFoldersInDb(Path oldFolderPath, Path newFolderPath) {
        return getAppDatabase().deckDaoAsync()
                .findByFolder(oldFolderPath.toString())
                .doOnSuccess(decks -> decks.forEach(deck -> {
                            String newPath = deck.getPath().replace(
                                    oldFolderPath.toString(),
                                    newFolderPath.toString()
                            );
                            deck.setPath(newPath);
                            getAppDatabase().deckDao().update(deck);
                        })
                )
                .toObservable()
                .ignoreElements();
    }

    /**
     * @return true if the folder already exists
     */
    @SuppressLint("CheckResult")
    public Single<Boolean> moveFolder(Path fromFolderPath, Path toFolderPath)
            throws IOException {

        boolean merged = Files.exists(toFolderPath);
        if (merged) {
            return Flowable.just(getDeckPaths(fromFolderPath))
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(dbPath -> moveDatabaseCompletable(Paths.get(dbPath), toFolderPath))
                    .andThen(deleteFolder(fromFolderPath))
                    .andThen(Single.just(true));
        } else {
            return Flowable.just(getDeckPaths(fromFolderPath))
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(dbPath -> closeDatabaseCompletable(dbPath))
                    .andThen(renameFolderInFiles(fromFolderPath, toFolderPath))
                    .andThen(renameFoldersInDb(fromFolderPath, toFolderPath))
                    .andThen(Single.just(false))
                    .subscribeOn(Schedulers.io());
        }
    }

    protected Completable moveDatabaseCompletable(Path fromPath, Path toPath) {
        return moveDatabase(fromPath, toPath)
                .toObservable()
                .ignoreElements();
    }

    protected Completable deleteFolder(Path folderPath) {
        return Completable.fromRunnable(() -> {
                    try {
                        Files.delete(folderPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    protected List<String> getDeckPaths(Path folder)
            throws IOException {

        return Files.walk(folder)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".db"))
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    protected String getDbName(String path) {
        return path.substring(path.lastIndexOf("/") + 1)
                .replace(".db", "");
    }

    public Completable deleteDatabase(String path) {
        return Completable.fromRunnable(() -> storageDb.deleteDatabase(path))
                .andThen(deleteDeckFromAppDb(path));
    }

    protected Completable deleteDeckFromAppDb(String path) {
        return getAppDatabase()
                .deckDaoAsync()
                .deleteByPath(path)
                .subscribeOn(Schedulers.io());
    }

    protected void checkDbExtension(String dbPath) {
        if (!dbPath.endsWith(".db")) {
            throw new RuntimeException("The database does not have the .db extension.");
        }
    }

    public static String addDbExtension(@NonNull String deckName) {
        if (!deckName.endsWith(".db")) {
            return deckName + ".db";
        } else if (deckName.toLowerCase().endsWith(".db")) {
            return deckName.substring(0, deckName.length() - 3) + ".db";
        }
        return deckName;
    }

    public static String removeDbExtension(@NonNull String deckName) {
        if (deckName.toLowerCase().endsWith(".db")) {
            return deckName.substring(0, deckName.length() - 3);
        }
        return deckName;
    }

    @NonNull
    public StorageDb<DeckDatabase> getStorageDb() {
        return storageDb;
    }

    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(context)
                .getDatabase();
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
