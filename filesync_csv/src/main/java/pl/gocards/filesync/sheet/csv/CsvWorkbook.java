package pl.gocards.filesync.sheet.csv;

import android.util.Log;

import androidx.annotation.NonNull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Workbook;

/**
 * @author Grzegorz Ziemski
 */
public class CsvWorkbook implements Workbook {

    private static final String TAG = "CsvWorkbook";

    private final List<CsvRow> rows = new LinkedList<>();

    private CSVParser parser;

    private CSVPrinter printer;

    private final CsvSheet sheet = new CsvSheet(rows);

    private final CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setIgnoreEmptyLines(false).build();

    public CsvWorkbook() {}

    public CsvWorkbook(@NonNull InputStream inputStream) throws IOException {
        this();
        final Reader reader = new InputStreamReader(inputStream);
        parser = new CSVParser(reader, csvFormat);

        for (CSVRecord record : parser.getRecords()) {

            List<CsvCell> cells = new LinkedList<>();
            for (String cell: record.toList()) {
                cells.add(new CsvCell(cell));
            }

            rows.add(new CsvRow(rows, cells));
        }
    }

    @Override
    public CsvSheet getSheetAt(int index) {
        return sheet;
    }

    public CsvSheet createSheet(String s) {
        return sheet;
    }

    public void write(OutputStream outputStream) throws IOException {
        printer = new CSVPrinter(new BufferedWriter(new OutputStreamWriter(outputStream)), csvFormat);

        removeLastEmptyRows();

        for (CsvRow row: rows) {
            List<String> cells = new LinkedList<>();
            for (Cell cell: row) {
                cells.add(cell.getStringValue());
            }
            printer.printRecord(cells);
            printer.flush();
        }
    }

    /**
     * FS_PRO_S.16.3 Remove empty lines at the end of the file.
     */
    protected void removeLastEmptyRows() {
        int lastRowNum = sheet.getLastRowNum();
        Log.i(TAG, String.format("RemoveLastEmptyRows [lastRowNum=%d]", lastRowNum));

        for (int rowNum = lastRowNum; rowNum >= 0; rowNum--) {
            CsvRow row = rows.get(rowNum);
            if (row == null || row.isRowEmpty()) {
                rows.remove(rowNum);
            } else {
                return;
            }
            Log.i(TAG, String.format("rowNum=%d Delete the empty row", rowNum));
        }
    }

    public void close() throws IOException {
        if (parser != null) parser.close();
        if (printer != null) printer.close();
    }
}