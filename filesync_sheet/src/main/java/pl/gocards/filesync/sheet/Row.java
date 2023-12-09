package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Grzegorz Ziemski
 */
public interface Row extends Iterable<Cell> {
    @NonNull
    Cell createCell(int position);
    @Nullable
    Cell getCell(int position);
    int getRowNum();
    short getLastCellNum();
    @Nullable
    String getStringCellValue(int position);
    boolean isRowEmpty();
    /** @noinspection unused*/
    boolean isCellEmpty(int position);
}
