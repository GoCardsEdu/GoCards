package pl.gocards.filesync.sheet.csv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class CsvSheet implements Sheet {

    private final List<CsvRow> rows;

    public CsvSheet(List<CsvRow> rows) {
        this.rows = rows;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Iterator<Row> iterator() {
        List<? extends Row> rows = this.rows;
        return (Iterator<Row>) rows.iterator();
    }

    @Override
    public int getLastRowNum() {
        return rows.size() - 1;
    }

    @Override
    public CsvRow getRow(int index) {
        if (index < rows.size()) {
            return rows.get(index);
        } else {
            return null;
        }
    }

    @Override
    public CsvRow createRow(int index) {
        CsvRow csvRow = new CsvRow(rows);
        rows.add(index, csvRow);
        return csvRow;
    }

    @Override
    public void removeRow(Row row) {
        int index = rows.indexOf((CsvRow) row);
        rows.set(index, new CsvRow(rows)); // TODO null?
    }

    @Override
    public void shiftRow(int startRowNum, int numberRowsToShift) {
        shiftAllRows(startRowNum, startRowNum, numberRowsToShift);
    }

    @Override
    public void shiftAllRows(int startRowNum, int lastRowNum, int numberRowsToShift) {
        List<CsvRow> shift = new LinkedList<>(rows.subList(startRowNum, lastRowNum + 1));

        // Clearing shifting rows
        for (int i = startRowNum; i < lastRowNum + 1; i++) {
            rows.set(i, new CsvRow(rows));
        }

        // Overwriting
        int currentNewRowNum = startRowNum + numberRowsToShift;
        for (CsvRow row: shift) {
            if (currentNewRowNum >= rows.size()) {
                rows.add(currentNewRowNum, row);
            } else {
                rows.set(currentNewRowNum, row);
            }
            currentNewRowNum++;
        }
    }

    @Nullable
    @Override
    public String getStringCellValue(@Nullable Row row, int position) {
        if (row == null) return null;
        Cell cell = row.getCell(position);
        if (cell == null) return null;
        return cell.getStringValue();
    }

    @Nullable
    @Override
    public Boolean getBooleanCellValue(@Nullable Row row, int position) {
        if (row == null) return null;
        Cell cell = row.getCell(position);
        if (cell == null) return null;
        return cell.getBooleanValue();
    }

    @Override
    public void setColumnWidth(int i, int width) {}
}