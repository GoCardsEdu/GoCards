package pl.gocards.filesync.algorithms.base;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.filesync.sheet.BigWorkbookFactory;
import pl.gocards.filesync.sheet.WorkbookFactory;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.filesync.sheet.Sheet;
import pl.gocards.filesync.sheet.Workbook;

/**
 * @author Grzegorz Ziemski
 */
public abstract class OpenFile extends AnalyzeSheetSchema {

    public static final int MULTIPLE_ORDINAL = 1;

    @NonNull
    private final Context context;

    @NotNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Workbook workbook;

    @NotNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Sheet sheet;

    @NonNull
    private final FileSynced fileSynced;

    @NonNull
    private final String deckDbPath;

    @NonNull
    private final WorkbookFactory workbookFactory = new BigWorkbookFactory();

    public OpenFile(
            @NonNull Context context,
            @NonNull String deckDbPath,
            @NonNull FileSynced fileSynced
    ) {
        this.context = context;
        this.deckDbPath = deckDbPath;
        this.fileSynced = fileSynced;
    }

    @NonNull
    protected String getDeckName(@NonNull String deckDbPath) {
        return AppDeckDbUtil.getDeckName(deckDbPath);
    }

    @NonNull
    protected Context getContext() {
        return context;
    }

    @NotNull
    protected Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(@NotNull Workbook workbook) {
        this.workbook = workbook;
    }

    @NotNull
    protected Sheet getSheet() {
        return sheet;
    }

    public void setSheet(@NotNull Sheet sheet) {
        this.sheet = sheet;
    }

    @NonNull
    protected FileSynced getFileSynced() {
        return fileSynced;
    }

    @NonNull
    protected String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    public WorkbookFactory getWorkbookFactory() {
        return workbookFactory;
    }
}
