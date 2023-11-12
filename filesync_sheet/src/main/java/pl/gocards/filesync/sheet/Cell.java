package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;

/**
 * @author Grzegorz Ziemski
 */
public interface Cell {
    @NonNull
    String getStringValue();
    @NonNull
    Boolean getBooleanValue();
    void setCellValue(String value);
    void setCellValue(boolean value);
    void setHeaderStyle();
}