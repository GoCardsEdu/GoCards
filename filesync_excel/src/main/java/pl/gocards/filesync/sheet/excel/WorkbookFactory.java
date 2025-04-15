package pl.gocards.filesync.sheet.excel;

import androidx.annotation.NonNull;

import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.IOException;
import java.io.InputStream;

import pl.gocards.filesync.sheet.Workbook;

/**
 * @author Grzegorz Ziemski
 */
public class WorkbookFactory implements pl.gocards.filesync.sheet.WorkbookFactory {

    @NonNull
    public Workbook createWorkbook(@NonNull String fileType) {
        return fileType.equals(MIME_TYPE_XLS)
                ? new HSSFWorkbook()
                : new XSSFWorkbook();
    }

    @NonNull
    public Workbook createWorkbook(
            @NonNull InputStream inputStream,
            @NonNull String fileMimeType
    ) throws IOException {
        //ZipSecureFile.setMinInflateRatio(0.001);
        return fileMimeType.equals(MIME_TYPE_XLS)
                ? new HSSFWorkbook(inputStream)
                : new XSSFWorkbook(inputStream);
    }


}
