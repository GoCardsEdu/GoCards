package pl.gocards.ui.cards.xml.study.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pl.gocards.ui.cards.xml.slider.add.AddCardSliderAdapter;
import pl.gocards.ui.cards.xml.slider.add.NewCardFragment;
import pl.gocards.ui.cards.xml.study.slider.fragment.StudyCardSliderFragment;
import pl.gocards.ui.cards.xml.study.slider.fragment.StudyEditCardFragment;
import pl.gocards.ui.cards.xml.study.slider.fragment.StudyNewCardFragment;

/**
 * C_R_22 Swipe the cards left and right
 * @author Grzegorz Ziemski
 */
public class StudyCardSliderAdapter extends AddCardSliderAdapter {

    public StudyCardSliderAdapter(@NonNull StudyCardSliderActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int cardId = getCardId(position);
        if (!getEditCardIds().contains(cardId) && !getNewCardIds().contains(cardId)) {
            StudyCardSliderFragment fragment = createStudyFragment();
            fragment.setArguments(createEditCardBundle(position));
            return fragment;
        } else {
            return super.createFragment(position);
        }
    }

    @NonNull
    protected StudyCardSliderFragment createStudyFragment() {
        return new StudyCardSliderFragment();
    }

    @NonNull
    @Override
    protected NewCardFragment createNewCardFragment() {
        return new StudyNewCardFragment();
    }

    @NonNull
    @Override
    protected StudyEditCardFragment createEditCardFragment() {
        return new StudyEditCardFragment();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets Items
     * ----------------------------------------------------------------------------------------- */

    @Override
    public long getItemId(int position) {
        int cardId = getCardId(position);
        if (!getEditCardIds().contains(cardId) && !getNewCardIds().contains(cardId)) {
            return cardId * 10L + 3 ;
        } else {
            return super.getItemId(position);
        }
    }
}
