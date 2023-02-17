package pl.softfly.flashcards.filesync.algorithms.export;

import static pl.softfly.flashcards.filesync.FileSync.TYPE_XLS;

import android.content.Context;

import androidx.annotation.NonNull;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.filesync.algorithms.base.OpenExcel;

/**
 * @author Grzegorz Ziemski
 */
public class ExportExcelToDeck extends OpenExcel {

    public ExportExcelToDeck(Context appContext) {
        super(appContext);
    }

    public void export(
            @NonNull String deckDbPath,
            @NonNull OutputStream os,
            @NonNull String typeFile
    ) throws IOException {
        this.workbook = TYPE_XLS.equals(typeFile)
                ? new HSSFWorkbook()
                : new XSSFWorkbook();
        this.sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(getDeckName(deckDbPath)));

        setTermIndex(0);
        setDefinitionIndex(1);
        setSkipEmptyRows(0);

        createHeader();
        createCards(os);
    }

    public void createCards(@NonNull OutputStream os) throws IOException {
        List<Card> cards = fsDeckDb.cardDao().getCardsOrderByOrdinalAsc();

        for (int rowNum = 1; !cards.isEmpty(); rowNum++) {
            Card card = cards.remove(0);
            createExcelCell(rowNum, card);

            if (cards.isEmpty()) {
                cards = fsDeckDb.cardDao().getCardsOrderByOrdinalAsc(card.getOrdinal());
            }
        }
        this.workbook.write(os);
        this.workbook.close();
        os.close();
    }

    protected void createExcelCell(@NonNull int rowNum, @NonNull Card card) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(getTermIndex());
        cell.setCellValue(card.getTerm());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(getDefinitionIndex());
        cell.setCellValue(card.getDefinition());
        cell.setCellStyle(cellStyle);
    }

    protected void createHeader() {
        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 10000);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFont(font);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(getTermIndex());
        cell.setCellValue("Term");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(getDefinitionIndex());
        cell.setCellValue("Definition");
        cell.setCellStyle(cellStyle);
    }
}