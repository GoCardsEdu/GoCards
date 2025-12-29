package pl.gocards.ui.settings

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ActivityScenario
import pl.gocards.ui.Robot

class SettingsActivityRobot(
    composeTestRule: ComposeTestRule,
    var activityScenario: ActivityScenario<SettingsActivity>?
): Robot(composeTestRule) {

    companion object {
        fun startActivity(
            composeTestRule: ComposeTestRule,
            application: Application,
            deckDbPath: String
        ): SettingsActivityRobot {
            val intent = Intent(application, SettingsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(SettingsActivity.DECK_DB_PATH, deckDbPath)
            }
            val activityScenario = ActivityScenario.launch<SettingsActivity>(intent)
            waitUntilTextExists(composeTestRule, "This deck")
            return SettingsActivityRobot(composeTestRule, activityScenario)
        }
    }

    fun clickAllDecksTab(): SettingsActivityRobot {
        clickWithText("All decks")
        waitUntilTextExists("All decks")
        return this
    }

    fun clickAppTab(): SettingsActivityRobot {
        clickWithText("App")
        waitUntilTextExists("Dark Mode")
        return this
    }
}
