package pl.gocards.filesync.sheet;

import androidx.annotation.Nullable;

/**
 * @author Grzegorz Ziemski
 */
public interface Sheet extends Iterable<Row>  {

    int getLastRowNum();
    @Nullable
    Row getRow(int i);
    Row createRow(int i);
    void removeRow(Row row);
    void shiftRow(int startRowNum, int numberRowsToShift);
    void shiftAllRows(int startRowNum, int lastRowNum, int numberRowsToShift);
    @Nullable
    String getStringCellValue(@Nullable Row row, int position);
    @Nullable
    Boolean getBooleanCellValue(@Nullable Row row, int position);
    void setColumnWidth(int i, int width);
}
