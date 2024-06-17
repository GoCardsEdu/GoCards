package pl.gocards.ui.cards.kt.list.search

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.ui.cards.kt.list.model.ListCardsViewModel

/**
 * C_R_02 Search cards
 * @author Grzegorz Ziemski
 */
class SearchListCardsViewModel(
    deckDb: DeckDatabase,
    application: Application
) : ListCardsViewModel(deckDb, application) {

    private val searchQuery = mutableStateOf<String?>(null)

    private val isSearchActive = mutableStateOf(false)

    fun search(query: String?, onSuccess: () -> Unit = {}) {
        searchQuery.value = query
        isSearchActive.value = true

        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNullOrEmpty()) {
                loadCardsAsync(onSuccess)
            } else {
                search(query, onSuccess)
            }
        }
    }

    private suspend fun search(
        query: String,
        onSuccess: () -> Unit
    ) {
        val cards = deckDb.cardKtxDao()
            .searchCards(query)
            .map { mapCard(it) }

        withContext(Dispatchers.Main) {
            items.clear()
            items.addAll(cards)
            onSuccess()
        }
    }

    fun disableSearch(onSuccess: () -> Unit = {}) {
        searchQuery.value = null
        isSearchActive.value = false

        viewModelScope.launch(Dispatchers.IO) {
            loadCardsAsync(onSuccess)
        }
    }

    fun enableSearch() {
        isSearchActive.value = true
    }

    fun getSearchQuery(): State<String?> {
        return searchQuery
    }

    fun isSearchActive(): State<Boolean> {
        return isSearchActive
    }
}

class SearchListCardsViewModelFactory(
    private val deckDbPath: String,
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SearchListCardsViewModel::class.java)) {
            val deckDb = AppDeckDbUtil.getInstance(application).getDatabase(application, deckDbPath)
            SearchListCardsViewModel(deckDb, application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}