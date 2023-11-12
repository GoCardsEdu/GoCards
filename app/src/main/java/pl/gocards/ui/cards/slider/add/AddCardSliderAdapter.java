package pl.gocards.ui.cards.slider.add;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

import pl.gocards.ui.cards.slider.edit.EditCardSliderAdapter;

/**
 * C_C_23 Create a new card
 * @author Grzegorz Ziemski
 */
public class AddCardSliderAdapter extends EditCardSliderAdapter {

    private final Set<Integer> newCardIds = new HashSet<>();

    public AddCardSliderAdapter(@NonNull AddCardSliderActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int cardId = getCardId(position);
        if (newCardIds.contains(cardId)) {
            NewCardFragment fragment = createNewCardFragment();
            fragment.setArguments(createNewCardBundle(position));
            return fragment;
        } else {
            return super.createFragment(position);
        }
    }

    @NonNull
    protected NewCardFragment createNewCardFragment() {
        return new NewCardFragment();
    }

    @NonNull
    protected Bundle createNewCardBundle(int position) {
        Bundle bundle = createEditCardBundle(position);
        bundle.putString(NewCardFragment.DECK_DB_PATH, getDeckDbPath());
        bundle.putInt(NewCardFragment.CARD_ID, getCardId(position));
        return bundle;
    }

    @Override
    public long getItemId(int position) {
        int cardId = getCardId(position);
        if (newCardIds.contains(cardId)) {
            return cardId * 10L + 2 ;
        } else {
            return super.getItemId(position);
        }
    }

    public Set<Integer> getNewCardIds() {
        return newCardIds;
    }
}
