package pl.softfly.flashcards.db.room;

import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.entity.deck.CardFts;
import pl.softfly.flashcards.entity.deck.CardLearningProgress;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.entity.filesync.CardEdge;
import pl.softfly.flashcards.entity.filesync.CardImported;
import pl.softfly.flashcards.entity.filesync.CardImportedRemoved;
import pl.softfly.flashcards.entity.filesync.FileSynced;

/**
 * @author Grzegorz Ziemski
 */
public interface DeckDatabaseSchema {

    public static final Class<?>[] entities = {
            Card.class,
            CardFts.class,
            CardLearningProgress.class,
            DeckConfig.class,
            CardImported.class,
            CardEdge.class,
            FileSynced.class,
            CardImportedRemoved.class
    };

}
