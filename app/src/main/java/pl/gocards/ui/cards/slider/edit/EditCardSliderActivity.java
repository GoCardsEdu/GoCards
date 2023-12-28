package pl.gocards.ui.cards.slider.edit;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.util.Objects;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.slider.slider.CardSliderActivity;
import pl.gocards.ui.cards.slider.slider.CardSliderAdapter;

/**
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
public class EditCardSliderActivity extends CardSliderActivity {

    public static final String EDIT_CARD_ID = "EDIT_CARD_ID";

    @NonNull
    @Override
    protected CardSliderAdapter createAdapter() {
        return new EditCardSliderAdapter(this);
    }

    @Override
    protected void onSuccessLoadCards() {
        onSuccessLoadCardsEditCard();
    }

    private void onSuccessLoadCardsEditCard() {
        int editCardId = getIntent().getIntExtra(EDIT_CARD_ID, 0);
        getIntent().removeExtra(EDIT_CARD_ID);

        if (editCardId != 0) {
            runOnUiThread(() -> {
                        int item = getActivityModel().getCardIds().indexOf(editCardId);
                        getViewPager().setCurrentItem(item, false);
                    }
            );
        }
    }

    @UiThread
    public void startEditCurrentCard() {
        Card card = getActiveCard();
        if (card != null) {
            getAdapter().getEditCardIds().add(card.getId());
            getAdapter().notifyItemChanged(getCurrentPosition());
        }
    }

    @UiThread
    public void stopEditCurrentCard() {
        Card card = getActiveCard();
        if (card != null) {
            getAdapter().getEditCardIds().remove(card.getId());
            getAdapter().notifyItemChanged(getCurrentPosition());
        }
    }

    @NonNull
    public EditCardSliderAdapter getAdapter() {
        return (EditCardSliderAdapter) super.getAdapter();
    }
}
