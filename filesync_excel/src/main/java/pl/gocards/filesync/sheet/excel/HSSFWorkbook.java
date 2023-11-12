package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class HSSFWorkbook extends XSSFWorkbook  {

    public HSSFWorkbook(@NonNull InputStream inputStream) throws IOException {
        super(new org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream));
    }

    public HSSFWorkbook() {
        super(new org.apache.poi.xssf.usermodel.XSSFWorkbook());
    }

}
