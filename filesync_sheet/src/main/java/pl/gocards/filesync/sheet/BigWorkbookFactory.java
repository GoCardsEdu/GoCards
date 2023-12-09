package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Grzegorz Ziemski
 */
public class BigWorkbookFactory implements WorkbookFactory {

    @Nullable
    private final WorkbookFactory excelWorkbookFactory = getExcelWorkbookFactory();

    @Nullable
    private final WorkbookFactory csvWorkbookFactory = getCsvWorkbookFactory();

    @Nullable
    public Workbook createWorkbook(@NonNull String fileMimeType) {
        switch (fileMimeType) {
            case MIME_TYPE_XLS:
            case MIME_TYPE_XLSX:
                if (excelWorkbookFactory == null) return null;
                return excelWorkbookFactory.createWorkbook(fileMimeType);
            case MIME_TYPE_CSV:
                if (csvWorkbookFactory == null) return null;
                return csvWorkbookFactory.createWorkbook(fileMimeType);
        }
        throw new RuntimeException("No support for " + fileMimeType);
    }

    @Nullable
    public Workbook createWorkbook(
            @NonNull InputStream inputStream,
            @NonNull String fileMimeType
    ) throws IOException {
        switch (fileMimeType) {
            case MIME_TYPE_XLS:
            case MIME_TYPE_XLSX:
                if (excelWorkbookFactory == null) return null;
                return excelWorkbookFactory.createWorkbook(inputStream, fileMimeType);
            case MIME_TYPE_CSV:
                if (csvWorkbookFactory == null) return null;
                return csvWorkbookFactory.createWorkbook(inputStream, fileMimeType);
        }
        throw new RuntimeException("No support for " + fileMimeType);
    }

    @NonNull
    public static String getFileExtension(@NonNull String fileMimeType) throws SheetWarningException {
        switch (fileMimeType) {
            case MIME_TYPE_XLS:
                return FILE_EXTENSION_XLS;
            case MIME_TYPE_XLSX:
                return FILE_EXTENSION_XLSX;
            case MIME_TYPE_CSV:
                return FILE_EXTENSION_CSV;
        }
        throw new SheetWarningException("No support for mime type: \"" + fileMimeType + "\"");
    }

    @Nullable
    public String getDotFileExtension(@NonNull String fileMimeType) {
        try {
            return "." + getFileExtension(fileMimeType);
        } catch (SheetWarningException e) {
            return null;
        }
    }

    @NonNull
    public static String getMimeType(@NonNull String extension) throws SheetWarningException {
        switch (extension) {
            case FILE_EXTENSION_XLS:
                return MIME_TYPE_XLS;
            case FILE_EXTENSION_XLSX:
                return MIME_TYPE_XLSX;
            case FILE_EXTENSION_CSV:
                return MIME_TYPE_CSV;
        }
        throw new SheetWarningException("No support for extension: \"" + extension + "\"");
    }

    @Nullable
    private WorkbookFactory getExcelWorkbookFactory() {
        try {
            return (WorkbookFactory) Class.forName("pl.gocards.filesync.sheet.excel.WorkbookFactory")
                    .newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private WorkbookFactory getCsvWorkbookFactory() {
        try {
            return (WorkbookFactory) Class.forName("pl.gocards.filesync.sheet.csv.WorkbookFactory")
                    .newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}