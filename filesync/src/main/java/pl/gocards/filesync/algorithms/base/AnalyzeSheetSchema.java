package pl.gocards.filesync.algorithms.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;


/**
 * Determines the column indexes for terms and definitions.
 *
 * @author Grzegorz Ziemski
 */
public abstract class AnalyzeSheetSchema {

    private int termIndex = -1;
    private int definitionIndex = -1;
    private int disabledIndex = -1;

    /**
     * The row where cards data begins.
     */
    private int skipEmptyRows = -1;

    public AnalyzeSheetSchema() {}

    /**
     * Determines the column indexes for terms and definitions.
     */
    protected void findColumnIndexes(@NonNull Sheet sheet) {
        FindColumnIndexes results = new FindColumnIndexes();
        results.findColumnIndexes(sheet);
        setTermIndex(results.getTermIndex());
        setDefinitionIndex(results.getDefinitionIndex());
        setDisabledIndex(results.getDisabledIndex());
        setSkipEmptyRows(results.getSkipEmptyRows());
    }

    protected int findFirstEmptyColumn(@NonNull Sheet sheet) {
        int emptyColIdx = 0;
        for (Row currentRow : sheet) {
            emptyColIdx = Math.max(emptyColIdx, currentRow.getLastCellNum());
        }
        return emptyColIdx + 1;
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

    public int getDisabledIndex() {
        return disabledIndex;
    }

    public void setDisabledIndex(int disabledIndex) {
        this.disabledIndex = disabledIndex;
    }

    protected int getSkipEmptyRows() {
        return skipEmptyRows;
    }

    protected void setSkipEmptyRows(int skipEmptyRows) {
        this.skipEmptyRows = skipEmptyRows;
    }
}
