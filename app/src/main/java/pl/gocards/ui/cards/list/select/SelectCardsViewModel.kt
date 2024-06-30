package pl.gocards.ui.cards.list.select

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.gocards.ui.cards.list.model.UiListCard
import java.util.stream.Collectors

/**
 * @author Grzegorz Ziemski
 */
class SelectCardsViewModel(
    application: Application
): AndroidViewModel(application) {

    private val selectedCards: MutableSet<UiListCard> = HashSet() // TODO maybe only ids??
    private val countSelected: MutableState<Int> = mutableIntStateOf(0)

    fun select(card: UiListCard) {
        selectedCards.add(card)
        countSelected.value = selectedCards.size
    }

    fun select(cards: Collection<UiListCard>) {
        selectedCards.addAll(cards)
        countSelected.value = selectedCards.size
    }

    fun deselect(card: UiListCard) {
        selectedCards.remove(card)
        countSelected.value = selectedCards.size
    }

    fun deselectAll() {
        if (selectedCards.isNotEmpty()) {
            selectedCards.clear()
            countSelected.value = selectedCards.size
        }
    }

    fun isSelected(card: UiListCard): Boolean {
        return selectedCards.contains(card)
    }

    fun countSelected(): Int {
        return selectedCards.size
    }

    fun isSelectionMode(): Boolean {
        return selectedCards.isNotEmpty()
    }

    fun getSelectedCardIds(): MutableList<Int> {
        return selectedCards
            .stream()
            .map { card -> card.id }
            .collect(Collectors.toList())
    }

    fun toSet(): Set<UiListCard> {
        return HashSet(selectedCards)
    }

    fun getCountSelectedState(): MutableState<Int> {
        return countSelected
    }
}

class SelectCardsViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SelectCardsViewModel::class.java)) {
            SelectCardsViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}