package pl.gocards.ui

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.ui.cards.slider.CreateSampleNumericDeck
import pl.gocards.ui.decks.decks.service.CreateSampleDeck
import java.nio.file.Path
import java.nio.file.Paths

class TestsUtil {
    companion object {
        suspend fun createSampleDeck(application: Application): String {
            val deckDbUtil = AppDeckDbUtil.getInstance(application)
            val firstFolder = getFirstFolder(deckDbUtil, application)

            deckDbUtil.deleteDatabase("$firstFolder/Sample Deck.db")
            CreateSampleDeck(application).create(firstFolder)

            return "$firstFolder/Sample Deck.db"
        }

        suspend fun createSampleNumericDeck(deckName: String): String {
            val application: Application = ApplicationProvider.getApplicationContext()
            val deckDbUtil = AppDeckDbUtil.getInstance(application)
            val firstFolder = getFirstFolder(deckDbUtil, application)

            deckDbUtil.deleteDatabase("$firstFolder/$deckName.db")
            CreateSampleNumericDeck(application).create(firstFolder, deckName, 2)

            return "$firstFolder/$deckName.db"
        }

        private fun getFirstFolder(deckDbUtil: AppDeckDbUtil, application: Application): Path {
            val rootFolder = deckDbUtil.getDbRootFolderPath(application)
            return deckDbUtil.listFolders(rootFolder)[0]
        }

        private fun getFrontendTestsFolder(): Path {
            val application: Application =
                ApplicationProvider.getApplicationContext() as Application
            val appDeckDbUtil = AppDeckDbUtil.getInstance(application)
            val rootFolder = appDeckDbUtil.getDbRootFolderPath(application)
            val testsFolder = "$rootFolder/Frontend Tests/"
            return Paths.get(testsFolder)
        }
    }
}