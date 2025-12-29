package pl.gocards.ui.decks.decks.view.actions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.ui.decks.decks.ListDecksAdapter
import pl.gocards.ui.decks.decks.service.CreateSampleDeck

/**
 * @author Grzegorz Ziemski
 */
class DeckActions(
    private var adapter: ListDecksAdapter,
    private var onRefreshItems: () -> Unit,
    private var application: App
) {

    fun onClickNewDeck() {
        val folder = adapter.getCurrentFolder()
        adapter.deckDialogs.showCreateDeckDialog(folder)
    }

    fun onClickCreateSampleDeck() {
        val folder = adapter.getCurrentFolder()
        CoroutineScope(Dispatchers.IO).launch {
            CreateSampleDeck(application).create(folder)
            onRefreshItems()
        }
    }
}