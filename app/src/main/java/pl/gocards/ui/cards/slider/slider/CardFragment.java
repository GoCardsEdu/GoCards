package pl.gocards.ui.cards.slider.slider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.base.IconInToolbarFragment;

/**
 * C_R_22 Swipe the cards left and right
 * @author Grzegorz Ziemski
 */
public abstract class CardFragment extends IconInToolbarFragment {

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onResume() {
        super.onResume();
        requireSliderActivity().setActiveFragment(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireSliderActivity().setActiveFragment(null);
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets
     * ----------------------------------------------------------------------------------------- */

    @Nullable
    public abstract Card getCard();

    public abstract int getCardId();

    @NonNull
    protected CardSliderActivity requireSliderActivity() {
        return (CardSliderActivity) requireActivity();
    }
}
