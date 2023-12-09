package pl.gocards.filesync.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import io.cucumber.datatable.DataTable;
import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;
import pl.gocards.room.entity.deck.Card;

/**
 * @author Grzegorz Ziemski
 */
public class TestCardsHelper {

    public static final int COLUMN_INDEX_TERM = 0;
    public static final int COLUMN_INDEX_DEFINITION = 1;

    public void assertCards(@NonNull List<Card> actualCardList, @NonNull DataTable expectedCards) {
        Iterator<Card> it = actualCardList.iterator();
        int ordinal = 1;
        for (List<String> row : expectedCards.asLists()) {
            if (it.hasNext()) {
                Card card = it.next();
                assertEquals("Wrong ordinal", ordinal++, card.getOrdinal());

                List<String> actual = new ArrayList<>(Arrays.asList(card.getTerm(), card.getDefinition()));
                List<String> expected = new ArrayList<>(Arrays.asList(getExpectedTerm(row), getExpectedDefinition(row)));
                assertThat(actual, is(expected));

                if (row.size() > 2) {
                    String expectedCreatedAt = row.get(2);
                    if (isInteger(expectedCreatedAt)) {
                        assertThat(
                                "Wrong createdAt.",
                                card.getCreatedAt(),
                                is(Long.parseLong(expectedCreatedAt))
                        );
                    }
                }
            } else {
                fail(String.format("Lack of the card term=%s, definition=%s", getExpectedTerm(row), getExpectedDefinition(row)));
            }
        }
    }

    @NonNull
    private String getExpectedTerm(@NonNull List<String> row) {
        String expectedTerm = row.get(0);
        if (expectedTerm == null) return "";
        return expectedTerm;
    }

    @Nullable
    protected String getExpectedDefinition(@NonNull List<String> row) {
        String expectedDefinition;
        if (row.size() > 1) {
            expectedDefinition = row.get(1);
            if (expectedDefinition != null) return expectedDefinition;
        }
        return "";
    }

    protected boolean isInteger(@NonNull String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @NonNull
    public String printDbCards(@NonNull List<Card> cards) {
        if (cards.size() == 0) {
            return "No cards in DB.";
        }

        int maxLengthOrdinal = calcMaxLengthOrdinal(cards);
        int maxLengthTerm = calcMaxLengthTerm(cards);
        int maxLengthDef = calcMaxLengthDef(cards);

        StringBuilder sb = new StringBuilder("\nCards in DB:");
        cards.forEach(card -> sb.append("\n")
                .append("| ")
                .append(String.format("%-" + maxLengthOrdinal + "." + maxLengthOrdinal + "s", card.getOrdinal()))
                .append(" | ")
                .append(String.format("%-" + maxLengthTerm + "." + maxLengthTerm + "s", card.getTerm()))
                .append(" | ")
                .append(String.format("%-" + maxLengthDef + "." + maxLengthDef + "s", card.getDefinition()))
                .append(" |")
        );
        return sb.toString();
    }

    private int calcMaxLengthOrdinal(@NonNull List<Card> cards) {
        return calcMaxLength(cards, card -> IntStream.of(Integer.toString(card.getOrdinal()).length()));
    }

    private int calcMaxLengthTerm(@NonNull List<Card> cards) {
        int maxLength = calcMaxLength(cards, card -> IntStream.of(card.getTerm().length()));
        if (maxLength == 0) return 1;
        return maxLength;
    }

    private int calcMaxLengthDef(@NonNull List<Card> cards) {
        int maxLength = calcMaxLength(cards, card -> IntStream.of(card.getDefinition().length()));
        if (maxLength == 0) return 1;
        return maxLength;
    }

    private int calcMaxLength(@NonNull List<Card> cards, @NonNull Function<Card, IntStream> flatCardToIntStream) {
        return cards.stream()
                .flatMapToInt(flatCardToIntStream)
                .max()
                .orElse(1);
    }

    @NonNull
    public String printSheetCards(@NonNull Sheet sheet) {
        int maxLengthTerm = calcMaxLengthTerm(sheet);
        int maxLengthDefinition = calcMaxLengthDef(sheet);

        StringBuilder sb = new StringBuilder("\nCards in the file:");
        for (Row row : sheet) {
            sb.append("\n")
                    .append("| ")
                    .append(String.format("%-" + maxLengthTerm + "." + maxLengthTerm + "s", getTerm(row)))
                    .append(" | ")
                    .append(String.format("%-" + maxLengthDefinition + "." + maxLengthDefinition + "s", getDefinition(row)
                    ))
                    .append(" |");
        }

        return sb.toString();
    }

    private int calcMaxLengthTerm(@NonNull Sheet sheet) {
        int maxLengthTerm = 1;
        for (Row row : sheet) {
            maxLengthTerm = Math.max(maxLengthTerm, getTermLength(row));
        }
        return maxLengthTerm;
    }

    private int getTermLength(@NonNull Row row) {
        String term = getTerm(row);
        if (term == null) return 0;
        return term.length();
    }

    private int calcMaxLengthDef(@NonNull Sheet sheet) {
        int maxLengthDefinition = 1;
        for (Row row : sheet) {
            maxLengthDefinition = Math.max(maxLengthDefinition, getDefinitionLength(row));
        }
        return maxLengthDefinition;
    }

    private int getDefinitionLength(@NonNull Row row) {
        String term = getDefinition(row);
        if (term == null) return 0;
        return term.length();
    }

    @Nullable
    private String getTerm(@NonNull Row row) {
        return row.getStringCellValue(COLUMN_INDEX_TERM);
    }

    @Nullable
    private String getDefinition(@NonNull Row row) {
        return row.getStringCellValue(COLUMN_INDEX_DEFINITION);
    }
}
