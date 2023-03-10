package pl.softfly.flashcards.filesync.algorithms.importt;

import static pl.softfly.flashcards.filesync.FileSync.TYPE_XLS;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pl.softfly.flashcards.CardUtil;
import pl.softfly.flashcards.db.AppDatabaseUtil;
import pl.softfly.flashcards.db.DeckDatabaseUtil;
import pl.softfly.flashcards.db.room.AppDatabase;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.filesync.algorithms.base.OpenExcel;

/**
 * Creates a new deck with cards from a file.
 *
 * @author Grzegorz Ziemski
 */
public class ImportExcelToDeck extends OpenExcel {

    public static final int ENTITIES_TO_UPDATE_POOL_MAX = 100;
    private final CardUtil cardUtil = CardUtil.getInstance();
    private Context appContext;
    @Nullable
    private DeckDatabase deckDb;

    // TODO only for testing
    public ImportExcelToDeck() {
        super();
    }

    public ImportExcelToDeck(Context appContext) {
        super(appContext);
        this.appContext = appContext;
    }

    public String importExcelFile(
            String importToFolderPath,
            String deckName,
            @NonNull InputStream inputStream,
            @NonNull String typeFile,
            Long lastModifiedAtFile
    ) throws IOException, DatabaseException {
        String deckDbPath = findFreeDeckName(importToFolderPath, deckName);
        Workbook workbook = typeFile.equals(TYPE_XLS)
                ? new HSSFWorkbook(inputStream)
                : new XSSFWorkbook(inputStream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        findColumnIndexes(datatypeSheet);
        importCardsToFile(
                datatypeSheet,
                deckDbPath,
                getTermIndex(),
                getDefinitionIndex(),
                getSkipEmptyRows(),
                lastModifiedAtFile
        );
       refreshLastUpdatedAt(deckDbPath);
        if (deckDb != null) {
            return deckDbPath;
        } else {
            return null;
        }
    }

    protected void importCardsToFile(
            @NonNull Sheet datatypeSheet,
            @NonNull String deckDbPath,
            int termPosition,
            int definitionPosition,
            int skipFirstRows,
            Long lastModifiedAtFile
    ) throws DatabaseException {
        if (termPosition == -1 && definitionPosition == -1) return;

        Iterator<Row> rowIt = datatypeSheet.iterator();
        List<Card> cardsList = new LinkedList<>();
        int ordinal = OpenExcel.MULTIPLE_ORDINAL;
        for (int rowNum = 0; rowIt.hasNext(); rowNum++) {
            Row currentRow = rowIt.next();
            if (rowNum <= skipFirstRows) {
                continue;
            }
            Card card = new Card();

            if (termPosition > -1) {
                Cell currentCell = currentRow.getCell(termPosition);
                if (currentCell != null) {
                    String value = currentCell.getStringCellValue().trim();
                    cardUtil.setTerm(card, value);
                }
            }
            if (definitionPosition > -1) {
                Cell currentCell = currentRow.getCell(definitionPosition);
                if (currentCell != null) {
                    cardUtil.setDefinition(card, getStringCellValue(currentCell));
                }
            }

            if (nonEmpty(card.getTerm()) || nonEmpty(card.getDefinition())) {
                if (empty(card.getTerm())) {
                    card.setTerm("");//NULL generates problem with WHERE sql query
                }
                if (empty(card.getDefinition())) {
                    card.setDefinition("");//NULL generates problem with WHERE sql query
                }
                card.setOrdinal(ordinal);
                ordinal += OpenExcel.MULTIPLE_ORDINAL;
                card.setCreatedAt(lastModifiedAtFile);
                card.setModifiedAt(lastModifiedAtFile);

                if (deckDb == null) deckDb = getDeckDatabase(deckDbPath);
                cardsList.add(card);
                if (rowNum > 0 && (rowNum % ENTITIES_TO_UPDATE_POOL_MAX == 0)) {
                    insertAll(new ArrayList<>(cardsList));
                    cardsList.clear();
                }
            }
        }

        if (!cardsList.isEmpty()) {
            insertAll(cardsList);
        }
    }

    protected String getStringCellValue(Cell cell) {
        if (cell.getCellType().equals(CellType.NUMERIC)) {
            return Double.toString(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue().trim();
        }
    }

    //@todo Public for mocking
    //@todo try DI instead of ServiceLocator
    public String findFreeDeckName(String folderPath, @NonNull String deckName) {
        return DeckDatabaseUtil
                .getInstance(appContext)
                .getStorageDb()
                .findFreePath(folderPath + "/" + deckName.substring(0, deckName.lastIndexOf('.')));
    }

    public void refreshLastUpdatedAt(String deckDbPath) {
        getAppDatabase().deckDao().refreshLastUpdatedAt(deckDbPath);
    }

    //@todo Public for mocking
    public void insertAll(List<Card> cards) {
        deckDb.cardDao().insertAll(cards);
    }

    //@todo Public for mocking
    @Nullable
    public DeckDatabase getDeckDatabase(@NonNull String deckDbPath) throws DatabaseException {
        return DeckDatabaseUtil.getInstance(appContext).createDatabase(deckDbPath);
    }

    protected AppDatabase getAppDatabase() {
        return AppDatabaseUtil
                .getInstance(appContext)
                .getDatabase();
    }
}
