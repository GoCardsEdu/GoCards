package pl.gocards.ui.cards.list.search;

import androidx.annotation.NonNull;

import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.list.select.SelectCardViewHolder;

/**
 * C_R_02 Search cards
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class SearchViewHolder extends SelectCardViewHolder {

    public SearchViewHolder(
            @NonNull ItemCardBinding binding,
            @NonNull SearchListCardsAdapter adapter
    ) {
        super(binding, adapter);
    }

    @NonNull
    @Override
    protected SearchListCardsAdapter getAdapter() {
        return (SearchListCardsAdapter) super.getAdapter();
    }
}