package pl.softfly.flashcards.entity.deck;

import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

/**
 * It has been moved to a external table otherwise foreign keys do not work.
 *
 * @author Grzegorz Ziemski
 */
@Fts4(contentEntity = Card.class)
@Entity(tableName = "Core_Card_fts4")
public class CardFts {

    @PrimaryKey
    private Integer rowid;

    private Integer id;

    private String term;

    private String definition;

    public Integer getRowid() {
        return rowid;
    }

    public void setRowid(Integer rowid) {
        this.rowid = rowid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
