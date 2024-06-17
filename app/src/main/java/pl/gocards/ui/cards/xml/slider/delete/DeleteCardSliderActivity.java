package pl.gocards.ui.cards.xml.slider.delete;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.xml.slider.undo_delete.UndoDeleteCardSliderActivity;

/**
 * C_D_25 Delete the card
 * @author Grzegorz Ziemski
 */
public class DeleteCardSliderActivity extends UndoDeleteCardSliderActivity {

    @Nullable
    private Integer cardId_ToRemoveFromPager;

    private int cardPosition_ToRemoveFromPager;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected ViewPager2 initViewPager() {
        ViewPager2 viewPager = super.initViewPager();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager2.SCROLL_STATE_DRAGGING:
                    case ViewPager2.SCROLL_STATE_SETTLING:
                        break;
                    case ViewPager2.SCROLL_STATE_IDLE:
                        onEndSliding();
                }
            }
        });
        return viewPager;
    }

    protected void onEndSliding() {
        if (cardId_ToRemoveFromPager == null && cardPosition_ToRemoveFromPager == 0) return;

        Integer cardId = getCurrentList().get(cardPosition_ToRemoveFromPager).getId();
        if (Objects.equals(cardId_ToRemoveFromPager, cardId)) {
            getViewPager().post(() -> {
                getCurrentList().removeIf(it -> Objects.equals(it.getId(), cardId_ToRemoveFromPager));
                getAdapter().notifyItemRemoved(cardPosition_ToRemoveFromPager);
                cardId_ToRemoveFromPager = null;
                cardPosition_ToRemoveFromPager = 0;
            });
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Delete the card
     * ----------------------------------------------------------------------------------------- */

    @UiThread
    @SuppressLint("CheckResult")
    public void deleteCard() {
        int position = getCurrentPosition();
        Card card = Objects.requireNonNull(getActiveCard());
        removeAndShowNextCard();
        Disposable disposable = getDeckDb().cardRxDao().delete(card)
                .doOnComplete(() -> runOnUiThread(
                        () -> doOnCompleteDeleteCard(position, card),
                        this::onErrorDeleteCard
                ))
                .subscribe(EMPTY_ACTION, this::onErrorDeleteCard);
        addToDisposable(disposable);
    }

    @SuppressLint("CheckResult")
    @UiThread
    public void deleteNoSavedCard() {
        removeAndShowPreviousCard();
    }

    @UiThread
    protected void removeAndShowNextCard() {
        if (getActivityModel().getCardsSize() == 1) {
            showNoMoreCardsDialog();
        } else {
            // It must be a position before sliding to the next card.
            int cardId = getActiveCardId();
            int position = getCurrentPosition();
            slideToNextCardWithRotate();
            cardId_ToRemoveFromPager = cardId;
            cardPosition_ToRemoveFromPager = position;
        }
    }

    @UiThread
    protected void removeAndShowPreviousCard() {
        if (getActivityModel().getCardsSize() == 1) {
            showNoMoreCardsDialog();
        } else {
            // It must be a position before sliding to the next card.
            int cardId = getActiveCardId();
            int position = getCurrentPosition();
            slideToPreviousCardWithRotate();
            cardId_ToRemoveFromPager = cardId;
            cardPosition_ToRemoveFromPager = position;
        }
    }

    @UiThread
    private void doOnCompleteDeleteCard(int position, @NonNull Card card) {
        Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.cards_list_toast_deleted_card),
                        Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.restore), v -> restoreCard(position, card))
                .show();
    }

    private void onErrorDeleteCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this, "Error while removing the card.");
    }
}