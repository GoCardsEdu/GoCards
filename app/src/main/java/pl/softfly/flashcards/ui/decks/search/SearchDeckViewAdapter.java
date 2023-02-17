package pl.softfly.flashcards.ui.decks.search;

import androidx.annotation.NonNull;

import pl.softfly.flashcards.ui.decks.folder.FolderDeckViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class SearchDeckViewAdapter extends FolderDeckViewAdapter {

    public SearchDeckViewAdapter(
            @NonNull MainActivity activity,
            SearchDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void searchItems(String query) {
        if (query.isEmpty()) {
            refreshItems();
        } else {
            paths.clear();
            paths.addAll(getStorageDb().searchDatabases(getRootFolder(), query));
            paths.addAll(getStorageDb().searchFolders(getRootFolder(), query));
            runOnUiThread(() -> notifyDataSetChanged(), this::onErrorBindView);
        }
    }

    @Override
    public void onItemClick(int position) {
        super.onItemClick(position);
        getFragment().clearSearch();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    protected SearchDecksFragment getFragment() {
        return (SearchDecksFragment) super.getFragment();
    }
}
