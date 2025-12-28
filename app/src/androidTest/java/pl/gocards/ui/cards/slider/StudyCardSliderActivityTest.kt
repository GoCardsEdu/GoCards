package pl.gocards.ui.cards.slider

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import pl.gocards.db.deck.AppDeckDbUtil
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Grzegorz Ziemski
 */
@RunWith(AndroidJUnit4::class)
class StudyCardSliderActivityTest {

    @get:Rule
    val testName = TestName()

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var activityScenario: ActivityScenario<StudyCardSliderActivity>? = null

    @After
    fun tearDown() {
        activityScenario?.close()
    }

    @Test
    fun testWhenSwipeLeftShowSecondCard() {
        val testsFolder = getFrontendTestsFolder()
        val deckName = testName.methodName
        createSampleDeck(testsFolder, deckName)

        openStudyCardSliderActivity("$testsFolder/$deckName.db")
        assertCard(1)

        swipeLeft()
        assertCard(2)
    }

    @Test
    fun testWhenSwipeRightShowFirstCard() {
        testWhenSwipeLeftShowSecondCard()
        swipeRight()
        assertCard(1)
    }

    private fun createSampleDeck(testsFolder: Path, deckName: String) {
        val application: Application = ApplicationProvider.getApplicationContext()
        val appDeckDbUtil = AppDeckDbUtil.getInstance(application)
        appDeckDbUtil.deleteDatabase("$testsFolder/$deckName.db")

        runBlocking {
            CreateSampleNumericDeck(application).create(testsFolder, deckName, 2)
        }
    }

    private fun openStudyCardSliderActivity(deckPath: String) {
        val application: Application = ApplicationProvider.getApplicationContext()
        val intent = Intent(application, StudyCardSliderActivity::class.java).apply {
            putExtra(StudyCardSliderActivity.DECK_DB_PATH, deckPath)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityScenario = ActivityScenario.launch(intent)
    }

    private fun assertCard(i: Int) {
        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText("Term $i")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun swipeLeft() {
        composeTestRule.onNodeWithTag("text_study_box")
            .performTouchInput { swipeLeft(durationMillis = 50) }
    }

    private fun swipeRight() {
        composeTestRule.onNodeWithTag("text_study_box")
            .performTouchInput { swipeRight(durationMillis = 50) }
    }

    private fun getFrontendTestsFolder(): Path {
        val application: Application = ApplicationProvider.getApplicationContext() as Application
        val appDeckDbUtil = AppDeckDbUtil.getInstance(application)
        val rootFolder = appDeckDbUtil.getDbFolder(application)
        val testsFolder = "$rootFolder/Frontend Tests/"
        return Paths.get(testsFolder)
    }
}
