package pl.gocards.ui.cards.list

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

@RunWith(AndroidJUnit4::class)
class ListCardActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

    @Test
    fun captureCardListScreenshot() = runBlocking {
        val deckDbPath = createSampleDeckForTest()

        CardListActivityRobot.startActivity(
            composeTestRule,
            getApplication(),
            deckDbPath
        )

        screenshotUtil.captureScreenshot("card-list")
    }

    @Test
    fun captureCardPopupMenuScreenshot() = runBlocking {
        val deckDbPath = createSampleDeckForTest()

        val robot = CardListActivityRobot.startActivity(
            composeTestRule,
            getApplication(),
            deckDbPath
        )

        robot.clickCard(5)
        robot.waitForPopupMenu()
        screenshotUtil.captureScreenshot("card-list-item-popup-menu")
    }

    @Test
    fun captureCardSelectPopupMenuScreenshot() = runBlocking {
        val deckDbPath = createSampleDeckForTest()

        val robot = CardListActivityRobot.startActivity(
            composeTestRule,
            getApplication(),
            deckDbPath
        )

        robot.longClickCard(3)
        robot.waitForMultiSelectMode()

        robot.longClickCard(5)
        robot.waitForSelectPopupMenu()

        screenshotUtil.captureScreenshot("card-list-item-select-popup-menu")
    }

    @Test
    fun captureCardSearchScreenshot() = runBlocking {
        val deckDbPath = createSampleDeckForTest()

        val robot = CardListActivityRobot.startActivity(
            composeTestRule,
            getApplication(),
            deckDbPath
        )
        robot.clickSearchButton()
        robot.enterSearchText("ha")
        screenshotUtil.captureScreenshot("card-list-search")
    }

    private suspend fun createSampleDeckForTest(): String {
        return TestsUtil.createSampleDeck(getApplication())
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}
