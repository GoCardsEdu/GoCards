package pl.gocards.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.ZonedDateTime;
import java.util.Date;

import pl.gocards.room.entity.deck.CardLearningHistory;
import pl.gocards.room.entity.deck.CardLearningProgress;
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory;
import pl.gocards.room.util.TimeUtil;

/**
 * Available grades:
 * 1) Again
 *    If the card is forgotten, the {@link CardLearningHistory#interval} is divided by 2.
 * 2) Quick
 *    The quick can only be used, only the card has never been remembered yet.
 * 3) Hard
 *    The interval is multiplied by 150%
 * 4) Easy
 *    The interval is multiplied by 200%
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocReference")
public class CardReplayScheduler {

    /**
     * If again was selected first, the other grades have 1 day.
     * 2 days * 0.50 ({@link CardReplayScheduler#DECREASE_IF_FORGOTTEN_PERCENT}) = 1 day
     */
    public static final int AGAIN_FIRST_INTERVAL_MINUTES = 2 * 24 * 60;

    /**
     * First interval if memorized:
     * 5 minutes * {@link CardReplayScheduler#QUICK_FACTOR_PERCENT} = 5 minutes
     */
    private static final int QUICK_FIRST_INTERVAL_MINUTES = 5;

    /**
     * First interval if memorized:
     * 2 days * {@link CardReplayScheduler#HARD_FACTOR_PERCENT} = 3 days
     */
    private static final int HARD_FIRST_INTERVAL_MINUTES = 2 * 24 * 60;

    /**
     * First interval if memorized:
     * 2.5 days * {@link CardReplayScheduler#EASY_FACTOR_PERCENT} = 5 days
     */
    private static final int EASY_FIRST_INTERVAL_MINUTES = (2 * 24 * 60) + (12 * 60);

    private static final int QUICK_FACTOR_PERCENT = 100;

    private static final int HARD_FACTOR_PERCENT = 150;

    private static final int EASY_FACTOR_PERCENT = 200;

    private static final int DECREASE_IF_FORGOTTEN_PERCENT = 50;

    /* -----------------------------------------------------------------------------------------
     * No grading button has been clicked yet:
     * ----------------------------------------------------------------------------------------- */

    /**
     * RPL.1 The "Again" is clicked and no button has been clicked yet.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleFirstAgainReplay(int cardId) {
        return createFirstNotMemorizedReplay(cardId);
    }

    /**
     * RPL.2 The "Quick Repetition (5 min)" is clicked and no button has been clicked yet.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleNextQuickReplay(int cardId) {
        return createFirstMemorizedReplay(
                cardId,
                QUICK_FIRST_INTERVAL_MINUTES,
                QUICK_FACTOR_PERCENT
        );
    }

    /**
     * RPL.3 The "Hard (3 days)" is clicked for the first time.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleFirstHardReplay(int cardId) {
        return createFirstMemorizedReplay(
                cardId,
                HARD_FIRST_INTERVAL_MINUTES,
                HARD_FACTOR_PERCENT
        );
    }

    /**
     * RPL.4 The "Easy (5 days)" is clicked for the first time.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleFirstEasyReplay(int cardId) {
        return createFirstMemorizedReplay(
                cardId,
                EASY_FIRST_INTERVAL_MINUTES,
                EASY_FACTOR_PERCENT
        );
    }

    @NonNull
    protected CardLearningProgressAndHistory createFirstNotMemorizedReplay(int cardId) {
        CardLearningHistory history = new CardLearningHistory();
        history.setId(null);
        history.setCardId(cardId);
        history.setWasMemorized(null);
        history.setInterval(AGAIN_FIRST_INTERVAL_MINUTES);
        history.setNextReplayAt(null);
        history.setReplayId(1);
        history.setCountMemorized(1);
        history.setCountNotMemorized(1);

        CardLearningProgress progress = new CardLearningProgress();
        progress.setCardId(cardId);
        progress.setMemorized(false);
        return new CardLearningProgressAndHistory(progress, history);
    }

    @NonNull
    protected CardLearningProgressAndHistory createFirstMemorizedReplay(
            int cardId,
            int defaultStartInterval,
            int factor
    ) {
        int newInterval = calcNewInterval(defaultStartInterval, factor);

        CardLearningHistory history = new CardLearningHistory();
        history.setId(null);
        history.setCardId(cardId);
        history.setWasMemorized(null);
        history.setInterval(newInterval);
        history.setNextReplayAt(calcNewReplayAt(newInterval));
        history.setReplayId(1);
        history.setCountMemorized(1);
        history.setCountNotMemorized(0);

        CardLearningProgress progress = new CardLearningProgress();
        progress.setCardId(cardId);
        progress.setMemorized(true);

        return new CardLearningProgressAndHistory(progress, history);
    }

    /* -----------------------------------------------------------------------------------------
     * One of the grading buttons has been clicked earlier:
     * ----------------------------------------------------------------------------------------- */

    /**
     * RPL.5 The "Again" is clicked and the "Again" was clicked previously.
     * RPL.6 The "Again" is clicked and "Quick", "Hard" or "Easy" was clicked previously.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleAgainNextReplay(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        return progressAndHistory.getProgress().isMemorized() ?
                createNotMemorized(progressAndHistory) // RPL.6
                : clone(progressAndHistory); // RPL.5 Do not change anything.
    }

    /**
     * RPL.5 The "Again" is clicked and the "Again" was clicked previously.
     * RPL.6 The "Again" is clicked and "Quick", "Hard" or "Easy" was clicked previously.
     */
    @Nullable
    public CardLearningHistory onAgainUpdatePrevious(
            @NonNull CardLearningProgressAndHistory progressAndHistory,
            long now
    ) {
        if (progressAndHistory.getProgress().isMemorized()) { // RPL.6
            CardLearningHistory previous = clone(progressAndHistory.getHistory());
            previous.setWasMemorized(false);
            previous.setMemorizedDuration(now - previous.getCreatedAt());
            return previous;
        } else {
            return null; // RPL.5 Do not change anything.
        }
    }

    /**
     * RPL.7 The "Quick Repetition" is clicked and "Again" was clicked previously.
     * The "Quick Repetition" can be used only when the card has never been memorized yet.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleNextQuickReplay(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        if (progressAndHistory.getProgress().isMemorized()
                || progressAndHistory.getHistory().getNextReplayAt() != null) {
            throw new UnsupportedOperationException("The quick can only be used, only the card has never been memorized yet.");
        } else {
            return updateNotMemorized(progressAndHistory, QUICK_FIRST_INTERVAL_MINUTES);
        }
    }

    /**
     * RPL.8 The "Hard" is clicked and the "Again" was clicked previously.
     * RPL.9 The "Hard" is clicked and "Quick", "Hard" or "Easy" was clicked previously.
     */
    @NonNull
    public CardLearningProgressAndHistory scheduleHardNextReplay(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        if (progressAndHistory.getProgress().isMemorized()) {
            int newInterval = calcNewInterval(progressAndHistory.getHistory().getInterval(), HARD_FACTOR_PERCENT);
            return createMemorized(progressAndHistory, newInterval);
        } else {
            int newInterval = calcNewIntervalForgotten(progressAndHistory.getHistory());
            return updateNotMemorized(progressAndHistory, newInterval);
        }
    }

    /**
     * RPL.9 The "Hard" is clicked and "Quick", "Hard" or "Easy" was clicked previously.
     */
    @Nullable
    public CardLearningHistory onHardUpdatePrevious(
            @NonNull CardLearningProgressAndHistory progressAndHistory,
            long now
    ) {
        return onEasyUpdatePrevious(progressAndHistory, now);
    }

    @NonNull
    public CardLearningProgressAndHistory scheduleEasyNextReplay(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        if (progressAndHistory.getProgress().isMemorized()) {
            long memorizedDuration = secondsToMinutes(TimeUtil.getNowEpochSec() - progressAndHistory.getHistory().getCreatedAt());
            int newInterval = calcNewInterval((int) memorizedDuration, EASY_FACTOR_PERCENT);
            return createMemorized(progressAndHistory, newInterval);
        } else {
            int newInterval = calcNewIntervalForgotten(progressAndHistory.getHistory());
            return updateNotMemorized(progressAndHistory, newInterval);
        }
    }

    protected long secondsToMinutes(long seconds) {
        return seconds / 60;
    }

    /**
     * RPL.11 The "Easy" is clicked and "Quick", "Hard" or "Easy" was clicked previously.
     */
    @Nullable
    public CardLearningHistory onEasyUpdatePrevious(
            @NonNull CardLearningProgressAndHistory progressAndHistory,
            long now
    ) {
        if (progressAndHistory.getProgress().isMemorized()) { // RPL.11
            CardLearningHistory previous = clone(progressAndHistory.getHistory());
            previous.setWasMemorized(true);
            previous.setMemorizedDuration(now - previous.getCreatedAt());
            return previous;
        } else { // RPL.10
            return null; // Already updated by Again
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected CardLearningProgressAndHistory updateNotMemorized(
            @NonNull CardLearningProgressAndHistory progressAndHistory,
            int interval
    ) {
        CardLearningHistory currentHistory = progressAndHistory.getHistory();
        CardLearningProgress currentProgress = progressAndHistory.getProgress();

        CardLearningHistory nextHistory = clone(currentHistory);
        nextHistory.setInterval(interval);
        nextHistory.setNextReplayAt(calcNewReplayAt(interval));

        CardLearningProgress nextProgress = clone(progressAndHistory.getProgress());
        nextProgress.setCardId(currentProgress.getCardId());
        nextProgress.setMemorized(true);
        return new CardLearningProgressAndHistory(nextProgress, nextHistory);
    }

    @NonNull
    protected CardLearningProgressAndHistory createNotMemorized(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        CardLearningHistory currentHistory = progressAndHistory.getHistory();
        CardLearningProgress currentProgress = progressAndHistory.getProgress();

        CardLearningHistory nextHistory = createNextReplay(currentHistory);
        nextHistory.setInterval(currentHistory.getInterval());
        nextHistory.setNextReplayAt(null);
        nextHistory.setCountMemorized(1);
        nextHistory.setCountNotMemorized(currentHistory.getCountNotMemorized() + 1);

        CardLearningProgress nextProgress = new CardLearningProgress();
        nextProgress.setCardId(currentProgress.getCardId());
        nextProgress.setMemorized(false);
        return new CardLearningProgressAndHistory(nextProgress, nextHistory);
    }

    @NonNull
    protected CardLearningProgressAndHistory createMemorized(
            @NonNull CardLearningProgressAndHistory progressAndHistory,
            int interval
    ) {
        CardLearningHistory currentHistory = progressAndHistory.getHistory();
        CardLearningProgress currentProgress = progressAndHistory.getProgress();

        CardLearningHistory nextHistory = createNextReplay(currentHistory);

        nextHistory.setInterval(interval);
        nextHistory.setNextReplayAt(calcNewReplayAt(interval));
        nextHistory.setCountMemorized(currentHistory.getCountMemorized() + 1);
        nextHistory.setCountNotMemorized(currentHistory.getCountNotMemorized());

        CardLearningProgress nextProgress = new CardLearningProgress();
        nextProgress.setCardId(currentProgress.getCardId());
        nextProgress.setMemorized(true);
        return new CardLearningProgressAndHistory(nextProgress, nextHistory);
    }

    @NonNull
    protected CardLearningHistory createNextReplay(@NonNull CardLearningHistory current) {
        CardLearningHistory next = new CardLearningHistory();
        next.setId(null);
        next.setCardId(current.getCardId());
        next.setReplayId(current.getReplayId() + 1);
        return next;
    }

    @NonNull
    protected CardLearningProgressAndHistory clone(
            @NonNull CardLearningProgressAndHistory progressAndHistory
    ) {
        return new CardLearningProgressAndHistory(
                clone(progressAndHistory.getProgress()),
                clone(progressAndHistory.getHistory())
        );
    }

    @NonNull
    protected CardLearningProgress clone(@NonNull CardLearningProgress current) {
        CardLearningProgress next = new CardLearningProgress();
        next.setCardId(current.getCardId());
        next.setCardLearningHistoryId(current.getCardLearningHistoryId());
        next.setMemorized(current.isMemorized());
        return next;
    }

    @NonNull
    protected CardLearningHistory clone(@NonNull CardLearningHistory current) {
        CardLearningHistory next = new CardLearningHistory();
        next.setId(current.getId());
        next.setCardId(current.getCardId());
        next.setWasMemorized(current.getWasMemorized());

        next.setInterval(current.getInterval());
        next.setNextReplayAt(current.getNextReplayAt());
        next.setReplayId(current.getReplayId());
        next.setCountMemorized(current.getCountMemorized());
        next.setCountNotMemorized(current.getCountNotMemorized());
        next.setCreatedAt(current.getCreatedAt());
        next.setMemorizedDuration(current.getMemorizedDuration());
        return next;
    }

    protected Date calcNewReplayAt(int minutes) {
        return Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant());
    }

    protected int calcNewInterval(int previousInterval, int factor) {
        return (int) (previousInterval * (factor / 100f));
    }

    /**
     * If the card is forgotten, the {@link CardLearningHistory#interval} is divided by 2.
     */
    protected int calcNewIntervalForgotten(@NonNull CardLearningHistory learningProgress) {
        return Math.max(
                QUICK_FIRST_INTERVAL_MINUTES,
                (int) (learningProgress.getInterval() * (DECREASE_IF_FORGOTTEN_PERCENT / 100f))
        );
    }
}
