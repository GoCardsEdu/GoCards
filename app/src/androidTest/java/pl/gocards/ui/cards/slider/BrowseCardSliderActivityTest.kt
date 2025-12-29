package pl.gocards.ui.cards.slider

import android.app.Application
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TestName
import org.junit.runner.RunWith
import pl.gocards.ui.TestsUtil
import pl.gocards.ui.home.ScreenshotUtil

/**
 * @author Grzegorz Ziemski
 */
@RunWith(Enclosed::class)
class BrowseCardSliderActivityTest {

    @RunWith(AndroidJUnit4::class)
    class SampleDeck {

        @get:Rule
        val composeTestRule = createEmptyComposeRule()

        private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

        @Test
        fun captureBrowseCardSliderScreenshot() = runBlocking {
            val deckPath = createSampleDeck()

            val robot = openBrowseCardSliderActivity(deckPath)
            val card = robot.getCard(0)
            robot.assertTerm(card.term)

            robot.moveToCard(3)
            val thirdCard = robot.getCard(3)
            robot.assertTerm(thirdCard.term)

            robot.clickShowDefinition()
            robot.assertDefinition(thirdCard.definition)

            robot.clickMoreButton()

            screenshotUtil.captureScreenshot("browse-card-slider-more")
        }

        private fun openBrowseCardSliderActivity(deckPath: String): BrowseCardSliderActivityRobot {
            return BrowseCardSliderActivityRobot.startActivity(composeTestRule, deckPath)
        }

        private suspend fun createSampleDeck(): String {
            return TestsUtil.createSampleDeck(getApplication())
        }

        private fun getApplication(): Application {
            return ApplicationProvider.getApplicationContext()
        }
    }
}
