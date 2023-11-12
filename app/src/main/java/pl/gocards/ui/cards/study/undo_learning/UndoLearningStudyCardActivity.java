package pl.gocards.ui.cards.study.undo_learning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.cards.study.slider.StudyCardSliderActivity;

/**
 * C_U_39 Undo click on the study buttons.
 * @author Grzegorz Ziemski
 */
public class UndoLearningStudyCardActivity extends StudyCardSliderActivity {

    /**
     * It is cleared when cards are slided by the user. {@link #onDetectSlideByUser()}
     */
    private final Deque<Card> undoCards = new LinkedList<>();

    /**
     * Last card shown after switching cards by clicked button.
     * Used to detect if it was slided by the user {@link #detectSlideByUser()}.
     * Then clear the cards in {@link #undoCards}
     */
    @Nullable
    private Integer lastCardIdSlidedByButton;

    @Nullable
    private Integer lastEditCard;

    @Nullable
    private Integer lastNewCard;

    @Override
    protected void handleOnBackPressed() {
        if (lastEditCard != null) {
            this.stopEditCurrentCard();
            return;
        }

        if (lastNewCard != null) {
            this.deleteNoSavedCard();
            return;
        }

        final Card backToCard = undoCards.pollLast();
        if (backToCard == null) {
            super.handleOnBackPressed();
            return;
        }

        runOnUiThread(() -> {
            if (detectSlideByUser()) {
                onDetectSlideByUser();
                super.handleOnBackPressed();
                return;
            } else {
                lastCardIdSlidedByButton = backToCard.getId();
            }
            undoCard(backToCard);
        }, this::onErrorUndoCard);
    }

    @UiThread
    protected void undoCard(@NonNull Card backToCard) {
        getActivityModel().setUndoCardProgress(backToCard);

        boolean wasAgain = getCurrentList().contains(new CardSlider(backToCard));
        if (wasAgain) {
            slideToPreviousCardWithRotate();
        } else {
            int deletedPosition = backToCard.getOrdinal() - 1;
            if (backToCard.getDeletedAt() != null) {
                restoreCard(deletedPosition, backToCard);
            } else {
                restoreDeletedCard(deletedPosition, new CardSlider(backToCard));
            }
        }
    }

    protected void onErrorUndoCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this, "Error while undo card learning progress.");
    }

    private boolean detectSlideByUser() {
        Card card = Objects.requireNonNull(getActiveCard());
        return !Objects.equals(lastCardIdSlidedByButton, card.getId());
    }

    private void onDetectSlideByUser() {
        undoCards.clear();
        lastEditCard = null;
        lastNewCard = null;
    }

    @Override
    public void forgetAndShowNextCard() {
        addActiveCardToUndo();
        updateLastCardIdFakeSlided();
        super.forgetAndShowNextCard();
    }

    @Override
    public void removeAndShowNextCard() {
        addActiveCardToUndo();
        updateLastCardIdFakeSlided();
        super.removeAndShowNextCard();
    }

    private void addActiveCardToUndo() {
        undoCards.add(getActiveCard());
    }

    private void updateLastCardIdFakeSlided() {
        int nextPosition = isLastCard() ? 0 : getNextPosition();
        lastCardIdSlidedByButton = getCurrentList().get(nextPosition).getId();
    }

    @UiThread
    public void startEditCurrentCard() {
        super.startEditCurrentCard();
        lastEditCard = getActiveCardId();
    }

    @Override
    public void stopEditCurrentCard() {
        super.stopEditCurrentCard();
        lastEditCard = null;
    }

    @UiThread
    @Override
    protected void addNewCardToSlider(int position, int newCardId) {
        super.addNewCardToSlider(position, newCardId);
        lastNewCard = newCardId;
    }

    @Override
    public void stopNewCard(Integer cardId) {
        super.stopNewCard(cardId);
        lastNewCard = null;
    }

    @UiThread
    public void deleteNoSavedCard() {
        super.deleteNoSavedCard();
        lastNewCard = null;
    }
}
