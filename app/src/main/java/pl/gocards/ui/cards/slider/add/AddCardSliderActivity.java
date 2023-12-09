package pl.gocards.ui.cards.slider.add;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.cards.slider.edit.EditCardSliderActivity;

/**
 * C_C_23 Create a new card
 * @author Grzegorz Ziemski
 */
public class AddCardSliderActivity extends EditCardSliderActivity {

    public static final String ADD_NEW_CARD = "ADD_NEW_CARD";

    public static final String NEW_CARD_AFTER_CARD_ID = "NEW_CARD_AFTER_CARD_ID";

    private int lastCardId;

    @NonNull
    @Override
    protected AddCardSliderAdapter createAdapter() {
        return new AddCardSliderAdapter(this);
    }

    @Override
    protected void onSuccessLoadCards() {
        if (newCard()) {
            return;
        }
        if (firstNewCard()) {
            return;
        }
        super.onSuccessLoadCards();
    }

    private boolean newCard() {
        int newCardAfterCardId = getIntent().getIntExtra(NEW_CARD_AFTER_CARD_ID, 0);
        getIntent().removeExtra(NEW_CARD_AFTER_CARD_ID);
        if (newCardAfterCardId != 0) {
            createNewCardAfter(newCardAfterCardId);
            return true;
        }
        return false;
    }

    public void createNewCardAfter(int cardId) {
        int position = getActivityModel().getCardIds().indexOf(cardId);
        runOnUiThread(() -> addNewCardToSlider(position + 1));
    }

    private boolean firstNewCard() {
        boolean isNewCard = getIntent().getBooleanExtra(ADD_NEW_CARD, false);
        getIntent().removeExtra(ADD_NEW_CARD);
        if (isNewCard) {
            runOnUiThread(() -> addNewCardToSlider(getLastPosition() + 1));
            return true;
        }
        return false;
    }

    @UiThread
    public void stopNewCard(Integer cardId) {
        getAdapter().getNewCardIds().remove(cardId);
        getAdapter().notifyItemChanged(getCurrentPosition());
    }

    public void addNewCardToSlider(int position) {
        if (lastCardId == 0) {
            Disposable disposable = getDeckDb().cardRxDao().lastId()
                    .subscribeOn(Schedulers.io())
                    .doOnEvent((value, error) -> {
                        if (value == null && error == null) {
                            runOnUiThread(() -> addNewCardToSlider(position, 1));
                        }
                    })
                    .doOnSuccess(lastCardId -> runOnUiThread(() -> {
                                this.lastCardId = lastCardId + 1;
                                addNewCardToSlider(position, this.lastCardId);
                            }, this::onErrorAddCard)
                    )
                    .ignoreElement()
                    .subscribe(EMPTY_ACTION, this::onErrorAddCard);
            addToDisposable(disposable);
        } else {
            runOnUiThread(() -> {
                this.lastCardId++;
                addNewCardToSlider(position, this.lastCardId);
            });
        }
    }

    @UiThread
    protected void addNewCardToSlider(int position, int newCardId) {
        getCurrentList().add(position, new CardSlider(newCardId, false));
        getAdapter().getNewCardIds().add(newCardId);
        getAdapter().notifyItemInserted(position);
        getViewPager().setCurrentItem(position);
    }

    private void onErrorAddCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this, "Error while adding the card.");
    }

    @NonNull
    public AddCardSliderAdapter getAdapter() {
        return (AddCardSliderAdapter) super.getAdapter();
    }
}
