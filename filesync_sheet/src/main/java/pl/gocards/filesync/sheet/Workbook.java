package pl.gocards.filesync.sheet;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Grzegorz Ziemski
 */
public interface Workbook {
    Sheet getSheetAt(int i);
    Sheet createSheet(String s);
    void write(OutputStream outputStream) throws IOException;
    void close() throws IOException;
}
