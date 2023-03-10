package pl.softfly.flashcards.entity.filesync;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import pl.softfly.flashcards.entity.deck.Card;

/**
 * Cards deleted from the deck and then deleted from the file.
 * Prevents you from removing new identical cards from the file indefinitely.
 * If someone adds the card to the file a second time, the card will not be deleted.
 *
 * @author Grzegorz Ziemski
 */
@Entity(
        tableName =  "FileSync_CardImportedRemoved",
        foreignKeys = {
                /* doesnt work with FTS4 */
                @ForeignKey(
                        onDelete = CASCADE,
                        entity = Card.class,
                        parentColumns = "id",
                        childColumns = "cardId"
                ),
                @ForeignKey(
                        onDelete = CASCADE,
                        entity = FileSynced.class,
                        parentColumns = "id",
                        childColumns = "fileSyncedId"
                )
        },
        indices = {
                @Index(value = {"fileSyncedId", "cardId"}, unique = true),
                @Index(value = {"cardId"}),
        }

)
public class CardImportedRemoved {

    @PrimaryKey
    private Integer id;

    private Integer fileSyncedId;

    private Integer cardId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFileSyncedId() {
        return fileSyncedId;
    }

    public void setFileSyncedId(Integer fileSyncedId) {
        this.fileSyncedId = fileSyncedId;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }
}
