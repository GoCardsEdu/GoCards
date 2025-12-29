package pl.gocards.ui.home

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ActivityScenario
import pl.gocards.ui.ALL_DECKS
import pl.gocards.ui.Robot
import pl.gocards.ui.decks.all.AllDecksTabRobot

class HomeActivityRobot(
    composeTestRule: ComposeTestRule,
    var activityScenario: ActivityScenario<HomeActivity>?
): Robot(composeTestRule) {

    companion object {
        fun startActivity(
            composeTestRule: ComposeTestRule,
            application: Application
        ): HomeActivityRobot {
            val intent = Intent(application, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val activityScenario = ActivityScenario.launch<HomeActivity>(intent)
            waitUntilTextExists(composeTestRule, "Recent")
            return HomeActivityRobot(composeTestRule, activityScenario)
        }
    }

    fun openAllDecksTab(): AllDecksTabRobot {
        clickWithText("All decks")
        waitUntilTagExists(ALL_DECKS)
        return AllDecksTabRobot(composeTestRule)
    }
}