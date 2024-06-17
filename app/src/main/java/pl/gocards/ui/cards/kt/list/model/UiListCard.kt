package pl.gocards.ui.cards.kt.list.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * @author Grzegorz Ziemski
 */
data class UiListCard(
    val id: Int,
    val ordinal: Int,
    val term: String = "",
    val definition: String = "",
    val isActive: MutableState<Boolean> = mutableStateOf(false),
    val isSelected: MutableState<Boolean> = mutableStateOf(false)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UiListCard

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}