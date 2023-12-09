package pl.gocards.filesync.sheet.csv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class CsvRow implements Row {

    @NonNull
    private final List<CsvRow> rows;

    @NonNull
    private final List<CsvCell> cells;

    public CsvRow(@NonNull List<CsvRow> rows) {
        this(rows, new LinkedList<>());
    }

    public CsvRow(@NonNull List<CsvRow> rows, @NonNull List<CsvCell> cells) {
        this.cells = cells;
        this.rows = rows;
    }

    @Override
    public CsvCell getCell(int i) {
        if (i < cells.size()) {
            return cells.get(i);
        } else {
            return null;
        }
    }

    @Override
    public int getRowNum() {
        return rows.indexOf(this);
    }

    @Override
    public short getLastCellNum() {
        return (short) (cells.size() - 1);
    }

    @NonNull
    @Override
    public CsvCell createCell(int i) {
        CsvCell cell = new CsvCell(null);
        cells.add(i, cell);
        return cell;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Cell> iterator() {
        List<? extends Cell> cells = this.cells;
        return (Iterator<Cell>) cells.iterator();
    }

    @Nullable
    @Override
    public String getStringCellValue(int position) {
        CsvCell cell = getCell(position);
        if (cell == null) return null;
        return cell.getStringValue();
    }

    public boolean isRowEmpty() {
        for (int cellNum = 0; cellNum <= getLastCellNum(); cellNum++) {
            if (!isCellEmpty(cellNum)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCellEmpty(int position) {
        String value = getStringCellValue(position);
        if (value == null) return true;
        return value.isEmpty();
    }
}