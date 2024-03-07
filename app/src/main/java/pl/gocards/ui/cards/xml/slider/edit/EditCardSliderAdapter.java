package pl.gocards.ui.cards.xml.slider.edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

import pl.gocards.ui.cards.xml.slider.slider.CardSliderAdapter;

/**
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
public class EditCardSliderAdapter extends CardSliderAdapter {

    private final Set<Integer> editCardIds = new HashSet<>();

    public EditCardSliderAdapter(@NonNull EditCardSliderActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        EditCardFragment fragment = createEditCardFragment();
        fragment.setArguments(createEditCardBundle(position));
        return fragment;
    }

    @NonNull
    protected EditCardFragment createEditCardFragment() {
        return new EditCardFragment();
    }

    @NonNull
    protected Bundle createEditCardBundle(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(EditCardFragment.DECK_DB_PATH, getDeckDbPath());
        bundle.putInt(EditCardFragment.CARD_ID, getCardId(position));
        return bundle;
    }

    @Override
    public long getItemId(int position) {
        return getCardId(position) * 10L + 1;
    }

    protected Set<Integer> getEditCardIds() {
        return editCardIds;
    }
}
