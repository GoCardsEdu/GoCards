package pl.gocards.ui.cards.xml.study.slider.model;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.xml.study.undo_learning.UndoLearningStudyViewModel;

/**
 * C_R_30 Study the cards
 * @author Grzegorz Ziemski
 */
public class StudyCardViewModel extends UndoLearningStudyViewModel {

    private StudyCardsSliderViewModel cardsViewModel;
    @NonNull
    private final MutableLiveData<Card> card = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<Boolean> showDefinition = new MutableLiveData<>();

    public void preLoadCard(
            @NonNull Integer cardId,
            @NonNull Consumer<? super Throwable> onError
    ) {
        Disposable disposable = getDeckDb().cardRxDao().getCard(cardId)
                .doOnSuccess(card -> {
                    this.card.postValue(card);
                    hideDefinition();
                    setupLearningProgress(cardId, onError);
                })
                .subscribeOn(Schedulers.io())
                .ignoreElement()
                .subscribe(EMPTY_ACTION, onError);
        getCompositeDisposable().add(disposable);
    }

    public void loadCard(
            @NonNull Integer cardId,
            @NonNull Consumer<? super Throwable> onError
    ) {
        Disposable disposable = getDeckDb().cardRxDao().getCard(cardId)
                .doOnSuccess(card -> {
                    this.card.postValue(card);
                    if (cardsViewModel != null && cardsViewModel.isUndoCardProgress(cardId)) {
                        revertPreviousLearningProgress(cardId, onError);
                        showDefinition();
                        cardsViewModel.clearUndoCardProgress();
                    } else {
                        hideDefinition();
                        setupLearningProgress(cardId, onError);
                    }
                })
                .subscribeOn(Schedulers.io())
                .ignoreElement()
                .subscribe(EMPTY_ACTION, onError);
        getCompositeDisposable().add(disposable);
    }

    @NonNull
    public MutableLiveData<Boolean> getShowDefinition() {
        return showDefinition;
    }

    public void showDefinition() {
        showDefinition.postValue(true);
    }
    public void hideDefinition() {
        showDefinition.postValue(false);
    }

    @NonNull
    public MutableLiveData<Card> getCard() {
        return card;
    }

    protected int getCardId() {
        Card card = Objects.requireNonNull(getCard().getValue());
        return Objects.requireNonNull(card.getId());
    }

    public StudyCardsSliderViewModel getCardsViewModel() {
        return cardsViewModel;
    }

    public void setCardsViewModel(@NotNull StudyCardsSliderViewModel cardsViewModel) {
        this.cardsViewModel = cardsViewModel;
    }
}
