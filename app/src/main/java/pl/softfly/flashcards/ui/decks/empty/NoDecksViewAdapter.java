package pl.softfly.flashcards.ui.decks.empty;

import androidx.annotation.NonNull;

import java.nio.file.Path;

import pl.softfly.flashcards.ui.decks.search.SearchDeckViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class NoDecksViewAdapter extends SearchDeckViewAdapter {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public NoDecksViewAdapter(
            @NonNull MainActivity activity,
            NoDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void loadItems(@NonNull Path folder) {
        super.loadItems(folder);
        if (paths.isEmpty()) {
            getActivity().getAllDecksFragment().setEmptyDeckListView(this::onErrorBindView);
        } else {
            getActivity().getAllDecksFragment().setNotEmptyDeckListView(this::onErrorBindView);
        }
    }
}
