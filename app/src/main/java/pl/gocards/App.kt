package pl.gocards

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.gocards.db.app.AppDbMainThreadUtil
import pl.gocards.db.app.AppDbUtil
import pl.gocards.db.room.AppDatabase
import pl.gocards.room.entity.app.AppConfig


/**
 * @author Grzegorz Ziemski
 */
class App : Application(), ActivityLifecycleCallbacks, Thread.UncaughtExceptionHandler {

    /**
     * Null only when the app is not active.
     * @noinspection DataFlowIssue
     */
    private var activeActivity: Activity? = null

    private var darkMode: Boolean? = null

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        registerActivityLifecycleCallbacks(this)

        try {
            reloadDarkMode()
        } catch (e: IllegalStateException) {
            getAppDbMainThreadUtil().deleteDatabase(this.applicationContext)
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                uncaughtException(thread, throwable)
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val appDb = getAppDbMainThread()
        appDb.appConfigDao().refreshLastExceptionAt()
    }

    /* -----------------------------------------------------------------------------------------
     * DarkMode
     * ----------------------------------------------------------------------------------------- */

    fun reloadDarkMode() {
        setDarkMode(getDarkModeDb())
    }

    private fun getDarkModeDb(): Boolean? {
        val darkMode = getAppDbMainThread()
            .appConfigDao()
            .getStringByKey(AppConfig.DARK_MODE) ?: return null

        return when (darkMode) {
            AppConfig.DARK_MODE_ON -> {
                true
            }
            AppConfig.DARK_MODE_OFF -> {
                false
            }
            else -> {
                null
            }
        }
    }

    private fun setDarkMode(darkMode: Boolean?) {
        if (darkMode == null) {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else if (darkMode) {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setDarkMode(@NightMode mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        // setBarSameColoursAsToolbar();
    }

    private fun getAppDbMainThreadUtil(): AppDbMainThreadUtil {
        return AppDbMainThreadUtil
            .getInstance(applicationContext)
    }

    private fun getAppDbMainThread(): AppDatabase {
        return getAppDbMainThreadUtil()
            .getDatabase(applicationContext)
    }

    /* -----------------------------------------------------------------------------------------
     * ActivityLifecycleCallbacks
     * ----------------------------------------------------------------------------------------- */

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        this.activeActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        this.activeActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        this.activeActivity = activity
    }

    /** @noinspection DataFlowIssue
     */
    override fun onActivityPaused(activity: Activity) {
        this.activeActivity = null
    }

    override fun onActivityStopped(activity: Activity) {
        // don't clear current activity because activity may get stopped after
        // the new activity is resumed
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        // don't clear current activity because activity may get destroyed after
        // the new activity is resumed
    }

    fun getActiveActivity(): AppCompatActivity {
        return activeActivity as AppCompatActivity
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/sets
     * ----------------------------------------------------------------------------------------- */

    fun getDarkMode(): Boolean? {
        return darkMode
    }
}