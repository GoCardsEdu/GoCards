package pl.gocards.util;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.ref.WeakReference;

/**
 * @author Grzegorz Ziemski
 */
public class FirebaseAnalyticsHelper {

    @NonNull
    public static final String CREATE_SAMPLE_DECK = "create_sample_deck";

    @NonNull
    public static final String CREATE_DECK = "create_deck";

    @NonNull
    public static final String EXPORT_DECK = "export_deck";

    @NonNull
    public static final String IMPORT_DECK = "import_deck";

    @NonNull
    public static final String SYNC_DECK = "sync_deck";

    @NonNull
    public static final String CREATE_CARD = "create_card";

    @NonNull
    public static final String CLICK_AGAIN = "click_again";

    @NonNull
    public static final String CLICK_QUICK = "click_quick";

    @NonNull
    public static final String CLICK_EASY = "click_easy";

    @NonNull
    public static final String CLICK_HARD = "click_hard";

    @NonNull
    public static final String MENU_OPEN_DISCORD = "menu_open_discord";

    @NonNull
    public static final String DISCOVER_OPEN_DISCORD = "discover_open_discord";

    @NonNull
    public static final String DISCOVER_OPEN_FANPAGE = "discover_open_discord";

    @NonNull
    public static final String DISCOVER_OPEN_REVIEW = "discover_open_review";

    @NonNull
    public static final String DISCOVER_OPEN_REVIEW_IN_APP = "discover_open_review_in_app";

    @NonNull
    public static final String DISCOVER_OPEN_REVIEW_FULL_PAGE = "discover_open_review_full_page";

    @NonNull
    private final FirebaseAnalytics firebaseAnalytics;

    @Nullable
    private static WeakReference<FirebaseAnalyticsHelper> INSTANCE;

    private FirebaseAnalyticsHelper(@NonNull Context context) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static synchronized FirebaseAnalyticsHelper getInstance(@NonNull Context context) {
        if (INSTANCE == null || INSTANCE.get() == null) {
            INSTANCE = new WeakReference<>(new FirebaseAnalyticsHelper(context));
        }
        return INSTANCE.get();
    }

    public void createSampleDeck() {
        firebaseAnalytics.logEvent(CREATE_SAMPLE_DECK, new Bundle());
        createDeck();
    }

    public void createDeck() {
        firebaseAnalytics.logEvent(CREATE_DECK, new Bundle());
    }

    public void importDeck() {
        firebaseAnalytics.logEvent(IMPORT_DECK, new Bundle());
        createDeck();
    }

    public void exportDeck() {
        firebaseAnalytics.logEvent(EXPORT_DECK, new Bundle());
    }

    public void syncDeck() {
        firebaseAnalytics.logEvent(SYNC_DECK, new Bundle());
    }

    public void createCard() {
        firebaseAnalytics.logEvent(CREATE_CARD, new Bundle());
    }

    public void again() {
        firebaseAnalytics.logEvent(CLICK_AGAIN, new Bundle());
    }

    public void quick() {
        firebaseAnalytics.logEvent(CLICK_QUICK, new Bundle());
    }

    public void easy() {
        firebaseAnalytics.logEvent(CLICK_EASY, new Bundle());
    }

    public void hard() {
        firebaseAnalytics.logEvent(CLICK_HARD, new Bundle());
    }

    public void menuOpenDiscord() {
        firebaseAnalytics.logEvent(MENU_OPEN_DISCORD, new Bundle());
    }

    public void discoverOpenDiscord() {
        firebaseAnalytics.logEvent(DISCOVER_OPEN_DISCORD, new Bundle());
    }

    public void discoverOpenFanpage() {
        firebaseAnalytics.logEvent(DISCOVER_OPEN_FANPAGE, new Bundle());
    }

    public void discoverOpenReview() {
        firebaseAnalytics.logEvent(DISCOVER_OPEN_REVIEW, new Bundle());
    }

    public void discoverOpenReviewInApp() {
        firebaseAnalytics.logEvent(DISCOVER_OPEN_REVIEW_IN_APP, new Bundle());
    }

    public void discoverOpenReviewFullPage() {
        firebaseAnalytics.logEvent(DISCOVER_OPEN_REVIEW_FULL_PAGE, new Bundle());
    }
}
