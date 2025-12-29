package pl.gocards.db.deck;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.db.app.AppDbUtil;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.storage.AppStorageDb;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.db.storage.ExternalStorageDb;
import pl.gocards.db.storage.StorageDb;
import pl.gocards.room.util.DbUtil;
import pl.gocards.util.Config;

/**
 * @author Grzegorz Ziemski
 */
public abstract class DeckDbUtil<DB extends RoomDatabase> {

    /**
     * key = DB path with .db extension
     */
    private final Map<String, DB> decks = new WeakHashMap<>();

    @NonNull
    protected final StorageDb<DB> storageDb;

    protected DeckDbUtil(@NonNull Context context) {
        this.storageDb = createStorageDb(context);
    }

    @NonNull
    @SuppressWarnings({"deprecation"})
    protected StorageDb<DB> createStorageDb(@NonNull Context context) {
        return Config.getInstance(context).isDatabaseExternalStorage() ?
                new ExternalStorageDb<>() {
                    @NonNull
                    @Override
                    protected Class<DB> getDbClass() {
                        return DeckDbUtil.this.getDbClass();
                    }
                } :
                new AppStorageDb<>() {
                    @NonNull
                    @Override
                    protected Class<DB> getDbClass() {
                        return DeckDbUtil.this.getDbClass();
                    }
                };
    }

    protected abstract Class<DB> getDbClass();

    @NonNull
    @SuppressWarnings("unused")
    public DB getDatabase(@NonNull Context context, @NonNull File folder, @NonNull String name)
            throws DatabaseException {
        return getDatabase(context, folder.getPath(), name);
    }

    @NonNull
    public DB getDatabase(@NonNull Context context, @NonNull String folder, @NonNull String name)
            throws DatabaseException {
        return getDatabase(context, folder + "/" + name);
    }

    @NonNull
    public synchronized DB getDatabase(@NonNull Context context, @NonNull String dbPath)
            throws DatabaseException {

        Objects.requireNonNull(dbPath);
        dbPath = DbUtil.addDbExtension(dbPath);
        checkExists(dbPath);

        DB db = decks.get(dbPath);
        if (db == null || !db.isOpen()) {
            db = storageDb.getRoomDb(context, dbPath);
            decks.put(dbPath, db);
        }
        return db;
    }

    @NonNull
    public synchronized DB getFreshDatabase(@NonNull Context context, @NonNull String dbPath)
            throws DatabaseException {

        Objects.requireNonNull(dbPath);
        dbPath = DbUtil.addDbExtension(dbPath);
        checkExists(dbPath);

        DB db = storageDb.getRoomDb(context, dbPath);
        decks.put(dbPath, db);
        return db;
    }

    public synchronized void closeDatabase(@NonNull String dbPath) {
        Objects.nonNull(dbPath);
        dbPath = DbUtil.addDbExtension(dbPath);

        DB db = decks.get(dbPath);
        if (db != null) {
            if (db.isOpen()) db.close();
            decks.remove(dbPath);
        }
    }

    @NonNull
    public synchronized DB createDatabase(@NonNull Context context, @NonNull String dbPath)
            throws DatabaseException {
        Objects.nonNull(dbPath);
        dbPath = DbUtil.addDbExtension(dbPath);
        checkNotExists(dbPath);

        DB db = storageDb.getRoomDb(context, dbPath);
        decks.put(dbPath, db);

        AppDbUtil
                .getInstance(context)
                .getDatabase(context)
                .deckRxDao()
                .refreshLastUpdatedAt(dbPath)
                .subscribeOn(Schedulers.io())
                .ignoreElement()
                .subscribe();

        return db;
    }

    @SuppressWarnings("unused")
    public boolean exists(@NonNull Path dbPath) {
        return Files.exists(DbUtil.addDbExtension(dbPath));
    }

    public boolean exists(@NonNull String dbPath) {
        return Files.exists(Paths.get(DbUtil.addDbExtension(dbPath)));
    }

    protected void checkExists(@NonNull String dbPath) throws DatabaseException {
        if (!exists(dbPath)) {
            throw new DatabaseException("The database does not exist: " + dbPath);
        }
    }

    protected void checkNotExists(@NonNull String dbPath) throws DatabaseException {
        if (exists(dbPath)) {
            throw new DatabaseException("The database exists: " + dbPath);
        }
    }

    /**
     * Use before moving the database.
     */
    public synchronized void flushDatabase(@NonNull Path dbPath)
            throws DatabaseException {
        Objects.nonNull(dbPath);
        flushDatabase(dbPath.toString());
    }

    /**
     * Use before moving the database.
     */
    public synchronized void flushDatabase(@NonNull String dbPath)
            throws DatabaseException {
        dbPath = DbUtil.addDbExtension(dbPath);

        checkExists(dbPath);
        closeDatabase(dbPath);

        // It is not use Room, just in case the schema is broken.
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor c = db.rawQuery("pragma wal_checkpoint(full)", null);
        if (c.moveToFirst() && c.getInt(0) == 1)
            throw new DatabaseException("Checkpoint was blocked from completing");
        c.close();
        db.close();
    }

    protected Completable flushDatabaseCompletable(@NonNull Path dbPath) {
        return Completable.fromRunnable(() -> {
            try {
                flushDatabase(dbPath);
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean deleteDatabase(@NonNull Path dbPath) {
        dbPath = DbUtil.addDbExtension(dbPath);

        if (Files.exists(dbPath)) {

            String dbPathString = dbPath.toString();
            try {
                Files.delete(dbPath);
                Files.deleteIfExists(Paths.get(dbPathString + "-shm"));
                Files.deleteIfExists(Paths.get(dbPathString + "-wal"));
                closeDatabase(dbPathString);
                return true;
            } catch (NoSuchFileException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteDatabase(@NonNull String dbPath) {
        return deleteDatabase(Paths.get(dbPath));
    }

    public Completable deleteDatabaseCompletable(@NonNull Context context, @NonNull Path dbPath) {
        return Completable.fromRunnable(() -> deleteDatabase(dbPath))
                .andThen(deleteDeckFromAppDb(context, dbPath));
    }

    private Completable deleteDeckFromAppDb(@NonNull Context context, @NonNull Path dbPath) {
        return this.deleteDeckFromAppDb(context, dbPath.toString());
    }

    private Completable deleteDeckFromAppDb(@NonNull Context context, @NonNull String dbPath) {
        return getAppDatabase(context)
                .deckRxDao()
                .deleteByPath(dbPath)
                .subscribeOn(Schedulers.io());
    }

    /* -----------------------------------------------------------------------------------------
     * Renaming, moving, copying the database
     * ----------------------------------------------------------------------------------------- */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    public void renameDatabase(
            @NonNull Context context,
            @NonNull Path currentDeckPath,
            @NonNull String newName,
            Runnable onSuccess,
            Runnable onExists
    ) throws DatabaseException {
        if (Objects.equals(newName, AppDeckDbUtil.getDeckName(currentDeckPath))) {
            if (onSuccess != null) onSuccess.run();
            return;
        }

        Path newDbPath = Paths.get(currentDeckPath.getParent().toString(), newName);
        if (exists(newDbPath)) {
            if (onExists != null) onExists.run();
            return;
        }

        renameDatabase(context, currentDeckPath, newName)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(deckName -> onSuccess.run())
                .ignoreElement()
                .subscribe(EMPTY_ACTION);
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    public Single<String> renameDatabase(@NonNull Context context, Path dbPath, String newName)
            throws DatabaseException {

        Objects.nonNull(dbPath);
        Objects.nonNull(newName);

        dbPath = DbUtil.addDbExtension(dbPath);
        checkExists(dbPath.toString());

        newName = DbUtil.addDbExtension(newName);

        return moveDatabaseProcess(
                context,
                dbPath,
                Paths.get(dbPath.getParent().toString(), newName)
        );
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    @Deprecated
    public Single<String> moveDatabase(@NonNull Context context, String dbPath, String toFolder) {
        return moveDatabase(context, Paths.get(dbPath), Paths.get(toFolder));
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    public Single<String> moveDatabase(
            @NonNull Context context,
            @NonNull Path dbPath,
            @NonNull Path toFolder
    ) {
        Objects.nonNull(dbPath);
        Objects.nonNull(toFolder);

        checkDbExtension(dbPath.toString());

        Path newPath = Paths.get(toFolder.toString(), dbPath.getFileName().toString());
        return moveDatabaseProcess(context, dbPath, newPath);
    }

    protected Completable moveDatabaseCompletable(
            @NonNull Context context,
            @NonNull Path fromPath,
            @NonNull Path toPath
    ) {
        return moveDatabase(context, fromPath, toPath)
                .toObservable()
                .ignoreElements();
    }

    /**
     * @return New file name. If the file already exists, a number is added to the end.
     */
    protected Single<String> moveDatabaseProcess(
            @NonNull Context context,
            @NonNull Path oldDbPath,
            @NonNull Path newDbPath
    ) {
        newDbPath = findFreePath(newDbPath);

        return flushDatabaseCompletable(oldDbPath)
                .andThen(moveDatabaseFile(oldDbPath, newDbPath))
                .andThen(getAppDatabase(context).deckRxDao().updatePathByPath(
                        oldDbPath.toString(),
                        newDbPath.toString()
                ))
                .andThen(Single.just(getDeckName(newDbPath.toString())))
                .subscribeOn(Schedulers.io());
    }

    protected Completable moveDatabaseFile(
            @NonNull Path oldDbPath,
            @NonNull Path newDbPath
    ) {
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
    @NonNull
    public String findFreePath(@NonNull String folder, @NonNull String name) {
        return findFreePath(folder + "/" + name);
    }

    public Path findFreePath(@NonNull Path dbPath) {
        dbPath = DbUtil.addDbExtension(dbPath);
        if (!Files.exists(dbPath)) {
            return dbPath;
        }

        String noDbExtension = DbUtil.removeDbExtension(dbPath.toString());
        String dbPathS = noDbExtension + ".db";

        for (int i = 1; i <= 100; i++) {
            dbPath = Paths.get(dbPathS);
            if (!Files.exists(dbPath)) {
                return dbPath;
            }
            dbPathS = noDbExtension + " " + i + ".db";
        }
        throw new RuntimeException(new DatabaseException("No free deck name found."));
    }

    @NonNull
    public String findFreePath(@NonNull String dbPath) {
        String withoutExtension = DbUtil.removeDbExtension(dbPath);
        dbPath = withoutExtension + ".db";
        for (int i = 1; i <= 100; i++) {
            if (!exists(dbPath)) {
                return dbPath;
            }
            dbPath = withoutExtension + " " + i + ".db";
        }
        throw new RuntimeException(new DatabaseException("No free deck name found."));
    }

    /* -----------------------------------------------------------------------------------------
     * Database listing
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Deprecated
    public List<Path> listDatabases(@NonNull File folder) throws IOException {
        return listDatabases(Paths.get(folder.toURI()));
    }

    @NonNull
    public List<Path> listDatabases(@NonNull Path folder) throws IOException {
        try (Stream<Path> files = Files.walk(folder, 1)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".db"))
                    .collect(Collectors.toList());
        }
    }

    @NonNull
    public List<Path> searchDatabases(Path path, @NonNull String query) throws IOException {
        String queryLowerCase = query.toLowerCase(Locale.getDefault());

        try (Stream<Path> files = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
            return files.filter(Files::isRegularFile)
                    .filter(file -> {
                        String fileName = file.getFileName().toString();
                        return fileName.endsWith(".db")
                                && fileName.toLowerCase(Locale.getDefault()).contains(queryLowerCase);
                    })
                    .collect(Collectors.toList());
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    protected void checkDbExtension(@NonNull String dbPath) {
        if (!dbPath.endsWith(".db")) {
            throw new RuntimeException(new DatabaseException("The database does not have the .db extension."));
        }
    }

    @NonNull
    public static String getDeckName(@NonNull Path dbPath) {
        return getDeckName(dbPath.toString());
    }

    @NonNull
    public static String getDeckName(@NonNull String dbPath) {
        return DbUtil.removeDbExtension(dbPath).substring(dbPath.lastIndexOf("/") + 1);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    protected AppDatabase getAppDatabase(@NonNull Context context) {
        return AppDbUtil
                .getInstance(context)
                .getDatabase(context);
    }

    @NonNull
    public Path getDbRootFolderPath(@NonNull Context context) {
        return storageDb.getDbRootFolderPath(context);
    }
}