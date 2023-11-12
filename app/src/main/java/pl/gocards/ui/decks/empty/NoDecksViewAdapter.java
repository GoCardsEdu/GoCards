package pl.gocards.ui.decks.empty;

import androidx.annotation.NonNull;

import java.nio.file.Path;

import pl.gocards.ui.decks.search.SearchDeckViewAdapter;

/**
 * D_R_01 No deck and folders
 * @author Grzegorz Ziemski
 */
public class NoDecksViewAdapter extends SearchDeckViewAdapter {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public NoDecksViewAdapter(@NonNull NoDecksFragment listDecksFragment) {
        super(listDecksFragment);
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void loadItems(@NonNull Path folder) {
        super.loadItems(folder);
        if (paths.isEmpty()) {
            getFragment().setEmptyDeckListView(this::onErrorItemLoading);
        } else {
            getFragment().setNotEmptyDeckListView(this::onErrorItemLoading);
        }
    }

    protected NoDecksFragment getFragment() {
        return (NoDecksFragment) super.getFragment();
    }
}
