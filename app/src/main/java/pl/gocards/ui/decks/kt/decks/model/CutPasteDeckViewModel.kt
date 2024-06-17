package pl.gocards.ui.decks.kt.decks.model

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.internal.functions.Functions
import pl.gocards.db.deck.AppDeckDbUtil
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class CutPasteDeckViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    val showDeckPasteBar = mutableStateOf(false)

    val cutPath = mutableStateOf<Path?>(null)

    /**
     * D_R_09 Cut the deck
     */
    fun cut(path: Path) {
        cutPath.value = path
        showDeckPasteBar.value = true
    }

    /**
     * D_U_10 Paste the deck
     */
    @SuppressLint("CheckResult")
    fun paste(
        toFolder: Path,
        onSuccess: (newDeckName: String) -> Unit
    ) {
        val path = cutPath.value ?: return

        getDeckDbUtil().moveDatabase(
            application,
            path,
            toFolder
        )
            .doOnSuccess { newDeckName -> onSuccess(newDeckName) }
            .ignoreElement()
            .subscribe(Functions.EMPTY_ACTION)

        clear()
    }

    fun clear() {
        cutPath.value = null
        showDeckPasteBar.value = false
    }

    private fun getDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(this.application)
    }
}