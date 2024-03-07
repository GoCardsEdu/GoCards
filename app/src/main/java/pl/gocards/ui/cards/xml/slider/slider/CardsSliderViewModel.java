package pl.gocards.ui.cards.xml.slider.slider;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.room.entity.deck.CardSlider;

/**
 * C_R_22 Swipe the cards left and right
 *
 * @author Grzegorz Ziemski
 */
public class CardsSliderViewModel extends ViewModel {

    private final List<CardSlider> cardSliders = new LinkedList<>();

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;

    @SuppressLint("CheckResult")
    public @NonNull Maybe<List<CardSlider>> loadCards(@NonNull Consumer<? super Throwable> onError) {
        return getDeckDb().cardSliderRxDao().getAllCards()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(this::doOnSuccess)
                .doOnError(onError);
    }

    protected void doOnSuccess(@NonNull List<CardSlider> cardIds) {
        this.cardSliders.clear();
        this.cardSliders.addAll(cardIds);
    }

    protected List<CardSlider> getCards() {
        return cardSliders;
    }

    public int getCardsSize() {
        return cardSliders.size();
    }

    public CardSlider getCard(int index) {
        return cardSliders.get(index);
    }

    @NonNull
    public List<Integer> getCardIds() {
        return cardSliders.stream().map(CardSlider::getId).collect(Collectors.toList());
    }

    @Nullable
    public CardSlider findFirstSavedBefore(int before) {
        List<CardSlider> list = new ArrayList<>(getCards().subList(0, before));
        Collections.reverse(list);

        return list
                .stream()
                .filter(CardSlider::isSaved)
                .findFirst()
                .orElse(null);
    }

    /** @noinspection DataFlowIssue*/
    @Nullable
    public Integer findIndexByCardId(int cardId) {
        Optional<CardSlider> card = cardSliders.stream()
                .filter(it -> cardId == it.getId())
                .findFirst();

        if (card.isPresent()) {
            return cardSliders.indexOf(card.get());
        } else {
            return null;
        }
    }

    @NonNull
    protected DeckDatabase getDeckDb() {
        return deckDb;
    }

    public void setDeckDb(@NonNull DeckDatabase deckDb) {
        this.deckDb = deckDb;
    }
}
