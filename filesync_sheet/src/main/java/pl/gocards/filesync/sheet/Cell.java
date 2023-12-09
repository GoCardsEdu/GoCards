package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Grzegorz Ziemski
 */
public interface Cell {
    @Nullable
    String getStringValue();
    @NonNull
    Boolean getBooleanValue();
    void setCellValue(String value);
    void setCellValue(boolean value);
    void setHeaderStyle();
}