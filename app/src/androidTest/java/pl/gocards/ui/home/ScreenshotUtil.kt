package pl.gocards.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.File
import java.io.FileOutputStream

/**
 * Utility class for capturing screenshots during instrumented tests.
 *
 * @author Grzegorz Ziemski
 */
class ScreenshotUtil {

    companion object {
        private const val DEVICE_SCREENSHOT_DIR = "/sdcard/Pictures/GoCards"
    }

    /**
     * Captures a screenshot excluding the Android system navigation bar.
     *
     * @param screenshotName Name of the screenshot file (without .png extension)
     */
    fun captureScreenshot(screenshotName: String) {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val tempFile = File(DEVICE_SCREENSHOT_DIR, "temp_$screenshotName.png")
        val screenshotFile = File(DEVICE_SCREENSHOT_DIR, "$screenshotName.png")

        // Ensure directory exists
        screenshotFile.parentFile?.mkdirs()

        try {
            // Take screenshot of entire screen (includes system bars)
            val success = uiDevice.takeScreenshot(tempFile)

            if (!success) {
                println("✗ Failed to capture screenshot: $screenshotName")
                return
            }

            removeNavigationBar(screenshotName, screenshotFile, tempFile)
        } catch (e: Exception) {
            println("✗ Error capturing screenshot: ${e.message}")
            e.printStackTrace()
            tempFile.delete()
        }
    }

    fun removeNavigationBar(screenshotName: String, screenshotFile: File, tempFile: File) {
        // Load the screenshot
        val originalBitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
        if (originalBitmap == null) {
            println("✗ Failed to load screenshot bitmap: $screenshotName")
            tempFile.delete()
            return
        }

        // Get navigation bar height (typically 132 pixels on most devices, ~48dp)
        val navigationBarHeight = getNavigationBarHeight()

        // Crop out the navigation bar from the bottom
        val croppedHeight = originalBitmap.height - navigationBarHeight
        val croppedBitmap = Bitmap.createBitmap(
            originalBitmap,
            0, // x
            0, // y
            originalBitmap.width, // width
            croppedHeight // height (excluding nav bar)
        )

        // Save the cropped bitmap
        FileOutputStream(screenshotFile).use { out ->
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Clean up
        originalBitmap.recycle()
        croppedBitmap.recycle()
        tempFile.delete()

        println("✓ Screenshot saved (without navigation bar): ${screenshotFile.absolutePath}")
        println("  To copy to project, run: adb pull ${screenshotFile.absolutePath} img/")
    }

    /**
     * Gets the navigation bar height from system resources.
     * Returns 132 pixels as a safe default if unable to determine.
     */
    private fun getNavigationBarHeight(): Int {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            132 // Default fallback (48dp at xxhdpi)
        }
    }
}