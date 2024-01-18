package pl.gocards.ui.cards.list.file_sync;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

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
import pl.gocards.ui.cards.list.learning_progress.LearningProgressListCardsAdapter;
import pl.gocards.ui.cards.list.standard.CardViewHolder;

/**
 * C_R_06 Show card last sync status: added, updated on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
public class FileSyncListCardsAdapter extends LearningProgressListCardsAdapter {

    private Set<Integer> recentlyAddedCards;

    private Set<Integer> recentlyUpdatedCards;

    private Set<Integer> recentlyAddedFileCards;

    private Set<Integer> recentlyUpdatedFileCards;

    public FileSyncListCardsAdapter(@NonNull FileSyncListCardsActivity activity) throws DatabaseException {
        super(activity);
    }

    @NonNull
    @Override
    protected Completable afterOnStart() {
        return Completable.mergeArray(
                super.afterOnStart(),
                Completable.fromAction(() -> requireActivity().autoSyncOnCreate()),
                Completable.fromAction(() -> requireActivity().observeEditingBlockedAt())
        );
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

    /**
     * C_R_06 Show card last sync status: added, updated on the right/left edge bar.
     */
    private void setRecentlySyncedEdgeBar(@NonNull FileSyncCardViewHolder holder, int position) {
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getLeftEdgeBar())) {
            View view = holder.getLeftEdgeBarView();
            setRecentlySyncedEdgeBar(view, position);
        }
        if (AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getRightEdgeBar())) {
            View view = holder.getRightEdgeBarView();
            setRecentlySyncedEdgeBar(view, position);
        }
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    private void setRecentlySyncedEdgeBar(@NonNull View view, int position) {
        int cardId = Objects.requireNonNull(getItem(position).getId());
        view.setBackgroundColor(0);
        if (setBackgroundColorIfContainsCard(view, recentlyAddedCards, cardId, R.attr.colorItemRecentlyAddedDeckCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyUpdatedCards, cardId, R.attr.colorItemRecentlyUpdatedDeckCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyAddedFileCards, cardId, R.attr.colorItemRecentlyAddedFileCard)) return;
        if (setBackgroundColorIfContainsCard(view, recentlyUpdatedFileCards, cardId, R.attr.colorItemRecentlyUpdatedFileCard)) return;
    }

    /* -----------------------------------------------------------------------------------------
     * Recently Synced
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected @NonNull Completable beforeLoadItems() {
        if (isShownRecentlySynced()) {
            return Completable.mergeArray(super.beforeLoadItems(), loadRecentlySynced());
        } else {
            return super.beforeLoadItems();
        }
    }

    private @NonNull Completable loadRecentlySynced() {
        return getDeckDb().fileSyncedRxDao().findDeckModifiedAt()
                .doOnSuccess(deckModifiedAt ->
                        Observable.merge(
                                        getRecentlyAddedDeckCards(deckModifiedAt).toObservable(),
                                        getRecentlyUpdatedDeckCards(deckModifiedAt).toObservable(),
                                        getRecentlyAddedFileCards(deckModifiedAt).toObservable(),
                                        getRecentlyUpdatedFileCards(deckModifiedAt).toObservable()
                                )
                                .doOnComplete(() -> runOnUiThread(() -> this.requireActivity().refreshMenuOnAppBar()))
                                .blockingSubscribe(listMaybe -> {}, this::errorShowRecentlySynced)
                ).toObservable().ignoreElements();
    }

    private boolean isShownRecentlySynced() {
        return AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getLeftEdgeBar())
                || AppConfig.EDGE_BAR_SHOW_RECENTLY_SYNCED.equals(getRightEdgeBar());
    }

    private Maybe<List<Integer>> getRecentlyAddedDeckCards(long createdAt) {
        return getDeckDb().cardRxDao().findIdsByCreatedAt(createdAt)
                .doOnSuccess(
                        cardIds -> runOnUiThread(
                                () -> recentlyAddedCards = new HashSet<>(cardIds),
                                this::errorShowRecentlySynced
                        )
                );
    }

    private Maybe<List<Integer>> getRecentlyUpdatedDeckCards(long updatedAt) {
        return getDeckDb().cardRxDao().findIdsByModifiedAtAndCreatedAtNot(updatedAt)
                .doOnSuccess(
                        cardIds -> runOnUiThread(
                                () -> recentlyUpdatedCards = new HashSet<>(cardIds),
                                this::errorShowRecentlySynced
                        )
                );
    }

    private Maybe<List<Integer>> getRecentlyAddedFileCards(long fileSyncCreatedAt) {
        return getDeckDb().cardRxDao().findIdsByFileSyncCreatedAt(fileSyncCreatedAt)
                .doOnSuccess(
                        cardIds -> runOnUiThread(
                                () -> recentlyAddedFileCards = new HashSet<>(cardIds),
                                this::errorShowRecentlySynced
                        )
                );
    }

    private Maybe<List<Integer>> getRecentlyUpdatedFileCards(long fileSyncModifiedAt) {
        return getDeckDb().cardRxDao().findIdsByFileSyncModifiedAtAndFileSyncCreatedAtNot(fileSyncModifiedAt)
                .doOnSuccess(
                        cardIds -> runOnUiThread(
                                () -> recentlyUpdatedFileCards = new HashSet<>(cardIds),
                                this::errorShowRecentlySynced
                        )
                );
    }

    private void errorShowRecentlySynced(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireActivity(), "Error while showing recently synced.");
    }

    @NonNull
    @Override
    public FileSyncListCardsActivity requireActivity() {
        return (FileSyncListCardsActivity) super.requireActivity();
    }
}
