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
class StudyCardSliderActivityTest {

    @RunWith(AndroidJUnit4::class)
    class SampleDeck {

        @get:Rule
        val composeTestRule = createEmptyComposeRule()

        private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

        @Test
        fun captureStudyCardSliderMoreScreenshot() = runBlocking {
            val deckPath = createSampleDeck()

            val robot = openStudyCardSliderActivity(deckPath)
            val card = robot.getCard(0)
            robot.assertTerm(card.term)

            robot.moveToCard(3)
            val thirdCard = robot.getCard(3)
            robot.assertTerm(thirdCard.term)

            robot.clickShowDefinition()
            robot.assertDefinition(thirdCard.definition)
            robot.waitUntilTextContains("Again")
            robot.waitUntilTextContains("Quick Repetition")

            robot.clickMoreButton()

            screenshotUtil.captureScreenshot("study-card-slider-more")
        }

        @Test
        fun captureCodeScreenshot() = runBlocking {
            val deckPath = createSampleDeck()

            val robot = openStudyCardSliderActivity(deckPath)
            val card = robot.getCard(0)
            robot.assertTerm(card.term)

            robot.moveToCard(2)
            val codeCard = robot.getCard(2)
            robot.assertTerm(codeCard.term)

            robot.clickShowDefinition()
            robot.assertHtmlDefinitionDisplayed()
            robot.moveDividerBar(0.4f)
            robot.setWebViewZoom(70)

            screenshotUtil.captureScreenshot("study-card-slider-code")
        }

        private fun openStudyCardSliderActivity(deckPath: String): StudyCardSliderActivityRobot {
            return StudyCardSliderActivityRobot.startActivity(composeTestRule, deckPath)
        }

        private suspend fun createSampleDeck(): String {
            return TestsUtil.createSampleDeck(getApplication())
        }

        private fun getApplication(): Application {
            return ApplicationProvider.getApplicationContext()
        }

    }

    @RunWith(AndroidJUnit4::class)
    class NumericDeck {

        @get:Rule
        val testName = TestName()

        @get:Rule
        val composeTestRule = createEmptyComposeRule()

        @Test
        fun testWhenSwipeLeftShowSecondCard() = runBlocking {
            val deckName = testName.methodName
            val deckPath = createSampleNumericDeck(deckName)

            val robot = openStudyCardSliderActivity(deckPath)
            robot.assertTerm("Term 1")
            robot.swipeLeft()
            robot.assertTerm("Term 2")
        }

        @Test
        fun testWhenSwipeRightShowFirstCard() = runBlocking {
            val deckName = testName.methodName
            val deckPath = createSampleNumericDeck(deckName)

            val robot = openStudyCardSliderActivity(deckPath)
            robot.assertTerm("Term 1")

            robot.swipeLeft()
            robot.assertTerm("Term 2")

            robot.swipeRight()
            robot.assertTerm("Term 1")
        }

        private suspend fun createSampleNumericDeck(deckName: String): String {
            return TestsUtil.createSampleNumericDeck(deckName)
        }

        private fun openStudyCardSliderActivity(deckPath: String): StudyCardSliderActivityRobot {
            return StudyCardSliderActivityRobot.startActivity(composeTestRule, deckPath)
        }
    }
}
