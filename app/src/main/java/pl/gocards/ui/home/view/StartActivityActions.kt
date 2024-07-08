package pl.gocards.ui.home.view

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import pl.gocards.ui.common.OpenUrl
import pl.gocards.ui.settings.SettingsActivity
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * @author Grzegorz Ziemski
 */
class StartActivityActions(
    private var activity: Activity,
    private var analytics: FirebaseAnalyticsHelper,
    private var scope: LifecycleCoroutineScope
) {

    fun startDeckSettingsActivity(dbPath: String) {
        val intent = Intent(activity, SettingsActivity::class.java)
        intent.putExtra(SettingsActivity.DECK_DB_PATH, dbPath)
        activity.startActivity(intent)
    }

    fun startAppSettingsActivity() {
        val intent = Intent(activity, SettingsActivity::class.java)
        activity.startActivity(intent)
    }

    fun openDiscord() {
        analytics.menuOpenDiscord()
        openUrl("https://discord.gg/jYyRnD27JP")
    }

    private fun openUrl(link: String) {
        OpenUrl.openUrl(activity, scope, link)
    }
}