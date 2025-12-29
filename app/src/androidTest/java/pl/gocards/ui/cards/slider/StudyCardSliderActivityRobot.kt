package pl.gocards.ui.cards.slider

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.entity.deck.DeckConfig
import pl.gocards.ui.cards.slider.page.study.model.StudyCardUi
import pl.gocards.ui.STUDY_BOX
import pl.gocards.ui.MORE_BUTTON
import pl.gocards.ui.Robot

class StudyCardSliderActivityRobot(
    composeTestRule: ComposeTestRule,
    var activityScenario: ActivityScenario<StudyCardSliderActivity>,
    var activity: StudyCardSliderActivity,
    var deckDbPath: String
): Robot(composeTestRule) {

    companion object {

        fun startActivity(
            composeTestRule: ComposeTestRule,
            deckDbPath: String
        ): StudyCardSliderActivityRobot {
            val application: Application = ApplicationProvider.getApplicationContext()
            val intent = Intent(application, StudyCardSliderActivity::class.java).apply {
                putExtra(StudyCardSliderActivity.DECK_DB_PATH, deckDbPath)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val activityScenario = ActivityScenario.launch<StudyCardSliderActivity>(intent)
            var activity: StudyCardSliderActivity? = null
            activityScenario.onActivity { activity = it }
            return StudyCardSliderActivityRobot(composeTestRule, activityScenario, activity!!, deckDbPath)
        }
    }

    fun swipeLeft() {
        composeTestRule.onNodeWithTag(STUDY_BOX)
            .performTouchInput { swipeLeft(durationMillis = 50) }
    }

    fun swipeRight() {
        composeTestRule.onNodeWithTag(STUDY_BOX)
            .performTouchInput { swipeRight(durationMillis = 50) }
    }

    fun moveToCard(index: Int) {
        val viewModel = activity.viewModel!!
        activity.runOnUiThread {
            viewModel.sliderCardManager.setScrollToPage(index)
        }
        Thread.sleep(1000)
    }

    fun clickShowDefinition() {
        clickWithText("?\nShow definition")
    }

    fun clickMoreButton() {
        clickWithTag(MORE_BUTTON)
        waitUntilTextExists("New")
    }

    fun assertTerm(text: String) {
        waitUntilTextExists(text)
    }

    fun assertDefinition(text: String) {
        waitUntilTextExists(text)
    }

    fun assertHtmlDefinitionDisplayed() {
        waitUntilTagExists(STUDY_BOX)
    }

    fun moveDividerBar(ratio: Float) {
        activity.runOnUiThread {
            val studyCard = getCurrentStudyCard()
            val windowHeight = studyCard.termHeightPx.value ?: 1000f
            studyCard.termHeightPx.value = windowHeight * ratio
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

    fun setDefinitionFontSizeToMin() {
        activity.runOnUiThread {
            val studyCard = getCurrentStudyCard()
            studyCard.defFontSize.value = DeckConfig.STUDY_CARD_FONT_SIZE_MIN.sp
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

    /**
     * Zoom out the WebView content by setting textZoom programmatically.
     * @param zoomPercent The zoom level as percentage (e.g., 50 = 50% = smaller text)
     */
    fun setWebViewZoom(zoomPercent: Int = 50) {
        activity.runOnUiThread {
            val webView = findWebView(activity.window.decorView)
            webView?.settings?.textZoom = zoomPercent
        }
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

    private fun findWebView(view: View): WebView? {
        if (view is WebView) {
            return view
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findWebView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    private fun getCurrentStudyCard(): StudyCardUi {
        val viewModel = activity.viewModel
        val currentPage = viewModel?.sliderCardManager?.getSettledPage()
        val currentCardId = viewModel?.sliderCardManager?.getItem(currentPage!!)?.id
        return viewModel?.studyCardManager?.cards?.value?.get(currentCardId)!!
    }

    suspend fun getCard(cardIndex: Int): Card {
        val deckDb = getDeckDb(deckDbPath)
        val cards = deckDb.cardSliderKtxDao().getAllCards()
        return deckDb.cardKtxDao().getCard(cards[cardIndex].id!!)!!
    }

    private fun getDeckDb(deckDbPath: String): DeckDatabase {
        val deckDbUtil = AppDeckDbUtil.getInstance(getApplication())
        return deckDbUtil.getDatabase(getApplication(), deckDbPath)
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}