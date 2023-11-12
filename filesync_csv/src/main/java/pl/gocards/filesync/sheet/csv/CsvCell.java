package pl.gocards.filesync.sheet.csv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import pl.gocards.filesync.sheet.Cell;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class CsvCell implements Cell {

    private String value;

    public CsvCell(@Nullable String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String getStringValue() {
        return value;
    }

    @NonNull
    @Override
    public Boolean getBooleanValue() {
        return Boolean.parseBoolean(value);
    }

    @Override
    public void setCellValue(String value) {
        this.value = value;
    }

    @Override
    public void setCellValue(boolean value) {
        this.value = Boolean.toString(value);
    }

    @Override
    public void setHeaderStyle() {
    }
}