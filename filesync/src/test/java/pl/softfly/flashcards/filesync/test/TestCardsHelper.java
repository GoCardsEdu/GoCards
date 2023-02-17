package pl.softfly.flashcards.filesync.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import io.cucumber.datatable.DataTable;
import pl.softfly.flashcards.entity.deck.Card;

/**
 * @author Grzegorz Ziemski
 */
public class TestCardsHelper {

    public void assertCards(List<Card> actualCardList, DataTable expectedCards) {
        Iterator<Card> it = actualCardList.iterator();
        int ordinal = 1;
        for (List<String> row : expectedCards.asLists()) {
            Card actualCard = it.next();
            assertEquals("Incorrect card order.", ordinal++, actualCard.getOrdinal().intValue());

            String expectedTerm = row.get(0);
            if (expectedTerm == null) expectedTerm = "";

            String expectedDefinition = row.get(1);
            if (expectedDefinition == null) expectedDefinition = "";

            List<String> actual = new ArrayList<>(Arrays.asList(actualCard.getTerm(), actualCard.getDefinition()));
            List<String> expected = new ArrayList<>(Arrays.asList(expectedTerm, expectedDefinition));
            if (row.size() > 2) {
                String expectedCreatedAt = row.get(2);
                actual.add(actualCard.getCreatedAt().toString());
                expected.add(expectedCreatedAt);
            }
            assertThat(actual, is(expected));
        }
    }

    @NonNull
    public String printDbCards(@NonNull List<Card> cards) {
        if (cards.size() == 0) {
            return "No cards in DB.";
        }

        int maxLengthTerm = calcMaxLengthTerm(cards);
        int maxLengthDef = calcMaxLengthDef(cards);

        StringBuilder sb = new StringBuilder("\nCards in DB:");
        cards.forEach(card -> sb.append("\n")
                .append("| ")
                .append(String.format("%-" + maxLengthTerm + "." + maxLengthTerm + "s", card.getTerm()))
                .append(" | ")
                .append(String.format("%-" + maxLengthDef + "." + maxLengthDef + "s", card.getDefinition()))
                .append(" |")
        );
        return sb.toString();
    }

    private int calcMaxLengthTerm(@NonNull List<Card> cards) {
        int maxLengthTerm = cards.stream()
                .flatMapToInt(card -> IntStream.of(card.getTerm().length()))
                .max()
                .getAsInt();
        if (maxLengthTerm == 0) return 1;
        return maxLengthTerm;
    }

    private int calcMaxLengthDef(@NonNull List<Card> cards) {
        int maxLengthTerm = cards.stream()
                .flatMapToInt(card -> IntStream.of(card.getDefinition().length()))
                .max()
                .getAsInt();
        if (maxLengthTerm == 0) return 1;
        return maxLengthTerm;
    }
}
