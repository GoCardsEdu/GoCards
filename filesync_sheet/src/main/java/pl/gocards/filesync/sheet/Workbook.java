package pl.gocards.filesync.sheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Grzegorz Ziemski
 */
public interface Workbook {
    @Nullable
    Sheet getSheetAt(int i);
    @NonNull
    Sheet createSheet(String s);
    void write(OutputStream outputStream) throws IOException;
    void close() throws IOException;
}
