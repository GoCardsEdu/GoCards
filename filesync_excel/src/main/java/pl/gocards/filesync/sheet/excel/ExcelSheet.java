package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class ExcelSheet implements pl.gocards.filesync.sheet.Sheet {
    private final Sheet sheet;

    public ExcelSheet(@NonNull Sheet sheet) {
        this.sheet = sheet;
    }

    @NonNull
    public Iterator<Row> iterator() {
        return new ExcelRowIterator(sheet);
    }

    @Override
    public int getLastRowNum() {
        return sheet.getLastRowNum();
    }

    @Override
    public ExcelRow getRow(int i) {
        if (sheet.getRow(i) == null) return null;
        return new ExcelRow(sheet.getRow(i));
    }

    @Override
    public ExcelRow createRow(int i) {
        return new ExcelRow(sheet.createRow(i));
    }

    @Override
    public void removeRow(Row row) {
        sheet.removeRow(((ExcelRow)row).getRow());
    }

    @Override
    public void shiftRow(int startRowNum, int numberRowsToShift) {
        shiftAllRows(startRowNum, startRowNum, numberRowsToShift);
    }

    @Override
    public void shiftAllRows(int startRowNum, int lastRowNum, int numberRowsToShift) {
        sheet.shiftRows(startRowNum, lastRowNum, numberRowsToShift);
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
    public void setColumnWidth(int i, int width) {
        sheet.setColumnWidth(i, width);
    }
}
