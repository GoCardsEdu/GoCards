package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class ExcelRow implements pl.gocards.filesync.sheet.Row {

    @NonNull
    private final Row row;

    public ExcelRow(@NonNull Row row) {
        this.row = Objects.requireNonNull(row);
    }

    @Override
    public ExcelCell getCell(int i) {
        if (row.getCell(i) == null) return null;
        return new ExcelCell(row.getCell(i));
    }

    @Override
    public int getRowNum() {
        return row.getRowNum();
    }

    @Override
    public short getLastCellNum() {
        return row.getLastCellNum();
    }

    @NonNull
    @Override
    public ExcelCell createCell(int i) {
        CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
        cellStyle.setWrapText(true);
        Cell cell = row.createCell(i);
        cell.setCellStyle(cellStyle);
        return new ExcelCell(cell);
    }

    @NonNull
    @Override
    public Iterator<pl.gocards.filesync.sheet.Cell> iterator() {
        return new ExcelCellIterator(row);
    }

    @Nullable
    @Override
    public String getStringCellValue(int position) {
        Cell cell = row.getCell(position);
        if (cell == null) return null;
        ExcelCell excelCell = new ExcelCell(cell);
        return excelCell.getStringValue();
    }

    @Override
    public boolean isRowEmpty() {
        for (int cellNum = 0; cellNum <= getLastCellNum(); cellNum++) {
            if (!isCellEmpty(cellNum)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCellEmpty(int position) {
        String value = getStringCellValue(position);
        if (value == null) return true;
        return value.isEmpty();
    }

    @NonNull
    protected Row getRow() {
        return row;
    }
}
