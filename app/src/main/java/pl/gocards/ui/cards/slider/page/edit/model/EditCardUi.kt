package pl.gocards.ui.cards.slider.page.edit.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.Date

/**
 * C_C_24 Edit the card
 *
 * @author Grzegorz Ziemski
 */
data class EditCardUi(
    var id: Int,
    val term: MutableState<String> = mutableStateOf(""),
    val definition: MutableState<String> = mutableStateOf(""),
    val nextReplayAt: Date? = null,
    val disabled: MutableState<Boolean> = mutableStateOf(false)
) {

    override fun equals(other: Any?): Boolean {
        return if (other is EditCardUi) {
            id == other.id
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}