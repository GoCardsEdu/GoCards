package pl.softfly.flashcards.filesync.algorithms.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import pl.softfly.flashcards.db.TimeUtil;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.filesync.FileSynced;
import pl.softfly.flashcards.filesync.db.FileSyncDatabaseUtil;
import pl.softfly.flashcards.filesync.db.FileSyncDeckDatabase;

/**
 * @author Grzegorz Ziemski
 */
public abstract class OpenExcel extends AnalyzeExcelSchema {

    public static final int MULTIPLE_ORDINAL = 1;

    protected Context appContext;

    @Nullable
    protected FileSyncDeckDatabase fsDeckDb;

    protected Workbook workbook;

    protected Sheet sheet;

    protected FileSynced fileSynced;

    // Todo only for testing
    public OpenExcel() {
    }

    public OpenExcel(Context appContext) {
        this.appContext = appContext;
    }

    public FileSynced initFileSynced(String deckDbPath, String fileUri, boolean autoSync)
            throws DatabaseException {
        fsDeckDb = getFsDeckDb(deckDbPath);
        fileSynced = fsDeckDb.fileSyncedDao().findByUri(fileUri);
        if (fileSynced == null) {
            fileSynced = new FileSynced();
        }
        fileSynced.setUri(fileUri);
        fileSynced.setDisplayName(getDeckName(deckDbPath) + ".xlsx");
        fileSynced.setAutoSync(autoSync);
        if (fileSynced.getId() == null) {
            fileSynced.setId(Long.valueOf(fsDeckDb.fileSyncedDao().insert(fileSynced)).intValue());
        }
        return fileSynced;
    }

    public void saveFileSynced() {
        saveFileSynced(TimeUtil.getNowEpochSec());
    }

    public void saveFileSynced(long lastSyncAt) {
        if (fileSynced.getLastSyncAt() < lastSyncAt) {
            fileSynced.setLastSyncAt(lastSyncAt);
        }
        fsDeckDb.fileSyncedDao().updateAll(fileSynced);
        if (fileSynced.isAutoSync()) {
            fsDeckDb.fileSyncedDao().disableAutoSyncByIdNot(fileSynced.getId());
        }
    }

    @NonNull
    protected String getDeckName(@NonNull String deckDbPath) {
        return deckDbPath.substring(deckDbPath.lastIndexOf("/") + 1)
                .replace(".db", "");
    }

    //@todo public visibility for testing
    @Nullable
    private FileSyncDeckDatabase getFsDeckDb(@NonNull String deckName) throws DatabaseException {
        return FileSyncDatabaseUtil.getInstance(appContext).getDeckDatabase(deckName);
    }
}
