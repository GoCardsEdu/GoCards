package pl.softfly.flashcards.ui.cards.file_sync;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.app.AppConfig;
import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.ui.cards.standard.CardViewHolder;
import pl.softfly.flashcards.ui.cards.learning_progress.LearningProgressViewAdapter;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncCardBaseViewAdapter extends LearningProgressViewAdapter {

    private Set<Integer> recentlyAddedCards;

    private Set<Integer> recentlyUpdatedCards;

    private Set<Integer> recentlyAddedFileCards;

    private Set<Integer> recentlyUpdatedFileCards;

    public FileSyncCardBaseViewAdapter(FileSyncListCardsActivity activity, String deckDbPath)
            throws DatabaseException {
        super(activity, deckDbPath);
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileSyncCardViewHolder(onCreateView(parent), this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        setRecentlySyncedEdgeBar((FileSyncCardViewHolder) holder, position);
    }

    protected void setRecentlySyncedEdgeBar(FileSyncCardViewHolder holder, int position) {
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getLeftEdgeBar())) {
            View view = holder.getLeftEdgeBarView();
            setRecentlySyncedEdgeBar(view, position);
        }
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getRightEdgeBar())) {
            View view = holder.getRightEdgeBarView();
            setRecentlySyncedEdgeBar(view, position);
        }
    }

    protected void setRecentlySyncedEdgeBar(View view, int position) {
        Integer cardId = getItem(position).getId();
        view.setBackgroundColor(0);
        if (setBackgroundColorIfContainsCard(view, recentlyAddedCards, cardId, R.attr.colorItemRecentlyAddedCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyUpdatedCards, cardId, R.attr.colorItemRecentlyUpdatedCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyAddedFileCards, cardId, R.attr.colorItemRecentlyAddedFileCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyUpdatedFileCards, cardId, R.attr.colorItemRecentlyUpdatedFileCard)) return;
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    protected Observable loadItems(ObservableSource next) {
        if (isShownRecentlySynced()) {
            return super.loadItems(
                    Completable
                            .fromObservable(loadRecentlySynced())
                            .andThen(next)
            );
        } else {
           return super.loadItems(next);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Menu actions
     * ----------------------------------------------------------------------------------------- */

    protected Observable<Long> loadRecentlySynced() {
        return getDeckDb().fileSyncedDao().findDeckModifiedAt()
                .doOnSuccess(deckModifiedAt ->
                        Observable.merge(
                                        getRecentlyAddedDeckCards(deckModifiedAt).toObservable(),
                                        getRecentlyUpdatedDeckCards(deckModifiedAt).toObservable(),
                                        getRecentlyAddedFileCards(deckModifiedAt).toObservable(),
                                        getRecentlyUpdatedFileCards(deckModifiedAt).toObservable()
                                )
                                .doOnComplete(getActivity()::refreshMenuOnAppBar)
                                .blockingSubscribe(listMaybe -> {}, this::errorShowRecentlySynced)
                ).toObservable();
    }

    protected boolean isShownRecentlySynced() {
        return AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getLeftEdgeBar())
                || AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getRightEdgeBar());
    }

    protected Maybe<List<Integer>> getRecentlyAddedDeckCards(long createdAt) {
        return getDeckDb().cardDaoAsync().findIdsByCreatedAt(createdAt)
                .doOnSuccess(cardIds -> recentlyAddedCards = new HashSet<>(cardIds));
    }

    protected Maybe<List<Integer>> getRecentlyUpdatedDeckCards(long modifiedAt) {
        return getDeckDb().cardDaoAsync().findIdsByModifiedAtAndCreatedAtNot(modifiedAt)
                .doOnSuccess(cardIds -> recentlyUpdatedCards = new HashSet<>(cardIds));
    }

    protected Maybe<List<Integer>> getRecentlyAddedFileCards(long fileSyncCreatedAt) {
        return getDeckDb().cardDaoAsync().findIdsByFileSyncCreatedAt(fileSyncCreatedAt)
                .doOnSuccess(cardIds -> recentlyAddedFileCards = new HashSet<>(cardIds));
    }

    protected Maybe<List<Integer>> getRecentlyUpdatedFileCards(long fileSyncModifiedAt) {
        return getDeckDb().cardDaoAsync().findIdsByFileSyncModifiedAtAndFileSyncCreatedAtNot(fileSyncModifiedAt)
                .doOnSuccess(cardIds -> recentlyUpdatedFileCards = new HashSet<>(cardIds));
    }

    protected void errorShowRecentlySynced(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName() + "_OnClickShowRecentlySynced",
                "Error while showing recently synced."
        );
    }

    public boolean isShowedRecentlySynced() {
        return AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getLeftEdgeBar())
                || AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getRightEdgeBar());
    }

    public boolean isShowedRecentlySynced(int position) {
        Card card = getItem(position);
        Integer cardId = card.getId();
        return (recentlyAddedCards != null && recentlyAddedCards.contains(cardId))
                || (recentlyUpdatedCards != null && recentlyUpdatedCards.contains(cardId))
                || (recentlyAddedFileCards != null && recentlyAddedFileCards.contains(cardId))
                || (recentlyUpdatedFileCards != null && recentlyUpdatedFileCards.contains(cardId));
    }

    @Override
    public FileSyncListCardsActivity getActivity() {
        return (FileSyncListCardsActivity) super.getActivity();
    }
}
