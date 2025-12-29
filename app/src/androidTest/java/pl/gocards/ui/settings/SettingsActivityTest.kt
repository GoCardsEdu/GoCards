package pl.gocards.ui.settings

import android.app.Application
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.gocards.ui.TestsUtil
import pl.gocards.ui.home.ScreenshotUtil

/**
 * Integration test that launches SettingsActivity and captures screenshots.
 *
 * @author Grzegorz Ziemski
 */
@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

    @Test
    fun captureSettingsThisDeckScreenshot() = runBlocking {
        val deckDbPath = createSampleDeckForTest()
        val settings = SettingsActivityRobot.startActivity(composeTestRule, getApplication(), deckDbPath)

        captureScreenshot("settings-this-deck")
        settings.clickAllDecksTab()
        captureScreenshot("settings-all-decks")
        settings.clickAppTab()
        captureScreenshot("settings-app")
    }

    private suspend fun createSampleDeckForTest(): String {
        return TestsUtil.createSampleDeck(getApplication())
    }

    private fun captureScreenshot(screenshotName: String) {
        screenshotUtil.captureScreenshot(screenshotName)
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}
