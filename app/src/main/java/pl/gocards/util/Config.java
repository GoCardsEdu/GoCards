package pl.gocards.util;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Grzegorz Ziemski
 */
public class Config {

    private static Config INSTANCE;

    private boolean databaseExternalStorage;

    private boolean testFilesExternalStorage;

    protected Config(@NonNull Context context) {
        try (InputStream is = context.getAssets().open("config.properties")) {
            Properties props = new Properties();
            props.load(is);
            databaseExternalStorage = Boolean.parseBoolean(
                    props.getProperty("database.external_storage", "")
            );
            testFilesExternalStorage = Boolean.parseBoolean(
                    props.getProperty("test.files.external_storage", "")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    public static synchronized Config getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new Config(context);
        }
        return INSTANCE;
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public boolean isDatabaseExternalStorage() {
        return databaseExternalStorage;
    }

    public boolean isTestFilesExternalStorage() {
        return testFilesExternalStorage;
    }

    public boolean isCrashlyticsEnabled(@NonNull Context context) {
        try (InputStream is = context.getAssets().open("config.properties")) {
            Properties props = new Properties();
            props.load(is);
            return Boolean.parseBoolean(
                    props.getProperty("crashlytics.enabled", "true")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean showExceptionInDialogException(@NonNull Context context) {
        try (InputStream is = context.getAssets().open("config.properties")) {
            Properties props = new Properties();
            props.load(is);
            return Boolean.parseBoolean(
                    props.getProperty("DialogException.showException", "false")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isPremiumMockEnabled(@NonNull Context context) {
        try (InputStream is = context.getAssets().open("config.properties")) {
            Properties props = new Properties();
            props.load(is);
            return Boolean.parseBoolean(
                    props.getProperty("premium.mock.enabled", "false")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isReviewMockEnabled(@NonNull Context context) {
        try (InputStream is = context.getAssets().open("config.properties")) {
            Properties props = new Properties();
            props.load(is);
            return Boolean.parseBoolean(
                    props.getProperty("review.mock.enabled", "false")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
