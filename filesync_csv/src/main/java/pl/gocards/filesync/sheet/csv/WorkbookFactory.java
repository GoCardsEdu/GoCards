package pl.gocards.filesync.sheet.csv;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import pl.gocards.filesync.sheet.Workbook;

/**
 * @author Grzegorz Ziemski
 */
public class WorkbookFactory implements pl.gocards.filesync.sheet.WorkbookFactory {

    public Workbook createWorkbook(@NonNull String fileType) {
        return new CsvWorkbook();
    }

    public Workbook createWorkbook(
            @NonNull InputStream inputStream,
            @NonNull String fileMimeType
    ) throws IOException {
        return new CsvWorkbook(inputStream);
    }
}