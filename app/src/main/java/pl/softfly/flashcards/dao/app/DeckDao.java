package pl.softfly.flashcards.dao.app;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.db.TimeUtil;
import pl.softfly.flashcards.entity.app.Deck;

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 */
@Dao
public abstract class DeckDao {

    @Query("SELECT * FROM Deck WHERE path=:path")
    public abstract Deck findByKey(String path);

    @NonNull
    @Insert
    public abstract long insert(Deck deck);

    @NonNull
    @Update
    public abstract void update(Deck deck);

    public Deck refreshLastUpdatedAt(String path) {
        if (!path.endsWith(".db")) {
            path += ".db";
        }

        Deck deck = findByKey(path);
        if (deck != null) {
            deck.setLastUpdatedAt(TimeUtil.getNowEpochSec());
            update(deck);
        } else {
            deck = new Deck();
            deck.setName(getDeckName(path));
            deck.setPath(path);
            deck.setLastUpdatedAt(TimeUtil.getNowEpochSec());
            insert(deck);
        }
        return deck;
    }

    @NonNull
    protected String getDeckName(@NonNull String deckDbPath) {
        return deckDbPath.substring(deckDbPath.lastIndexOf("/") + 1, deckDbPath.length() - 3);
    }

}
