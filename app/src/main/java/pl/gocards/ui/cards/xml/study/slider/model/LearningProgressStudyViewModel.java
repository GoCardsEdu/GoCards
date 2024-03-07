package pl.gocards.ui.cards.xml.study.slider.model;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.room.entity.deck.CardLearningHistory;
import pl.gocards.room.entity.deck.CardLearningProgress;
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory;
import pl.gocards.util.CardReplayScheduler;
import pl.gocards.room.util.TimeUtil;

/**
 * C_R_30 Study the cards
 * @author Grzegorz Ziemski
 */
public class LearningProgressStudyViewModel extends ViewModel {

    @NonNull
    private final CardReplayScheduler cardReplayScheduler = new CardReplayScheduler();
    @Nullable
    private CardLearningProgressAndHistory current;
    @NonNull
    private final MutableLiveData<CardLearningProgressAndHistory> nextAfterAgain = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<CardLearningProgressAndHistory> nextAfterQuick = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<CardLearningProgressAndHistory> nextAfterEasy = new MutableLiveData<>();
    @NonNull
    private final MutableLiveData<CardLearningProgressAndHistory> nextAfterHard = new MutableLiveData<>();

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private AppDatabase appDb;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private String deckDbPath;

    @SuppressLint("CheckResult")
    public void setupLearningProgress(
            @NonNull Integer cardId,
            @NonNull Consumer<? super Throwable> onError
    ) {
        Disposable disposable = deckDb.cardLearningProgressAndHistoryRxDao()
                .findCurrentByCardId(cardId)
                .subscribeOn(Schedulers.io())
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        scheduleFirstReplay(cardId);
                    }
                })
                .doOnSuccess(current -> {
                    this.current = current;
                    this.scheduleNextReplay(current);
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, onError);

        compositeDisposable.add(disposable);
    }

    /**
     * Any grade button has ever been selected for this card yet.
     */
    protected void scheduleFirstReplay(Integer cardId) {
        nextAfterAgain.postValue(cardReplayScheduler.scheduleFirstAgainReplay(cardId));
        nextAfterQuick.postValue(cardReplayScheduler.scheduleNextQuickReplay(cardId));
        nextAfterHard.postValue(cardReplayScheduler.scheduleFirstHardReplay(cardId));
        nextAfterEasy.postValue(cardReplayScheduler.scheduleFirstEasyReplay(cardId));
    }

    /**
     * At least once, the grade button has been selected for this card.
     */
    protected void scheduleNextReplay(@NonNull CardLearningProgressAndHistory current) {
        nextAfterAgain.postValue(cardReplayScheduler.scheduleAgainNextReplay(current));
        scheduleNextReplayAfterQuick(current);
        nextAfterEasy.postValue(cardReplayScheduler.scheduleEasyNextReplay(current));
        nextAfterHard.postValue(cardReplayScheduler.scheduleHardNextReplay(current));
    }

    /**
     * RPL.7 The "Quick Repetition" is clicked and "Again" was clicked previously.
     * <p>
     * The quick can be only used when the card has never been memorized yet.
     */
    protected void scheduleNextReplayAfterQuick(@NonNull CardLearningProgressAndHistory current) {
        boolean isMemorized = current.getProgress().isMemorized();
        boolean emptyNextReplayAt = current.getHistory().getNextReplayAt() == null;
        boolean showOnlyFirstTime = !isMemorized && emptyNextReplayAt && current.getHistory().getReplayId() == 1;

        if (showOnlyFirstTime) {
            nextAfterQuick.postValue(cardReplayScheduler.scheduleNextQuickReplay(current));
        } else {
            nextAfterQuick.postValue(null);
        }
    }

    /**
     * C_U_32 Again
     */
    @NonNull
    public Completable onAgainClick() {
        long now = TimeUtil.getNowEpochSec();
        CardLearningProgressAndHistory next = Objects.requireNonNull(getNextAfterAgain().getValue());
        CardLearningProgressAndHistory current = getCurrent();

        if (current != null) {
            CardLearningHistory previous = cardReplayScheduler.onAgainUpdatePrevious(current, now);
            return updateLearningProgress(previous, next, now);
        } else {
            return updateLearningProgress(null, next, now);
        }
    }

    /**
     * C_U_33 Quick Repetition (5 min)
     */
    @NonNull
    public Completable onQuickClick() {
        long now = TimeUtil.getNowEpochSec();
        CardLearningProgressAndHistory next = Objects.requireNonNull(getNextAfterQuick().getValue());

        return updateLearningProgress(null, next, now);
    }

    /**
     * C_U_35 Easy (5 days)
     */
    @NonNull
    public Completable onEasyClick() {
        long now = TimeUtil.getNowEpochSec();
        CardLearningProgressAndHistory next = Objects.requireNonNull(getNextAfterEasy().getValue());
        CardLearningProgressAndHistory current = getCurrent();

        if (current != null) {
            CardLearningHistory previous = cardReplayScheduler.onEasyUpdatePrevious(current, now);
            return updateLearningProgress(previous, next, now);
        } else {
            return updateLearningProgress(null, next, now);
        }
    }

    /**
     * C_U_34 Hard (3 days)
     */
    @NonNull
    public Completable onHardClick() {
        long now = TimeUtil.getNowEpochSec();
        CardLearningProgressAndHistory next = Objects.requireNonNull(getNextAfterHard().getValue());
        CardLearningProgressAndHistory current = getCurrent();

        if (current != null) {
            CardLearningHistory previous = cardReplayScheduler.onHardUpdatePrevious(current, now);
            return updateLearningProgress(previous, next, now);
        } else {
            return updateLearningProgress(null, next, now);
        }
    }

    @NonNull
    protected Completable updateLearningProgress(
            @Nullable CardLearningHistory previous,
            @NonNull CardLearningProgressAndHistory next,
            long now
    ) {
        return Completable.fromAction(() -> {
            updatePrevious(previous);
            saveNext(next, now);
            appDb.deckDao().refreshLastUpdatedAt(deckDbPath);
        }).subscribeOn(Schedulers.io());
    }

    protected void updatePrevious(@Nullable CardLearningHistory previous) {
        if (previous != null) {
            deckDb.cardLearningHistoryDao().updateAll(previous);
        }
    }

    protected void saveNext(@NonNull CardLearningProgressAndHistory next, long now) {
        CardLearningProgress progress = next.getProgress();
        CardLearningHistory history = next.getHistory();

        if (history.getId() == null) {
            history.setCreatedAt(now);
            long learningHistoryId = deckDb.cardLearningHistoryDao().insert(history);

            progress.setCardLearningHistoryId((int) learningHistoryId);
            updateLearningProgress(progress);
        } else {
            deckDb.cardLearningProgressDao().updateAll(progress);
            deckDb.cardLearningHistoryDao().updateAll(history);
        }
    }

    protected void updateLearningProgress(@NonNull CardLearningProgress current) {
        if (deckDb.cardLearningProgressDao().exists(current.getCardId())) {
            deckDb.cardLearningProgressDao().update(
                    current.getCardId(),
                    current.getCardLearningHistoryId(),
                    current.isMemorized()
            );
        } else {
            deckDb.cardLearningProgressDao().insert(current);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        compositeDisposable.dispose();
    }

    @NonNull
    protected CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    @SuppressWarnings("UnusedReturnValue")
    protected boolean addToDisposable(@NonNull Disposable disposable) {
        return compositeDisposable.add(disposable);
    }

    /**
     * The only click was in again.
     */
    public boolean wasNeverMemorized() {
        return current != null && !current.getProgress().isMemorized()
                && current.getHistory().getInterval() == CardReplayScheduler.AGAIN_FIRST_INTERVAL_MINUTES;
    }

    @NonNull
    public MutableLiveData<CardLearningProgressAndHistory> getNextAfterAgain() {
        return nextAfterAgain;
    }

    @NonNull
    public MutableLiveData<CardLearningProgressAndHistory> getNextAfterQuick() {
        return nextAfterQuick;
    }

    @NonNull
    public MutableLiveData<CardLearningProgressAndHistory> getNextAfterEasy() {
        return nextAfterEasy;
    }

    @NonNull
    public MutableLiveData<CardLearningProgressAndHistory> getNextAfterHard() {
        return nextAfterHard;
    }

    @NonNull
    protected DeckDatabase getDeckDb() {
        return deckDb;
    }

    public void setDeckDb(@NonNull DeckDatabase deckDb) {
        this.deckDb = deckDb;
    }

    public void setAppDb(@NonNull AppDatabase appDb) {
        this.appDb = appDb;
    }

    public void setDeckDbPath(@NonNull String deckDbPath) {
        this.deckDbPath = deckDbPath;
    }

    @Nullable
    public CardLearningProgressAndHistory getCurrent() {
        return current;
    }
}
