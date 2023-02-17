package pl.softfly.flashcards.entity.filesync;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Represents a synchronized file.
 *
 * @author Grzegorz Ziemski
 */
@Entity(
        tableName = "FileSync_FileSynced",
        indices = {
                @Index(value = "uri", unique = true)
        }
)
public class FileSynced {

    @PrimaryKey
    private Integer id;

    private String displayName;

    private String uri;

    /**
     *  It helps to more accurately estimate whether the card is newer in the deck or in the file.
     *  If the card is newer than lastSyncAt, it means that the card in the deck is newer than the card in the file.
     *
     *  {@link pl.softfly.flashcards.filesync.algorithms.sync.SyncExcelToDeck#isImportedFileNewer}
     */
    private Long lastSyncAt = 0l;

    /**
     * Timestamp of last deck update.
     * It is useful to find cards that were last changed during the sync.
     */
    private Long deckModifiedAt;

    /**
     * A deck can only automatically sync with one file.
     */
    private boolean autoSync;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Long getLastSyncAt() {
        return lastSyncAt;
    }

    public void setLastSyncAt(Long lastSyncAt) {
        this.lastSyncAt = lastSyncAt;
    }

    public Long getDeckModifiedAt() {
        return deckModifiedAt;
    }

    public void setDeckModifiedAt(Long deckModifiedAt) {
        this.deckModifiedAt = deckModifiedAt;
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }
}
