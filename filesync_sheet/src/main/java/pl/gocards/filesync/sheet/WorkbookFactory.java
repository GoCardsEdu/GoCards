package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Grzegorz Ziemski
 */
public interface WorkbookFactory {

    String MIME_TYPE_XLS = "application/vnd.ms-excel";

    @SuppressWarnings("SpellCheckingInspection")
    String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    String MIME_TYPE_CSV = "text/comma-separated-values";

    String[] SUPPORTED_MIME_TYPES = new String[]{MIME_TYPE_XLS, MIME_TYPE_XLSX, MIME_TYPE_CSV};

    String FILE_EXTENSION_XLS = "xls";

    String FILE_EXTENSION_XLSX = "xlsx";

    String FILE_EXTENSION_CSV = "csv";

    String[] SUPPORTED_EXTENSIONS = new String[]{FILE_EXTENSION_XLS, FILE_EXTENSION_XLSX, FILE_EXTENSION_CSV};

    @Nullable
    Workbook createWorkbook(@NonNull String fileMimeType);

    @Nullable
    Workbook createWorkbook(
            @NonNull InputStream inputStream,
            @NonNull String fileMimeType
    ) throws IOException;
}
