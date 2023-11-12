package pl.gocards.ui.cards.study.slider.model;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.cards.slider.slider.CardsSliderViewModel;

/**
 * @author Grzegorz Ziemski
 */
public class StudyCardsSliderViewModel extends CardsSliderViewModel {

    /**
     * C_U_39 Undo click on the study buttons.
     */
    private Card undoCardProgress;

    @NonNull
    @Override
    public Maybe<List<CardSlider>> loadCards(
            @NonNull Consumer<? super Throwable> onError
    ) {
        return getDeckDb().cardSliderRxDao().getNextCardsToReplay()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(this::doOnSuccess);
    }

    public void setUndoCardProgress(Card undoCardProgress) {
        this.undoCardProgress = undoCardProgress;
    }

    public boolean isUndoCardProgress(int cardId) {
        return undoCardProgress != null && cardId == Objects.requireNonNull(undoCardProgress.getId());
    }

    public void clearUndoCardProgress() {
        setUndoCardProgress(null);
    }
}
