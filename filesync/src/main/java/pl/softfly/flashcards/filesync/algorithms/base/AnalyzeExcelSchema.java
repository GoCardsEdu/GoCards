package pl.softfly.flashcards.filesync.algorithms.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

/**
 * Determines the column indexes for terms and definitions.
 *
 * @author Grzegorz Ziemski
 */
public abstract class AnalyzeExcelSchema {

    private static final String TAG = AnalyzeExcelSchema.class.getSimpleName();

    /**
     * The column index of term.
     */
    private int termIndex = -1;

    /**
     * The column index of definition.
     */
    private int definitionIndex = -1;

    /**
     * The row where cards data begins.
     */
    private int skipEmptyRows = -1;


    // @todo Only for testing
    public AnalyzeExcelSchema() {
    }

    /**
     * Determines the column indexes for terms and definitions.
     */
    protected void findColumnIndexes(@NonNull Sheet datatypeSheet) {
        FindColumnIndexes results = new FindColumnIndexes();
        results.findColumnIndexes(datatypeSheet);
        termIndex = results.getTermIndex();
        definitionIndex = results.getDefinitionIndex();
        skipEmptyRows = results.getSkipEmptyRows();
    }

    protected int findFirstEmptyColumn(@NonNull Sheet datatypeSheet) {
        int emptyColIdx = 0;
        Iterator<Row> rowIt = datatypeSheet.iterator();
        while (rowIt.hasNext()) {
            Row currentRow = rowIt.next();
            emptyColIdx = Math.max(emptyColIdx, currentRow.getLastCellNum());
        }
        return emptyColIdx + 1;
    }

    protected boolean nonEmpty(@Nullable String str) {
        return str != null && !str.isEmpty();
    }

    protected boolean empty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    protected int getTermIndex() {
        return termIndex;
    }

    protected void setTermIndex(int termIndex) {
        this.termIndex = termIndex;
    }

    protected int getDefinitionIndex() {
        return definitionIndex;
    }

    protected void setDefinitionIndex(int definitionIndex) {
        this.definitionIndex = definitionIndex;
    }

    protected int getSkipEmptyRows() {
        return skipEmptyRows;
    }

    protected void setSkipEmptyRows(int skipEmptyRows) {
        this.skipEmptyRows = skipEmptyRows;
    }
}
