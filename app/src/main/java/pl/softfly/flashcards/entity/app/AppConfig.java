package pl.softfly.flashcards.entity.app;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;

/**
 * @author Grzegorz Ziemski
 */
@Entity
public class AppConfig {

    public static final String OFF = "Off";

    public static final String DARK_MODE = "DarkMode";

    public static final String DARK_MODE_DEFAULT = "System";

    public static final String DARK_MODE_ON = "On";

    public static final String DARK_MODE_OFF = "Off";

    public static final List<String> DARK_MODE_OPTIONS = Arrays.asList(DARK_MODE_DEFAULT, DARK_MODE_ON, DARK_MODE_OFF);

    public static final String EDGE_BAR_Off = "Off";

    public static final String EDGE_BAR_SHOW_LEARNING_STATUS = "LearningStatus";

    public static final String EDGE_BAR_SHOW_RECENTLY_SYNCED = "RecentlySynced";

    public static final String LEFT_EDGE_BAR = "LeftEdgeBar";

    public static final String LEFT_EDGE_BAR_DEFAULT = EDGE_BAR_SHOW_LEARNING_STATUS;

    public static final String RIGHT_EDGE_BAR = "RightEdgeBar";

    public static final String RIGHT_EDGE_BAR_DEFAULT = EDGE_BAR_SHOW_RECENTLY_SYNCED;


    @PrimaryKey
    @NonNull
    private String key;

    private String value;

    public AppConfig(String key, String value) {
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
