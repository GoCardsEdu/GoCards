package pl.softfly.flashcards.entity.deck;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author Grzegorz Ziemski
 */
@Entity(tableName = "Core_DeckConfig")
public class DeckConfig {

    public static final String FILE_SYNC_EDITING_BLOCKED_AT = "FileSync_EditingBlockedAt";

    public static final String STUDY_CARD_TERM_FONT_SIZE = "StudyCard_Term_FontSize";

    public static final String STUDY_CARD_DEFINITION_FONT_SIZE = "StudyCard_Definition_FontSize";

    public final static int STUDY_CARD_FONT_SIZE_DEFAULT = 24;

    public static final String STUDY_CARD_TD_DISPLAY_RATIO = "StudyCard_TD_DisplayRatio";

    public static final String MAX_FORGOTTEN_CARDS = "StudyCard_MaxForgottenCards";

    public static final int MAX_FORGOTTEN_CARDS_DEFAULT = 20;

    @NonNull
    @PrimaryKey
    private String key;

    private String value;

    public DeckConfig() {
    }

    public DeckConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
