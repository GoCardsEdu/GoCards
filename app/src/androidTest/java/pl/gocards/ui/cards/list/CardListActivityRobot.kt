package pl.gocards.ui.cards.list

import android.app.Application
import android.content.Intent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ActivityScenario
import androidx.test.uiautomator.UiObject2
import pl.gocards.ui.Robot
import pl.gocards.ui.SEARCH_FIELD

/**
 * @author Grzegorz Ziemski
 */
class CardListActivityRobot(
    composeTestRule: ComposeTestRule,
    var activityScenario: ActivityScenario<ListCardsActivity>?
): Robot(composeTestRule) {

    companion object {

        fun startActivity(
            composeTestRule: ComposeTestRule,
            application: Application,
            deckDbPath: String
        ): CardListActivityRobot {
            val intent = Intent(application, ListCardsActivity::class.java).apply {
                putExtra(ListCardsActivity.DECK_DB_PATH, deckDbPath)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val activityScenario = ActivityScenario.launch<ListCardsActivity>(intent)
            waitUntilTextExists(composeTestRule, "Term")
            return CardListActivityRobot(composeTestRule, activityScenario)
        }
    }

    fun clickCard(index: Int) {
        val item = findRecyclerItems()[index]
        item?.click()

    }

    fun waitForPopupMenu() {
        assertText("Select / Cut")
    }

    fun longClickCard(index: Int) {
        val item = findRecyclerItems()[index]
        item?.longClick()

    }

    fun waitForMultiSelectMode() {
        waitUntilTextExists("1 selected")
    }

    fun waitForSelectPopupMenu() {
        assertText("Paste")
    }

    fun clickSearchButton() {
        clickWithContentDescription("Search")
    }

    fun enterSearchText(text: String) {
        textInputWithTag(SEARCH_FIELD, text)
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }

    private fun findRecyclerItems(): List<UiObject2?> {
        return findItemsById("cardListItem")
    }
}
