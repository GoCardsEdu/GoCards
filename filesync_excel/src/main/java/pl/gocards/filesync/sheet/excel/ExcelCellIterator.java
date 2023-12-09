package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import pl.gocards.filesync.sheet.Cell;

/**
 * @author Grzegorz Ziemski
 */
public class ExcelCellIterator implements Iterator<Cell>  {

    @NonNull
    private final Row row;

    int index = 0;

    public ExcelCellIterator(@NonNull Row row) {
        this.row = row;
    }

    @Override
    public boolean hasNext() {
        return index <= row.getLastCellNum();
    }

    @Nullable
    @Override
    public ExcelCell next() {
        if (row.getCell(index) == null) {
            index++;
            return null;
        }
        return new ExcelCell(row.getCell(index++));
    }
}
