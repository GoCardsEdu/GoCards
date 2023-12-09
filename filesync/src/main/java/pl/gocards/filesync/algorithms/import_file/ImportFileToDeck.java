package pl.gocards.filesync.algorithms.import_file;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.filesync.algorithms.base.AnalyzeSheetSchema;
import pl.gocards.filesync.algorithms.base.OpenFile;
import pl.gocards.filesync.exception.DisabledWrongTypeWarningException;
import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;
import pl.gocards.filesync.sheet.Workbook;
import pl.gocards.filesync.sheet.BigWorkbookFactory;
import pl.gocards.filesync.sheet.WorkbookFactory;

/**
 * Creates a new deck with cards from a file.
 *
 * @author Grzegorz Ziemski
 */
public class ImportFileToDeck extends AnalyzeSheetSchema {

    public static final int ENTITIES_TO_UPDATE_POOL_MAX = 100;
    @NonNull
    private final Context context;
    @Nullable
    private DeckDatabase deckDb;

    @NonNull
    private final WorkbookFactory workbookFactory = new BigWorkbookFactory();

    public ImportFileToDeck(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public String importFile(
            @NonNull String importToFolderPath,
            @NonNull String deckName,
            @NonNull InputStream inputStream,
            @NonNull String fileMimeType,
            @NonNull Long lastModifiedAtFile
    ) throws IOException, DatabaseException {
        String deckDbPath = findFreeDeckName(importToFolderPath, deckName);
        Workbook workbook = workbookFactory.createWorkbook(inputStream, fileMimeType);
        Objects.requireNonNull(workbook);
        Sheet sheet = workbook.getSheetAt(0);
        Objects.requireNonNull(sheet);
        findColumnIndexes(sheet);
        importCardsToFile(
                sheet,
                deckDbPath,
                getTermIndex(),
                getDefinitionIndex(),
                getDisabledIndex(),
                getSkipEmptyRows(),
                lastModifiedAtFile
        );
        removeLastEmptyCards();
        return deckDbPath;
    }

    protected void importCardsToFile(
            @NonNull Sheet sheet,
            @NonNull String deckDbPath,
            int termPosition,
            int definitionPosition,
            int disabledPosition,
            int skipFirstRows,
            Long lastModifiedAtFile
    ) throws DatabaseException {
        if (termPosition == -1 && definitionPosition == -1) return;

        List<Card> cardsList = new LinkedList<>();
        int ordinal = OpenFile.MULTIPLE_ORDINAL;

        int rowEnd = sheet.getLastRowNum();
        for (int rowNum = skipFirstRows; rowNum <= rowEnd; rowNum++) {
            Row currentRow = sheet.getRow(rowNum);
            Card card = new Card();

            if (currentRow != null) {
                if (termPosition > -1) {
                    Card.Companion.setTerm(card, sheet.getStringCellValue(currentRow, termPosition));
                }
                if (definitionPosition > -1) {
                    Card.Companion.setDefinition(card, sheet.getStringCellValue(currentRow, definitionPosition));
                }
                Card.Companion.setHtmlFlags(card);
                setDisabled(card, sheet, currentRow, disabledPosition);
            }

            card.setOrdinal(ordinal);
            ordinal += OpenFile.MULTIPLE_ORDINAL;
            card.setCreatedAt(lastModifiedAtFile);
            card.setUpdatedAt(lastModifiedAtFile);

            if (deckDb == null) deckDb = createDeckDatabase(deckDbPath);
            cardsList.add(card);
            if (rowNum > 0 && (rowNum % ENTITIES_TO_UPDATE_POOL_MAX == 0)) {
                insertAll(new ArrayList<>(cardsList));
                cardsList.clear();
            }
        }

        if (!cardsList.isEmpty()) {
            insertAll(cardsList);
        }
    }

    protected void setDisabled(
            @NonNull Card card,
            @NonNull Sheet sheet,
            @NonNull Row currentRow,
            int disabledPosition
    ) {
        if (disabledPosition > -1) {
            try {
                Boolean disabled = sheet.getBooleanCellValue(currentRow, disabledPosition);
                if (disabled != null) {
                    card.setDisabled(disabled);
                }
            } catch (IllegalStateException e) {
                throw new DisabledWrongTypeWarningException(context, currentRow.getRowNum(), e);
            }
        }
    }

    protected void removeLastEmptyCards() {
        Objects.requireNonNull(deckDb);

        int maxOrdinal = deckDb.cardDao().maxOrdinal();
        for (int i = maxOrdinal; i > 0; i--) {
            Card card = deckDb.cardDao().findByOrdinal(i);
            if (empty(card.getTerm()) && empty(card.getDefinition())) {
                Objects.requireNonNull(card.getId());
                deckDb.cardDao().forceDeleteById(card.getId());
            } else {
                return;
            }
        }
    }

    @NonNull
    protected AppDeckDbUtil getDeckDatabaseUtil() {
        return AppDeckDbUtil.getInstance(context);
    }

    @NonNull
    protected String findFreeDeckName(@NonNull String folderPath, @NonNull String deckName) {
        return getDeckDatabaseUtil().findFreePath(folderPath, deckName);
    }

    protected void insertAll(@NonNull List<Card> cards) {
        Objects.requireNonNull(deckDb).cardDao().insertAll(cards);
    }

    @NonNull
    protected DeckDatabase createDeckDatabase(@NonNull String deckDbPath) throws DatabaseException {
        return getDeckDatabaseUtil().createDatabase(context, deckDbPath);
    }
}
