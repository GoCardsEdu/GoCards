package pl.gocards.ui.decks.kt.search

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * D_R_03 Search decks
 * @author Grzegorz Ziemski
 */
class SearchFoldersDecksViewModel(
    application: Application
) : AndroidViewModel(application) {

    @JvmField
    val isSearchActive = mutableStateOf(false)

    private val searchQuery = mutableStateOf<String?>(null)

    fun search(query: String) {
        searchQuery.value = query
        isSearchActive.value = true
    }

    fun enableSearch() {
        isSearchActive.value = true
    }

    fun disableSearch() {
        isSearchActive.value = false
    }

    fun isSearchActive(): State<Boolean> {
        return isSearchActive
    }

    fun getSearchQuery(): State<String?> {
        return searchQuery
    }
}

class SearchFoldersDecksViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SearchFoldersDecksViewModel::class.java)) {
            SearchFoldersDecksViewModel(application) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}