package pl.gocards.util;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.nio.file.Path;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.util.TimeUtil;

/**
 * @author Grzegorz Ziemski
 */
public class CreateSampleDeck {

    private static final String TAG = "CreateSampleDeck";

    private static final String DECK_NAME = "Sample Deck";

    private static final String[][] SAMPLE_DECK = {
            {
                    "What has 13 hearts, but no other organs?",
                    "A deck of cards."
            },
            {
                    "What building has the most stories?",
                    "The public library."
            },
            {
                    "Why did the spider get a job in I.T.?",
                    "He was a great web designer."
            },
            {
                    "I follow you all the time and copy your every move, but you can’t touch me or catch me. What am I?",
                    "Your shadow."
            },
            {
                    "What is easier to get into than out of?",
                    "Trouble."
            },
            {
                    "What is always right in front of you, but you cannot see it?",
                    "The future."
            },
            {
                    "When does a British potato change its nationality?",
                    "When it becomes a french fries."
            },
            {
                    "What is blue and not very heavy?",
                    "Light blue."
            },
            {
                    "What do you call a witch that lives on the beach?",
                    "Sand-witch"
            },
            {
                    "Why did Mickey Mouse go to space?",
                    "To look for Pluto!"
            },
            {
                    "When is the moon heaviest?",
                    "When it is full."
            },
            {
                    "Why does Voldemort prefer Twitter over Facebook?",
                    "He has only followers, not friends."
            },
            {
                    "Why did Robin Hood steal from the rich?",
                    "The poor didn't have anything worth stealing!"
            },
            {
                    "Why can not Cinderella play soccer?",
                    "She is always running away from the ball!"
            },
            {
                    "Why is Peter Pan flying all the time?",
                    "He Neverlands!"
            },
            {
                    "What does the Loch Ness monster eat?",
                    "Fish and Ships"
            },
            {
                    "How does every English joke start?",
                    "By looking over your shoulder."
            },
            {
                    "How do you measure a snake?",
                    "In inches — they don’t have feet."
            },
            {
                    "Where should you go in the room if you are feeling cold?",
                    "The corner — they are usually 90 degrees."
            },
            {
                    "Why don’t blind people skydive?",
                    "It scares their dogs."
            },
            {
                    "What kind of shoes does a spy wear?",
                    "Sneakers"
            },
            {
                    "What goes up but never comes back down?",
                    "Your age."
            },
            {
                    "What does nobody want, yet nobody wants to lose?",
                    "Work."
            },
            {
                    "If I have it, I don’t share it.  If I share it, I don’t have it. What is it?",
                    "A secret."
            },
            {
                    "What has no beginning, end, or middle?",
                    "A circle."
            },
            {
                    "What type of music do rabbits like?",
                    "Hip Hop!"
            },
            {
                    "What do you call a bear with no teeth?",
                    "A gummy bear!"
            },
            {
                    "Why are spiders so smart??",
                    "They can find everything on the web."
            },
            {
                    "What do you call two suns fighting each other?",
                    "Star Wars"
            },
            {
                    "Which fruit is always sad?",
                    "A blueberry."
            }
    };

    @SuppressLint("CheckResult")
    public void create(
            @NonNull Context applicationContext,
            @NonNull Path folder,
            @NonNull Action doOnComplete,
            @NonNull FragmentActivity activity,
            @NonNull CompositeDisposable activityDisposable
    ) throws DatabaseException {

        AppDeckDbUtil deckDbUtil = AppDeckDbUtil
                .getInstance(applicationContext);

        String deckDbPath = folder + "/" + DECK_NAME + ".db";
        deckDbPath = deckDbUtil.findFreePath(deckDbPath);

        DeckDatabase deckDb = createDatabase(applicationContext, deckDbPath);

        Card[] cards = new Card[SAMPLE_DECK.length];
        long updatedAt = TimeUtil.getNowEpochSec();

        for (int i = 0; i < SAMPLE_DECK.length; i++) {
            String[] sampleCard = SAMPLE_DECK[i];
            Card card = new Card();
            card.setOrdinal(i + 1);
            Card.Companion.setTerm(card, sampleCard[0]);
            Card.Companion.setDefinition(card, sampleCard[1]);
            card.setCreatedAt(updatedAt);
            card.setUpdatedAt(updatedAt);
            cards[i] = card;
        }

        Disposable disposable = deckDb.cardRxDao().insertAll(cards)
                .subscribeOn(Schedulers.io())
                .doOnComplete(doOnComplete)
                .subscribe(EMPTY_ACTION, e -> this.onErrorCreateSampleDeck(e, activity));
        activityDisposable.add(disposable);

        FirebaseAnalyticsHelper
                .getInstance(applicationContext)
                .createSampleDeck();
    }

    protected void onErrorCreateSampleDeck(
            @NonNull Throwable e,
            @NonNull FragmentActivity activity
    ) {
        getExceptionHandler().handleException(
                e, activity, TAG,
                "Error while creating sample deck."
        );
    }

    @NonNull
    protected DeckDatabase createDatabase(
            @NonNull Context applicationContext,
            @NonNull String dbPath
    ) throws DatabaseException {
        return AppDeckDbUtil
                .getInstance(applicationContext)
                .createDatabase(applicationContext, dbPath);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

}
