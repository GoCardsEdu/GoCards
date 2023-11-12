package pl.gocards.db.deck;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Service locator to maintain and caching only one connection per database.
 * https://developer.android.com/training/dependency-injection#di-alternatives
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public abstract class FolderDeckDbUtil<DB extends RoomDatabase> extends DeckDbUtil<DB> {

    protected FolderDeckDbUtil(@NonNull Context context) {
        super(context);
    }

    /**
     * @return true if the folder already exists and the folders were merged
     * @noinspection MismatchedJavadocCode
     */
    @SuppressLint("CheckResult")
    public Single<Boolean> renameFolder(
            @NonNull Context context,
            @NonNull Path fromFolderPath,
            String newFolderName
    ) throws IOException {

        Path newFolderPath = Paths.get(
                fromFolderPath.getParent().toString(),
                newFolderName
        );

        return renameFolder(context, fromFolderPath, newFolderPath);
    }

    /**
     * @return true if the folder already exists and the folders were merged
     * @noinspection MismatchedJavadocCode
     */
    @SuppressLint("CheckResult")
    protected Single<Boolean> renameFolder(
            @NonNull Context context,
            @NonNull Path fromFolderPath,
            @NonNull Path toFolderPath
    ) throws IOException {
        return moveFolder(context, fromFolderPath, toFolderPath);
    }

    /**
     * @return true if the folder already exists and the folders were merged
     * @noinspection MismatchedJavadocCode
     */
    @SuppressLint("CheckResult")
    public Single<Boolean> moveFolder(
            @NonNull Context context,
            @NonNull Path fromFolderPath,
            @NonNull Path toFolderPath
    ) throws IOException {

        boolean merged = Files.exists(toFolderPath);
        if (merged) {
            return Flowable.just(listDatabases(fromFolderPath))
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(dbPath -> moveDatabaseCompletable(context, dbPath, toFolderPath))
                    .andThen(deleteFolderCompletable(fromFolderPath))
                    .andThen(Single.just(true));
        } else {
            return Flowable.just(listDatabases(fromFolderPath))
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(this::flushDatabaseCompletable)
                    .andThen(renameFolderOnDisk(fromFolderPath, toFolderPath))
                    .andThen(renameFolderInDb(context, fromFolderPath, toFolderPath))
                    .andThen(Single.just(false))
                    .subscribeOn(Schedulers.io());
        }
    }

    protected CompletableSource renameFolderOnDisk(Path oldFolderPath, Path newFolderPath) {
        return Completable.fromRunnable(() -> {
            try {
                Files.move(oldFolderPath, newFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected Completable renameFolderInDb(
            @NonNull Context context,
            @NonNull Path oldFolderPath,
            @NonNull Path newFolderPath
    ) {
        return getAppDatabase(context).deckRxDao()
                .findByFolder(oldFolderPath.toString())
                .doOnSuccess(decks -> decks.forEach(deck -> {
                            String newPath = deck.getPath().replace(
                                    oldFolderPath.toString(),
                                    newFolderPath.toString()
                            );
                            deck.setPath(newPath);
                            getAppDatabase(context).deckDao().update(deck);
                        })
                )
                .toObservable()
                .ignoreElements();
    }

    protected Completable deleteFolderCompletable(Path folderPath) {
        return Completable.fromRunnable(() -> {
                    try {
                        Files.delete(folderPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Folder listing
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public List<Path> listFolders(@NonNull Path folder) throws IOException {
        try (Stream<Path> files = Files.list(folder)) {
            return files
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        }
    }

    @NonNull
    public List<Path> searchFolders(@NonNull Path path, @NonNull String query) throws IOException {
        String queryLowerCase = query.toLowerCase(Locale.getDefault());

        try (Stream<Path> files = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
            return files
                    .filter(Files::isDirectory)
                    .filter(folder -> !path.equals(folder))
                    .filter(folder -> {
                        String folderName = folder.getFileName().toString();
                        return folderName.toLowerCase(Locale.getDefault()).contains(queryLowerCase);
                    })
                    .collect(Collectors.toList());
        }
    }

    /* -----------------------------------------------------------------------------------------
     * F_D_05 Delete Folder
     * ----------------------------------------------------------------------------------------- */

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteFolder(@NonNull Path folder) throws IOException {
        try (Stream<Path> paths = Files.walk(folder)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}