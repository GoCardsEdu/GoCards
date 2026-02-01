package pl.gocards.ui.decks.all

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.uiautomator.UiObject2
import pl.gocards.ui.MORE_BUTTON
import pl.gocards.ui.NEW_DECK
import pl.gocards.ui.Robot
import pl.gocards.ui.SEARCH_FIELD

class AllDecksTabRobot(
    composeTestRule: ComposeTestRule
): Robot(composeTestRule) {

    fun clickMoreButton() {
        clickWithTag(MORE_BUTTON)
        waitUntilTextExists("New folder")
    }

    fun clickNewDeckButton() {
        clickWithTag(NEW_DECK)
        waitUntilTextExists("Create a new deck")
    }

    fun clickDeckMoreButton(deckName: String) {
        val deckItem = findUiObject(deckName)
        val moreButton = findDeckMoreButton(deckItem)
        moreButton.click()
        assertText("List cards")
    }

    fun clickPopupMoreButton() {
        clickWithText("More")
        waitUntilTextExists("Deck Settings")
    }


    private fun findDeckMoreButton(element: UiObject2): UiObject2 {
        return findObjectByDesc(element, "Pop-up menu for the deck.")
    }

    fun clickFolderMoreButton(folderName: String) {
        val item = findUiObject(folderName)
        val moreButton = findFolderMoreButton(item)
        moreButton.click()
        assertText("Rename")
    }

    private fun findFolderMoreButton(element: UiObject2): UiObject2 {
        return findObjectByDesc(element, "Pop-up menu for the folder.")
    }

    fun clickCut() {
        clickWithText("Cut")
        waitUntilTextExists("Paste the deck here")
    }

    fun clickSearchButton() {
        clickWithContentDescription("Search")
    }

    fun enterSearchText(text: String) {
        textInputWithTag(SEARCH_FIELD, text)
        composeTestRule.waitForIdle()
        Thread.sleep(500)
    }
}