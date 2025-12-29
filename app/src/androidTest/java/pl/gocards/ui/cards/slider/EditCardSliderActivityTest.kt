package pl.gocards.ui.cards.slider

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
 * @author Grzegorz Ziemski
 */
@RunWith(AndroidJUnit4::class)
class EditCardSliderActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

    @Test
    fun captureEditCardScreenshot() = runBlocking {
        val deckPath = createSampleDeck()
        val robot = openEditCardSliderActivity(deckPath)

        val card0 = robot.getCard(0)
        robot.assertTermValue(card0.term)
        robot.assertDefinitionValue(card0.definition)

        robot.moveToCard(4)
        val card4 = robot.getCard(4)
        robot.assertTermValue(card4.term)
        robot.assertDefinitionValue(card4.definition)

        screenshotUtil.captureScreenshot("edit-card")
    }

    @Test
    fun captureCreateNewCardScreenshot() = runBlocking {
        val deckPath = createSampleDeck()
        val robot = EditCardSliderActivityRobot.startActivityWithNewCard(
            composeTestRule,
            deckPath
        )
        robot.assertTermLabel()
        robot.assertDefinitionLabel()
        screenshotUtil.captureScreenshot("new-card")
    }

    private fun openEditCardSliderActivity(deckPath: String): EditCardSliderActivityRobot {
        return EditCardSliderActivityRobot.startActivity(composeTestRule, deckPath)
    }

    private suspend fun createSampleDeck(): String {
        return TestsUtil.createSampleDeck(getApplication())
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }

}
