package pl.gocards.db;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * SQLite use SECONDS timestamp.
 * Java use MILLISECONDS timestamp.
 * Convert for compatibility.
 *
 * @author Grzegorz Ziemski
 */
public class Converters {

    @Nullable
    @TypeConverter
    public static Long fromStringToLong(@Nullable String value) {
        return value == null ? null : Long.parseLong(value);
    }

    @Nullable
    @TypeConverter
    public static Float fromStringToFloat(@Nullable String value) {
        return value == null ? null : Float.parseFloat(value);
    }

    @Nullable
    @TypeConverter
    public static Date fromTimestamp(@Nullable Long value) {
        return value == null ? null : new Date(TimeUnit.SECONDS.toMillis(value));
    }

    @Nullable
    @TypeConverter
    public static Long dateToTimestamp(@Nullable Date date) {
        return date == null ? null : TimeUnit.MILLISECONDS.toSeconds(date.getTime());
    }

    @Nullable
    @TypeConverter
    @SuppressWarnings("unused")
    public static LocalDateTime fromTimestampToLocalDateTime(@Nullable Long value) {
        return value == null ? null :
                LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(value),
                        ZoneId.systemDefault()
                );
    }

    @Nullable
    @TypeConverter
    @SuppressWarnings("unused")
    public static Long localDateTimeToTimestamp(@Nullable LocalDateTime dateTime) {
        return dateTime == null ? null :
                dateTime.atZone(ZoneId.systemDefault())
                        .toEpochSecond();
    }

}
