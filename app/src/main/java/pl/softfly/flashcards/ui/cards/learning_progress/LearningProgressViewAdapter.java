package pl.softfly.flashcards.ui.cards.learning_progress;

import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.app.AppConfig;
import pl.softfly.flashcards.ui.cards.select.SelectCardBaseViewAdapter;
import pl.softfly.flashcards.ui.cards.standard.CardViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class LearningProgressViewAdapter extends SelectCardBaseViewAdapter {

    private Set<Integer> disabledCards;

    private Set<Integer> forgottenCards;

    private Set<Integer> rememberedCards;

    private String leftEdgeBar;

    private String rightEdgeBar;

    public LearningProgressViewAdapter(LearningProgressListCardsActivity activity, String deckDbPath)
            throws DatabaseException {
        super(activity, deckDbPath);
    }

    @Override
    public void init() {
        Completable.fromObservable(Observable.merge(loadLeftEdgeBar(), loadRightEdgeBar()))
                .subscribe(() -> loadItems());
    }

    protected Observable<AppConfig> loadLeftEdgeBar() {
        return getAppDb().appConfigAsync().load(AppConfig.LEFT_EDGE_BAR, AppConfig.LEFT_EDGE_BAR_DEFAULT, s -> leftEdgeBar = s);
    }

    protected Observable<AppConfig> loadRightEdgeBar() {
        return getAppDb().appConfigAsync().load(AppConfig.RIGHT_EDGE_BAR, AppConfig.RIGHT_EDGE_BAR_DEFAULT, s -> rightEdgeBar = s);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        setCardStatusEdgeBar((LearningProgressViewHolder) holder, position);
    }

    public void setCardStatusEdgeBar(@NonNull LearningProgressViewHolder holder, int position) {
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(leftEdgeBar)) {
            View view = holder.getLeftEdgeBarView();
            setCardStatusEdgeBar(view, position);
        }
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(rightEdgeBar)) {
            View view = holder.getRightEdgeBarView();
            setCardStatusEdgeBar(view, position);
        }
    }

    public void setCardStatusEdgeBar(View view, int position) {
        Integer cardId = getItem(position).getId();
        view.setBackgroundColor(0);
        if (setBackgroundColorIfContainsCard(view, disabledCards, cardId, R.attr.colorItemDisabledCard)) return;
        if (setBackgroundColorIfContainsCard(view, forgottenCards, cardId, R.attr.colorItemForgottenCard)) return;
        if (setBackgroundColorIfContainsCard(view, rememberedCards, cardId, R.attr.colorItemRememberedCards)) return;
    }

    protected boolean setBackgroundColorIfContainsCard(
            View itemView,
            Set<Integer> cards,
            int cardId,
            @AttrRes int colorAttributeResId
    ) {
        if (cards != null && cards.contains(cardId)) {
            itemView.setBackgroundColor(MaterialColors.getColor(itemView, colorAttributeResId));
            return true;
        }
        return false;
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    protected Observable loadItems(ObservableSource next) {
        if (isShownLearningStatus()) {
            return super.loadItems(
                    Completable.fromObservable(loadCardLearningProgress())
                            .andThen(next)
            );
        } else {
            return super.loadItems(next);
        }
    }

    protected Observable<List<Integer>> loadCardLearningProgress() {
        return Observable.merge(
                getDisabledCards().toObservable(),
                getForgottenCards().toObservable(),
                getRememberedCards().toObservable()
        );
    }

    protected boolean isShownLearningStatus() {
        return AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(leftEdgeBar)
                || AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(rightEdgeBar);
    }

    protected Maybe<List<Integer>> getDisabledCards() {
        return getDeckDb().cardDaoAsync().findIdsByDisabledTrueCards()
                .doOnSuccess(cardIds -> disabledCards = new HashSet<>(cardIds));
    }

    protected Maybe<List<Integer>> getForgottenCards() {
        return getDeckDb().cardLearningProgressAsyncDao().findCardIdsByForgotten()
                .doOnSuccess(cardIds -> forgottenCards = new HashSet<>(cardIds));
    }

    protected Maybe<List<Integer>> getRememberedCards() {
        return getDeckDb().cardLearningProgressAsyncDao().findCardIdsByRemembered()
                .doOnSuccess(cardIds -> rememberedCards = new HashSet<>(cardIds));
    }



    @Override
    public LearningProgressListCardsActivity getActivity() {
        return (LearningProgressListCardsActivity) super.getActivity();
    }

    public String getLeftEdgeBar() {
        return leftEdgeBar;
    }

    public String getRightEdgeBar() {
        return rightEdgeBar;
    }
}
