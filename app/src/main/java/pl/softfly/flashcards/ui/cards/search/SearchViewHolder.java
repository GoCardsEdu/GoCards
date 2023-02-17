package pl.softfly.flashcards.ui.cards.search;

import pl.softfly.flashcards.databinding.ItemCardBinding;
import pl.softfly.flashcards.ui.cards.standard.CardViewHolder;

/**
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
public class SearchViewHolder extends CardViewHolder {

    public SearchViewHolder(
            ItemCardBinding binding,
            SearchViewAdapter adapter
    ) {
        super(binding, adapter);
    }

    @Override
    protected SearchViewAdapter getAdapter() {
        return (SearchViewAdapter) super.getAdapter();
    }
}