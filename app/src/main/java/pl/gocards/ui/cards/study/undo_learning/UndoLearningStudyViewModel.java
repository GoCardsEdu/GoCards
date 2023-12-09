package pl.gocards.ui.cards.study.undo_learning;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;
import static pl.gocards.util.CardReplayScheduler.AGAIN_FIRST_INTERVAL_MINUTES;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.entity.deck.CardLearningHistory;
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory;
import pl.gocards.ui.cards.study.slider.model.LearningProgressStudyViewModel;

/**
 * @author Grzegorz Ziemski
 */
public class UndoLearningStudyViewModel extends LearningProgressStudyViewModel {

    /**
     * This allows to undo the first click again, only if it happened during the current session.
     */
    public final List<Integer> forgottenInThisSession = new LinkedList<>();

    @NonNull
    public Completable onAgainClick() {
        CardLearningProgressAndHistory current = getCurrent();
        if (current == null) {
            CardLearningProgressAndHistory next = Objects.requireNonNull(getNextAfterAgain().getValue());
            forgottenInThisSession.add(next.getProgress().getCardId());
        } else if (current.getProgress().isMemorized()) {
            forgottenInThisSession.add(current.getProgress().getCardId());
        }
        return super.onAgainClick();
    }

    @SuppressLint("CheckResult")
    protected void revertPreviousLearningProgress(
            int cardId, @NonNull Consumer<? super Throwable> onError
    ) {
        Disposable disposable = getDeckDb().cardLearningHistoryRxDao()
                .findCurrentByCardId(cardId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(current -> {
                    CardLearningHistory previous = findPreviousLearningHistory(current);
                    if (previous != null) {
                        revertPrevious(current, previous);
                    } else {
                        revertFirst(current);
                    }
                    setupLearningProgress(cardId, onError);
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, onError);
        addToDisposable(disposable);
    }

    protected void revertFirst(@NonNull CardLearningHistory current) {
        if (current.getCountNotMemorized() == 1) {
            revertFirstNoMemorized(current);
        } else {
            revertFirstMemorized(current);
        }
    }

    protected void revertFirstNoMemorized(@NonNull CardLearningHistory current) {
        if (forgottenInThisSession.contains(current.getCardId())) {
            revertFirstMemorized(current);
        } else {
            getDeckDb().cardLearningProgressDao().updateIsMemorized(current.getCardId(), false);
            current.setInterval(AGAIN_FIRST_INTERVAL_MINUTES);
            getDeckDb().cardLearningHistoryDao().update(current);
        }
    }

    protected void revertFirstMemorized(@NonNull CardLearningHistory current) {
        getDeckDb().cardLearningProgressDao().deleteByCardId(current.getCardId());
        deleteLearningHistory(current);
    }

    protected void revertPrevious(@NonNull CardLearningHistory current, @NonNull CardLearningHistory previous) {
        if (!Objects.requireNonNull(previous.getWasMemorized())) {
            revertPreviousNoMemorized(current, previous);
        } else {
            revertPreviousMemorized(current, previous);
        }
    }

    /**
     * Restores when "Again" has been clicked
     */
    protected void revertPreviousNoMemorized(@NonNull CardLearningHistory current, @NonNull CardLearningHistory previous) {
        if (forgottenInThisSession.contains(current.getCardId())) {
            revertPreviousMemorized(current, previous);
        } else {
            getDeckDb().cardLearningProgressDao().updateIsMemorized(current.getCardId(), false);
            current.setInterval(previous.getInterval());
            getDeckDb().cardLearningHistoryDao().update(current);
        }
    }

    protected void revertPreviousMemorized(CardLearningHistory current, @NonNull CardLearningHistory previous) {
        setPreviousLearningHistoryAsCurrent(previous);
        deleteLearningHistory(current);
    }

    @Nullable
    private CardLearningHistory findPreviousLearningHistory(@NonNull CardLearningHistory current) {
        return getDeckDb().cardLearningHistoryDao()
                .findByCardIdAndLearningHistoryId(
                        current.getCardId(),
                        current.getReplayId() - 1
                );
    }

    private void setPreviousLearningHistoryAsCurrent(@NonNull CardLearningHistory previous) {
        getDeckDb().cardLearningProgressDao().update(
                previous.getCardId(),
                Objects.requireNonNull(previous.getId()),
                true
        );
        previous.setWasMemorized(null);
        previous.setMemorizedDuration(null);
        getDeckDb().cardLearningHistoryDao().update(previous);
    }

    private void deleteLearningHistory(CardLearningHistory current) {
        getDeckDb().cardLearningHistoryDao().delete(current);
    }
}
