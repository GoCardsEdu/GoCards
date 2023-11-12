package pl.gocards.filesync.algorithms.import_file;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;

/**
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("unused")
public class MockImportFileToDeck extends ImportFileToDeck {

    /**
     * org.mockito.exceptions.base.MockitoException: Unable to initialize @Spy annotated field 'importFileToDeck'.
     * Please ensure that the type 'ImportFileToDeck' has a no-arg constructor.
     *
     * @noinspection DataFlowIssue
     */
    public MockImportFileToDeck() {
        super(null);
    }

    public MockImportFileToDeck(Context context) {
        super(context);
    }

    @NonNull
    public String findFreeDeckName(@NonNull String folderPath, @NonNull String deckName) {
        return super.findFreeDeckName(folderPath, deckName);
    }

    public void removeLastEmptyCards() {
        super.removeLastEmptyCards();
    }

    public void insertAll(@NonNull List<Card> cards) {
        super.insertAll(cards);
    }

    @NotNull
    public DeckDatabase createDeckDatabase(@NonNull String deckDbPath) throws DatabaseException {
        return super.createDeckDatabase(deckDbPath);
    }
}
