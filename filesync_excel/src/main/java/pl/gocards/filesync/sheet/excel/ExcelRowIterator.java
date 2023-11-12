package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

import pl.gocards.filesync.sheet.Row;

/**
 * @author Grzegorz Ziemski
 */
public class ExcelRowIterator implements Iterator<Row> {

    private final Sheet sheet;

    int index = 0;

    public ExcelRowIterator(@NonNull Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public boolean hasNext() {
        return index <= sheet.getLastRowNum();
    }

    @Override
    public ExcelRow next() {
        if (sheet.getRow(index) == null) {
            index++;
            return null;
        }
        return new ExcelRow(sheet.getRow(index++));
    }
}