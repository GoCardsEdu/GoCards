package pl.gocards.ui.cards.list.learning_progress;

import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.google.android.material.color.MaterialColors;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import pl.gocards.R;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.app.AppConfig;
import pl.gocards.ui.cards.list.search.SearchListCardsAdapter;
import pl.gocards.ui.cards.list.standard.CardViewHolder;

/**
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
public class LearningProgressListCardsAdapter extends SearchListCardsAdapter {

    private Set<Integer> disabledCards;

    private Set<Integer> forgottenCards;

    private Set<Integer> rememberedCards;

    private String leftEdgeBar;

    private String rightEdgeBar;

    public LearningProgressListCardsAdapter(
            @NonNull LearningProgressListCardsActivity activity
    ) throws DatabaseException {
        super(activity);
    }

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    protected @NonNull Completable beforeOnStart() {
        return Completable.mergeArray(
                super.beforeOnStart(),
                loadLeftEdgeBarConfig(),
                loadRightEdgeBarConfig()
        );
    }

    @NonNull
    private Completable loadLeftEdgeBarConfig() {
        return getAppDb().appConfigRxDao().load(
                AppConfig.LEFT_EDGE_BAR,
                AppConfig.LEFT_EDGE_BAR_DEFAULT,
                // Do not use runOnUiThread, otherwise null is read by other Rx.
                s -> leftEdgeBar = s
        ).ignoreElements();
    }

    @NonNull
    private Completable loadRightEdgeBarConfig() {
        return getAppDb().appConfigRxDao().load(
                AppConfig.RIGHT_EDGE_BAR,
                AppConfig.RIGHT_EDGE_BAR_DEFAULT,
                // Do not use runOnUiThread, otherwise null is read by other Rx.
                s -> rightEdgeBar = s
        ).ignoreElements();
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        setCardStatusEdgeBar((LearningProgressViewHolder) holder, position);
    }

    @UiThread
    private void setCardStatusEdgeBar(@NonNull LearningProgressViewHolder holder, int position) {
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(leftEdgeBar)) {
            View view = holder.getLeftEdgeBarView();
            setCardStatusEdgeBar(view, position);
        }
        if (AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(rightEdgeBar)) {
            View view = holder.getRightEdgeBarView();
            setCardStatusEdgeBar(view, position);
        }
    }

    @UiThread
    @SuppressWarnings("UnnecessaryReturnStatement")
    private void setCardStatusEdgeBar(@NonNull View view, int position) {
        int cardId = Objects.requireNonNull(getItem(position).getId());
        view.setBackgroundColor(0);
        if (setBackgroundColorIfContainsCard(view, disabledCards, cardId, R.attr.colorItemDisabledCard)) return;
        if (setBackgroundColorIfContainsCard(view, forgottenCards, cardId, R.attr.colorItemForgottenCard)) return;
        if (setBackgroundColorIfContainsCard(view, rememberedCards, cardId, R.attr.colorItemRememberedCards)) return;
    }

    @UiThread
    protected boolean setBackgroundColorIfContainsCard(
            @NonNull View itemView,
            @Nullable Set<Integer> cards,
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

    @Override
    protected @NonNull Completable beforeLoadItems() {
        return Completable.fromAction(() -> {
            if (isShownLearningStatus()) {
                Completable.mergeArray(
                        super.beforeLoadItems(),
                        loadCardLearningProgress()
                ).blockingSubscribe();
            } else {
                super.beforeLoadItems().blockingSubscribe();
            }
        });
    }

    @NonNull
    private Completable loadCardLearningProgress() {
        return Observable.merge(
                getDisabledCards().toObservable(),
                getForgottenCards().toObservable(),
                getRememberedCards().toObservable()
        ).ignoreElements();
    }

    private boolean isShownLearningStatus() {
        return AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(leftEdgeBar)
                || AppConfig.EDGE_BAR_SHOW_LEARNING_STATUS.equals(rightEdgeBar);
    }

    private Maybe<List<Integer>> getDisabledCards() {
        return getDeckDb().cardRxDao()
                .findIdsByDisabledTrueCards()
                .doOnSuccess(cardIds -> runOnUiThread(() -> disabledCards = new HashSet<>(cardIds)));
    }

    private Maybe<List<Integer>> getForgottenCards() {
        return getDeckDb().cardLearningProgressRxDao()
                .findCardIdsByForgotten()
                .doOnSuccess(cardIds -> runOnUiThread(() -> forgottenCards = new HashSet<>(cardIds)));
    }

    private Maybe<List<Integer>> getRememberedCards() {
        return getDeckDb().cardLearningProgressRxDao()
                .findCardIdsByRemembered()
                .doOnSuccess(cardIds -> runOnUiThread(() -> rememberedCards = new HashSet<>(cardIds)));
    }

    /* -----------------------------------------------------------------------------------------
     * Get/sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public LearningProgressListCardsActivity requireActivity() {
        return (LearningProgressListCardsActivity) super.requireActivity();
    }

    public String getLeftEdgeBar() {
        return leftEdgeBar;
    }

    public String getRightEdgeBar() {
        return rightEdgeBar;
    }
}
