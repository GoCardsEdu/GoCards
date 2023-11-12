package pl.gocards.filesync.algorithms.base;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.Locale;

import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;


/**
 * Determines the column indexes for terms and definitions.
 *
 * @author Grzegorz Ziemski
 */
public class FindColumnIndexes {

    private static final String TAG = FindColumnIndexes.class.getSimpleName();

    private int termIndex = -1;

    private int definitionIndex = -1;

    private int disabledIndex = -1;

    /**
     * The row where cards data begins.
     */
    private int skipEmptyRows = -1;

    private int headerRow = -1;

    private int rowNum;
    private int colNum;
    private String cellValue;

    /**
     * Search for the header only in the first non-empty string
     */
    private boolean hasBlankRowsBefore = true;

    private int firstNonEmptyColIdx = -1;
    private int secondNonEmptyColIdx = -1;

    /**
     * Determines the column indexes for terms and definitions.
     */
    protected void findColumnIndexes(@NonNull Sheet sheet) {
        Iterator<Row> rowIt = sheet.iterator();

        termIndex = -1;
        definitionIndex = -1;
        skipEmptyRows = -1;
        hasBlankRowsBefore = true;
        firstNonEmptyColIdx = -1;
        secondNonEmptyColIdx = -1;

        ROWS:
        for (rowNum = 0; rowIt.hasNext(); rowNum++) {
            Row currentRow = rowIt.next();
            Iterator<Cell> cellIt = currentRow.iterator();
            if (hasBlankRowsBefore) {
                hasBlankRowsBefore = !termHeaderFound() && !defHeaderFound() && !isFound(firstNonEmptyColIdx);
                if (hasBlankRowsBefore) {
                    skipEmptyRows = rowNum - 1;
                }
            }

            for (colNum = 0; cellIt.hasNext(); colNum++) {
                cellValue = getStringValue(cellIt.next());
                if (nonEmpty(cellValue) && processCell()) {
                    break ROWS;
                }
            }
        }

        if (!termHeaderFound() && defHeaderFound() && isFound(firstNonEmptyColIdx)) {
            termIndex = firstNonEmptyColIdx;
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_09 Found 2 columns with only the Definition header.");

        } else if (!termHeaderFound() && !defHeaderFound() && isFound(firstNonEmptyColIdx) && isFound(secondNonEmptyColIdx)) {
            termIndex = firstNonEmptyColIdx;
            definitionIndex = secondNonEmptyColIdx;
            Log.i(TAG, "IE_06 2 columns, no header columns.");

        } else if (!termHeaderFound() && !defHeaderFound() && isFound(firstNonEmptyColIdx)) {
            termIndex = firstNonEmptyColIdx;
            Log.i(TAG, "IE_01 Found 1 column, no header columns.");

        } else if (termHeaderFound() && !defHeaderFound() && isFound(firstNonEmptyColIdx)) {
            definitionIndex = firstNonEmptyColIdx;
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_04 Found 2 columns with only the Term header.");

        } else if (termHeaderFound() && defHeaderFound()) {
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_01 Found 2 header columns: Term and Definition");

        } else if (termHeaderFound() && !defHeaderFound() && !isFound(firstNonEmptyColIdx) && !isFound(secondNonEmptyColIdx)) {
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_02 Found 1 column with only the Term header.");

        } else if (!termHeaderFound() && defHeaderFound() && !isFound(firstNonEmptyColIdx) && !isFound(secondNonEmptyColIdx)) {
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_03 Found 1 column with only the Definition header.");

        }

        Log.i(TAG, String.format("skipEmptyRows=%d", skipEmptyRows));
    }

    protected boolean processCell() {
        if (!checkIsTermHeader() && !checkIsDefinitionHeader()) checkIsDisabledHeader();
        if (termHeaderFound() && defHeaderFound() && disabledHeaderFound()) {
            skipEmptyRows = headerRow + 1;
            Log.i(TAG, "IE_10 Found 3 header columns: Term, Definition, Disabled");
            return true;
        }
        if (!checkIsFirstNonEmptyCol()) checkIsSecondNonEmptyCol();
        return finishTwoFirstColNoEmpty();
    }

    /**
     * Finish when the first two columns in the row are not empty.
     */
    protected boolean finishTwoFirstColNoEmpty() {
        return firstNonEmptyColIdx == 0 && secondNonEmptyColIdx == 1
                || secondNonEmptyColIdx == 0 && firstNonEmptyColIdx == 1;
    }

    protected boolean checkIsTermHeader() {
        if (hasBlankRowsBefore && cellValue.toLowerCase(Locale.getDefault()).startsWith("term")) {
            termIndex = colNum;
            headerRow = rowNum;
            Log.i(TAG, "The term header column found=" + this.termIndex);
            return true;
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean checkIsDefinitionHeader() {
        if (hasBlankRowsBefore && cellValue.toLowerCase(Locale.getDefault()).startsWith("definition")) {
            definitionIndex = colNum;
            headerRow = rowNum;
            Log.i(TAG, "The definition header column found=" + this.definitionIndex);
            return true;
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean checkIsDisabledHeader() {
        if (hasBlankRowsBefore && cellValue.toLowerCase(Locale.getDefault()).startsWith("disabled")) {
            disabledIndex = colNum;
            headerRow = rowNum;
            Log.i(TAG, "The disabled header column found=" + this.disabledIndex);
            return true;
        }
        return false;
    }

    protected boolean checkIsFirstNonEmptyCol() {
        if (isFirstNonEmptyCol(colNum, firstNonEmptyColIdx)) {
            if (firstNonEmptyColIdx != -1) {
                secondNonEmptyColIdx = firstNonEmptyColIdx;
                Log.i(TAG, "Second non-empty col=" + secondNonEmptyColIdx);
            }
            firstNonEmptyColIdx = colNum;
            skipEmptyRows = rowNum;
            Log.i(TAG, "First non-empty col=" + firstNonEmptyColIdx);
            return true;
        }
        return false;
    }

    protected boolean isFirstNonEmptyCol(int colNum, int firstNonEmptyColIdx) {
        return isNotTermHeaderCol(colNum)
                && isNotDefHeaderCol(colNum)
                && isLessThanCol(firstNonEmptyColIdx, colNum);
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean checkIsSecondNonEmptyCol() {
        if (isSecondNonEmptyCol(colNum, firstNonEmptyColIdx, secondNonEmptyColIdx)) {
            secondNonEmptyColIdx = colNum;
            Log.i(TAG, "Second non-empty col=" + secondNonEmptyColIdx);
            return true;
        }
        return false;
    }

    protected boolean isSecondNonEmptyCol(
            int colNum,
            int firstNonEmptyColIdx,
            int secondNonEmptyColIdx
    ) {
        return isNotTermHeaderCol(colNum)
                && isNotDefHeaderCol(colNum)
                && isLessThanCol(secondNonEmptyColIdx, colNum)
                && firstNonEmptyColIdx != colNum;
    }

    protected boolean isFound(int col) {
        return col != -1;
    }

    protected boolean isLessThanCol(int nonEmptyColIdx, int colNum) {
        return nonEmptyColIdx == -1 || nonEmptyColIdx > colNum;
    }

    protected String getStringValue(Cell cell) {
        if (cell == null) return null;
        return cell.getStringValue().trim();
    }

    protected boolean isNotTermHeaderCol(int colNum) {
        return termIndex != colNum;
    }

    protected boolean isNotDefHeaderCol(int colNum) {
        return definitionIndex != colNum;
    }

    protected boolean termHeaderFound() {
        return termIndex != -1;
    }

    protected boolean defHeaderFound() {
        return definitionIndex != -1;
    }

    protected boolean disabledHeaderFound() {
        return disabledIndex != -1;
    }

    protected boolean nonEmpty(@Nullable String str) {
        return str != null && !str.isEmpty();
    }

    public int getTermIndex() {
        return termIndex;
    }

    public int getDefinitionIndex() {
        return definitionIndex;
    }

    public int getDisabledIndex() {
        return disabledIndex;
    }

    public int getSkipEmptyRows() {
        return skipEmptyRows;
    }
}