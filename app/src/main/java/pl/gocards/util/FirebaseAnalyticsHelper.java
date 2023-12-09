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
}
