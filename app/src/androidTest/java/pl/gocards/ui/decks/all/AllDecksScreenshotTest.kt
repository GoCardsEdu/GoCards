package pl.gocards.ui.decks.all

import android.app.Application
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pl.gocards.ui.DEFAULT_DECK_NAME
import pl.gocards.ui.DEFAULT_FOLDER_NAME
import pl.gocards.ui.home.HomeActivityRobot
import pl.gocards.ui.home.ScreenshotUtil

/**
 * Integration test that launches HomeActivity and captures a screenshot.
 *
 * @author Grzegorz Ziemski
 */
@RunWith(AndroidJUnit4::class)
class AllDecksScreenshotTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var screenshotUtil: ScreenshotUtil = ScreenshotUtil()

    @Test
    fun captureHomeActivityScreenshot() {
        launchHomeActivity()
        captureScreenshot("home")
    }

    @Test
    fun captureAllDecksScreenshot() {
        val home = launchHomeActivity()
        home.openAllDecksTab()
        captureScreenshot("all-decks")
    }

    @Test
    fun captureAllDecksMoreMenuScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickMoreButton()

        captureScreenshot("all-decks-more-menu")
    }

    @Test
    fun captureCreateDeckDialogScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickNewDeckButton()
        Thread.sleep(1000)

        captureScreenshot("create-deck")
    }

    @Test
    fun captureDeckPopupMenuScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickDeckMoreButton(DEFAULT_DECK_NAME)

        captureScreenshot("deck-popup-menu")
        allDecks.clickPopupMoreButton()
        captureScreenshot("deck-more-menu")
    }

    @Test
    fun capturePasteDeckScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickDeckMoreButton(DEFAULT_DECK_NAME)
        allDecks.clickCut()
        allDecks.clickDeckMoreButton(DEFAULT_DECK_NAME)

        captureScreenshot("deck-paste-action-bar")
    }

    @Test
    fun captureFolderPopupMenuScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickFolderMoreButton(DEFAULT_FOLDER_NAME)

        captureScreenshot("folder-popup-menu")
    }

    @Test
    fun captureSearchScreenshot() {
        val home = launchHomeActivity()

        val allDecks = home.openAllDecksTab()
        allDecks.clickSearchButton()
        allDecks.enterSearchText("v")

        captureScreenshot("all-decks-search")
    }

    private fun launchHomeActivity(): HomeActivityRobot {
        return HomeActivityRobot.startActivity(composeTestRule, getApplication())
    }

    private fun captureScreenshot(screenshotName: String) {
        screenshotUtil.captureScreenshot(screenshotName)
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}