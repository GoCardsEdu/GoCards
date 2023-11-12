package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;

import org.apache.poi.ss.util.WorkbookUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pl.gocards.filesync.sheet.Workbook;

/**
 * @author Grzegorz Ziemski
 */
public class XSSFWorkbook implements Workbook {

    private final org.apache.poi.ss.usermodel.Workbook workbook;

    protected XSSFWorkbook(@NonNull org.apache.poi.ss.usermodel.Workbook workbook) {
        this.workbook = workbook;
    }

    public XSSFWorkbook(@NonNull InputStream inputStream) throws IOException {
        this(new org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream));
    }
    public XSSFWorkbook() {
        this(new org.apache.poi.xssf.usermodel.XSSFWorkbook());
    }

    @Override
    public ExcelSheet getSheetAt(int var1) {
        return new ExcelSheet(workbook.getSheetAt(var1));
    }

    public ExcelSheet createSheet(String s) {
        return new ExcelSheet(workbook.createSheet(WorkbookUtil.createSafeSheetName(s)));
    }

    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }

    public void close() throws IOException {
        workbook.close();
    }
}
