package pl.gocards.ui

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

open class Robot(val composeTestRule: ComposeTestRule) {

    companion object {
        @JvmStatic
        protected fun waitUntilTextExists(composeTestRule: ComposeTestRule, text: String) {
            composeTestRule.waitUntil(DEFAULT_TIMEOUT) {
                composeTestRule
                    .onAllNodesWithText(text)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
        }
    }

    protected fun clickWithText(text: String) {
        composeTestRule
            .onAllNodesWithText(text)[0]
            .performClick()
    }

    protected fun clickWithTag(testTag: String) {
        composeTestRule
            .onNodeWithTag(testTag)
            .performClick()
    }

    protected fun clickWithContentDescription(label: String) {
        composeTestRule
            .onAllNodesWithContentDescription(label)
            .onFirst()
            .performClick()
    }

    protected fun textInputWithTag(textTag: String, text: String) {
        composeTestRule
            .onNodeWithTag(textTag)
            .performTextInput(text)
    }

    protected fun waitUntilTagExists(testTag: String) {
        composeTestRule.waitUntil(DEFAULT_TIMEOUT) {
            composeTestRule
                .onAllNodesWithTag(testTag)
                .fetchSemanticsNodes()
                .any()
        }
    }

    fun waitUntilTextExists(text: String) {
        composeTestRule.waitUntil(DEFAULT_TIMEOUT) {
            composeTestRule
                .onAllNodesWithText(text)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    fun waitUntilTextContains(text: String) {
        composeTestRule.waitUntil(DEFAULT_TIMEOUT) {
            composeTestRule
                .onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    protected fun findObjectByDesc(element: UiObject2, detailMessage: String): UiObject2 {
        val parent = element.parent
            ?: throw AssertionError("Could not find parent of '$element'")

        return parent.findObject(By.desc(detailMessage))
            ?: throw AssertionError("Could not find '$element'")
    }

    protected fun findUiObject(text: String): UiObject2 {
        assertText(text)
        return getUiDevice().findObject(By.text(text))
            ?: throw AssertionError("'$text' not found")
    }

    fun assertText(text: String) {
        Thread.sleep(500) // Allow popup animation to complete
        composeTestRule.waitForIdle()

        val found = getUiDevice().wait(Until.hasObject(By.text(text)), DEFAULT_TIMEOUT)
        if (!found) {
            throw AssertionError("'$text' not found")
        }
    }

    protected fun assertById(id: String) {
        val packageName = getPackageName()
        val found = getUiDevice().wait(Until.hasObject(By.res("$packageName:id/$id")), DEFAULT_TIMEOUT)
        if (!found) {
            throw AssertionError("'$id' not found")
        }
    }

    protected fun findItemsById(id: String): List<UiObject2?> {
        val packageName = getPackageName()
        return getUiDevice().findObjects(By.res("$packageName:id/$id"))
    }

    protected fun getPackageName(): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.packageName
    }

    protected fun getUiDevice(): UiDevice {
        return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

}