package pl.gocards;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;

import pl.gocards.db.app.AppDbMainThreadUtil;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.room.entity.app.AppConfig;

/**
 * @author Grzegorz Ziemski
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {

    /** @noinspection DataFlowIssue*/
    @NonNull // Null only when the app is not active.
    private Activity activeActivity = null;

    private Boolean darkMode;

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        registerActivityLifecycleCallbacks(this);

        try {
            reloadDarkMode();
        } catch (IllegalStateException e) {
            getAppDbMainThreadUtil().deleteDatabase(this.getApplicationContext());
        }
    }

    /* -----------------------------------------------------------------------------------------
     * DarkMode
     * ----------------------------------------------------------------------------------------- */

    public void reloadDarkMode() {
        setDarkMode(getDarkModeDb());
    }

    protected Boolean getDarkModeDb() {
        String darkMode = getAppDbMainThread()
                .appConfigDao()
                .getStringByKey(AppConfig.DARK_MODE);

        if (darkMode == null) return null;

        switch (darkMode) {
            case AppConfig.DARK_MODE_ON -> {
                return true;
            }
            case AppConfig.DARK_MODE_OFF -> {
                return false;
            }
            default -> {
                return null;
            }
        }
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
        if (darkMode == null) {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (darkMode) {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    protected void setDarkMode(@AppCompatDelegate.NightMode int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        // setBarSameColoursAsToolbar();
    }

    protected AppDbMainThreadUtil getAppDbMainThreadUtil() {
        return AppDbMainThreadUtil
                .getInstance(getApplicationContext());
    }

    protected AppDatabase getAppDbMainThread() {
        return getAppDbMainThreadUtil()
                .getDatabase(getApplicationContext());
    }

    /* -----------------------------------------------------------------------------------------
     * ActivityLifecycleCallbacks
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        App.this.activeActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        App.this.activeActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        App.this.activeActivity = activity;
    }

    /** @noinspection DataFlowIssue*/
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        App.this.activeActivity = null;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // don't clear current activity because activity may get stopped after
        // the new activity is resumed
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // don't clear current activity because activity may get destroyed after
        // the new activity is resumed
    }

    @NonNull
    public AppCompatActivity getActiveActivity() {
        return (AppCompatActivity) activeActivity;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/sets
     * ----------------------------------------------------------------------------------------- */

    public Boolean getDarkMode() {
        return darkMode;
    }
}
