package pl.gocards.ui.cards.xml.slider.undo_delete;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.cards.xml.slider.add.AddCardSliderActivity;

/**
 * C_U_26 Undo card deletion
 * @author Grzegorz Ziemski
 */
public class UndoDeleteCardSliderActivity extends AddCardSliderActivity {

    @SuppressLint("CheckResult")
    protected void restoreCard(int position, @NonNull Card card) {
        Disposable disposable = getDeckDb().cardRxDao().restore(card)
                .doOnComplete(() -> runOnUiThread(
                        () -> doOnCompleteRestoreCard(position, new CardSlider(card)),
                        this::onErrorRestoreCard
                ))
                .subscribe(EMPTY_ACTION, this::onErrorRestoreCard);
        addToDisposable(disposable);
    }

    @UiThread
    private void doOnCompleteRestoreCard(int deletedPosition, @NonNull CardSlider backToCard) {
        restoreDeletedCard(deletedPosition, backToCard);
        showShortToastMessage(R.string.cards_list_toast_restore_card);
    }

    private void onErrorRestoreCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this, "Error while restoring the card.");
    }

    @UiThread
    protected void restoreDeletedCard(int deletedPosition, @NonNull CardSlider backToCard) {
        Objects.requireNonNull(backToCard.getId());
        int currentPosition = getNextPositionWithRotate(deletedPosition);

        boolean wasRevertCardFirst = currentPosition == 0 && deletedPosition == 1; // TODO propably can be removed
        boolean wasRevertCardLast = currentPosition == 0 && deletedPosition != 0;

        if (wasRevertCardFirst) {
            revertCardAsFirst(backToCard);
        } else if (wasRevertCardLast) {
            revertCardAsLast(backToCard);
        } else {
            revertCardAtMiddle(backToCard);
        }
    }

    @UiThread
    protected void revertCardAsFirst(CardSlider revertCard) {
        getCurrentList().add(0, revertCard);
        getAdapter().notifyItemRangeInserted(0, 1);
        getViewPager().setCurrentItem(1);
        getViewPager().setCurrentItem(0);
    }

    @UiThread
    protected void revertCardAsLast(CardSlider revertCard) {
        int newLastPosition = getCurrentList().size();
        getCurrentList().add(newLastPosition, revertCard);
        getAdapter().notifyItemRangeInserted(newLastPosition, 1);
        getViewPager().setCurrentItem(newLastPosition);
    }

    @UiThread
    protected void revertCardAtMiddle(CardSlider revertCard) {
        int currentPosition = getCurrentPosition();
        getCurrentList().add(currentPosition, revertCard);
        getAdapter().notifyItemRangeInserted(currentPosition, 1);
        getViewPager().setCurrentItem(currentPosition + 1);
        getViewPager().setCurrentItem(currentPosition);
    }
}
