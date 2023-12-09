package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Objects;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class ExcelCell implements pl.gocards.filesync.sheet.Cell {

    @NonNull
    private final Cell cell;

    public ExcelCell(@NonNull Cell cell) {
        this.cell = Objects.requireNonNull(cell);
    }

    @NonNull
    @Override
    public String getStringValue() {
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return Double.toString(cell.getNumericCellValue());
        } else if (cell.getCellType().equals(CellType.BOOLEAN)) {
            return Boolean.toString(cell.getBooleanCellValue()).toUpperCase();
        } else {
            return cell.getStringCellValue().trim();
        }
    }

    @NonNull
    @Override
    public Boolean getBooleanValue() {
        if (cell.getCellType().equals(CellType.BOOLEAN)) {
            return cell.getBooleanCellValue();
        } else {
            return Boolean.parseBoolean(cell.getStringCellValue());
        }
    }

    @Override
    public void setCellValue(String value) {
        cell.setCellValue(value);
    }

    @Override
    public void setCellValue(boolean value) {
        cell.setCellValue(value);
    }

    @Override
    public void setHeaderStyle() {
        Font font = cell.getSheet().getWorkbook().createFont();
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    @NonNull
    protected Cell getCell() {
        return cell;
    }
}
