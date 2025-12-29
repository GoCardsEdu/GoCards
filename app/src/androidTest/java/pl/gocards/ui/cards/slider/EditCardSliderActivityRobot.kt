package pl.gocards.ui.cards.slider

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.ui.Robot

class EditCardSliderActivityRobot(
    composeTestRule: ComposeTestRule,
    var activityScenario: ActivityScenario<EditCardSliderActivity>,
    var activity: EditCardSliderActivity,
    var deckDbPath: String
): Robot(composeTestRule) {

    companion object {

        fun startActivity(
            composeTestRule: ComposeTestRule,
            deckDbPath: String,
            editCardId: Int = 0
        ): EditCardSliderActivityRobot {
            val application: Application = ApplicationProvider.getApplicationContext()
            val intent = Intent(application, EditCardSliderActivity::class.java).apply {
                putExtra(EditCardSliderActivity.DECK_DB_PATH, deckDbPath)
                if (editCardId != 0) {
                    putExtra(EditCardSliderActivity.EDIT_CARD_ID, editCardId)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val activityScenario = ActivityScenario.launch<EditCardSliderActivity>(intent)
            var activity: EditCardSliderActivity? = null
            activityScenario.onActivity { activity = it }
            return EditCardSliderActivityRobot(composeTestRule, activityScenario, activity!!, deckDbPath)
        }

        fun startActivityWithNewCard(
            composeTestRule: ComposeTestRule,
            deckDbPath: String
        ): EditCardSliderActivityRobot {
            val application: Application = ApplicationProvider.getApplicationContext()
            val intent = Intent(application, EditCardSliderActivity::class.java).apply {
                putExtra(EditCardSliderActivity.DECK_DB_PATH, deckDbPath)
                putExtra(EditCardSliderActivity.ADD_NEW_CARD, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val activityScenario = ActivityScenario.launch<EditCardSliderActivity>(intent)
            var activity: EditCardSliderActivity? = null
            activityScenario.onActivity { activity = it }
            return EditCardSliderActivityRobot(composeTestRule, activityScenario, activity!!, deckDbPath)
        }
    }

    fun assertTermLabel() {
        waitUntilTextExists("Term:")
    }

    fun assertDefinitionLabel() {
        waitUntilTextExists("Definition:")
    }

    fun assertDisabledLabel() {
        waitUntilTextExists("Disabled")
    }

    fun assertTermValue(term: String) {
        waitUntilTextExists(term)
    }

    fun assertDefinitionValue(definition: String) {
        waitUntilTextExists(definition)
    }

    fun enterTerm(text: String) {
        val device = getUiDevice()
        val termField = device.findObject(
            androidx.test.uiautomator.By.text("Term:")
        )?.parent?.findObject(
            androidx.test.uiautomator.By.clazz("android.widget.EditText")
        )
        termField?.text = text
        Thread.sleep(500)
    }

    fun enterDefinition(text: String) {
        val device = getUiDevice()
        val definitionField = device.findObject(
            androidx.test.uiautomator.By.text("Definition:")
        )?.parent?.findObject(
            androidx.test.uiautomator.By.clazz("android.widget.EditText")
        )
        definitionField?.text = text
        Thread.sleep(500)
    }

    suspend fun getCardCount(): Int {
        val deckDb = getDeckDb(deckDbPath)
        return deckDb.cardSliderKtxDao().getAllCards().size
    }

    fun moveToCard(index: Int) {
        val viewModel = activity.viewModel!!
        activity.runOnUiThread {
            viewModel.sliderCardManager.setScrollToPage(index)
        }
    }

    suspend fun getCard(cardIndex: Int): Card {
        val deckDb = getDeckDb(deckDbPath)
        val cards = deckDb.cardSliderKtxDao().getAllCards()
        return deckDb.cardKtxDao().getCard(cards[cardIndex].id!!)!!
    }

    suspend fun getFirstCardId(): Int {
        val deckDb = getDeckDb(deckDbPath)
        val cards = deckDb.cardSliderKtxDao().getAllCards()
        return cards[0].id!!
    }

    private fun getDeckDb(deckDbPath: String): DeckDatabase {
        val deckDbUtil = AppDeckDbUtil.getInstance(getApplication())
        return deckDbUtil.getDatabase(getApplication(), deckDbPath)
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}