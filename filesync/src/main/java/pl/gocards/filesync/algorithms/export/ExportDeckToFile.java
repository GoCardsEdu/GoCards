package pl.gocards.filesync.algorithms.export;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.filesync.FileSynced;
import pl.gocards.room.util.TimeUtil;
import pl.gocards.filesync.algorithms.base.OpenFile;
import pl.gocards.filesync.db.FileSyncDeckDatabase;
import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;

/**
 * @author Grzegorz Ziemski
 */
public class ExportDeckToFile extends OpenFile {

    @NotNull
    protected final FileSyncDeckDatabase fsDeckDb;

    public ExportDeckToFile(
            @NonNull Context context,
            @NonNull String deckDbPath,
            @NonNull FileSynced fileSynced,
            @NonNull FileSyncDeckDatabase fsDeckDb
    ) {
        super(context, deckDbPath, fileSynced);
        this.fsDeckDb = fsDeckDb;
    }

    public void export(
            @NonNull OutputStream os,
            @NonNull String fileMimeType
    ) throws IOException {
        setWorkbook(Objects.requireNonNull(getWorkbookFactory().createWorkbook(fileMimeType)));
        setSheet(getWorkbook().createSheet(getDeckName(getDeckDbPath())));

        setTermIndex(0);
        setDefinitionIndex(1);
        setDisabledIndex(2);
        setSkipEmptyRows(0);

        createHeader();
        createCards(os);
    }

    public void createCards(@NonNull OutputStream os) throws IOException {
        List<Card> cards = fsDeckDb.cardDao().getCardsOrderByOrdinalAsc();

        for (int rowNum = 1; !cards.isEmpty(); rowNum++) {
            Card card = cards.remove(0);
            createRow(rowNum, card);

            if (cards.isEmpty()) {
                cards = fsDeckDb.cardDao().getCardsOrderByOrdinalAsc(card.getOrdinal());
            }
        }
        this.getWorkbook().write(os);
        this.getWorkbook().close();
        os.close();
    }

    protected void createRow(int rowNum, @NonNull Card card) {
        Row row = getSheet().createRow(rowNum);
        Cell cell = row.createCell(getTermIndex());
        cell.setCellValue(card.getTerm());

        cell = row.createCell(getDefinitionIndex());
        cell.setCellValue(card.getDefinition());

        cell = row.createCell(getDisabledIndex());
        cell.setCellValue(card.getDisabled());
    }

    protected void createHeader() {
        getSheet().setColumnWidth(getTermIndex(), 10000);
        getSheet().setColumnWidth(getDefinitionIndex(), 10000);

        Row row = getSheet().createRow(0);
        Cell cell = row.createCell(getTermIndex());
        cell.setCellValue("Term");
        cell.setHeaderStyle();

        cell = row.createCell(getDefinitionIndex());
        cell.setCellValue("Definition");
        cell.setHeaderStyle();

        cell = row.createCell(getDisabledIndex());
        cell.setCellValue("Disabled");
        cell.setHeaderStyle();
    }

    public void saveFileSynced() {
        saveFileSynced(TimeUtil.getNowEpochSec());
    }

    public void saveFileSynced(long lastSyncAt) {
        if (getFileSynced().getLastSyncAt() < lastSyncAt) {
            getFileSynced().setLastSyncAt(lastSyncAt);
        }
        fsDeckDb.fileSyncedDao().updateAll(getFileSynced());
        if (getFileSynced().getAutoSync()) {
            fsDeckDb.fileSyncedDao().disableAutoSyncByIdNot(Objects.requireNonNull(getFileSynced().getId()));
        }
    }
}