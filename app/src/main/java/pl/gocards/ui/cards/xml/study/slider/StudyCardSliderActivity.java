package pl.gocards.ui.cards.xml.study.slider;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.ViewModelProvider;

import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.cards.xml.slider.delete.DeleteCardSliderActivity;
import pl.gocards.ui.cards.xml.study.slider.model.StudyCardsSliderViewModel;

/**
 * C_R_22 Swipe the cards left and right
 * @author Grzegorz Ziemski
 */
public abstract class StudyCardSliderActivity extends DeleteCardSliderActivity {

    private int maxForgottenCards = DeckConfig.MAX_FORGOTTEN_CARDS_DEFAULT;

    /**
     * If the number of unmemorized cards reaches {@link #maxForgottenCards}
     * then the stack of cards will be restarted and browsing will go back to the first unmemorized card.
     */
    private int countForgottenCards = 0;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveMaxForgottenCards();
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                StudyCardSliderActivity.this.handleOnBackPressed();
            }
        });
    }

    @NonNull
    @Override
    protected StudyCardSliderAdapter createAdapter() {
        return new StudyCardSliderAdapter(this);
    }

    @NonNull
    @Override
    protected StudyCardsSliderViewModel createModel() {
        return new ViewModelProvider(this).get(StudyCardsSliderViewModel.class);
    }

    private void liveMaxForgottenCards() {
        getDeckDb().deckConfigLiveDataDao().findByKey(DeckConfig.MAX_FORGOTTEN_CARDS)
                .observe(this, deckConfig -> {
                    if (deckConfig != null)
                        maxForgottenCards = Integer.parseInt(deckConfig.getValue());
                });
    }

    /* -----------------------------------------------------------------------------------------
     * Overridden
     * ----------------------------------------------------------------------------------------- */

    /**
     * To not use parentActivityName in AndroidManifest
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void handleOnBackPressed() {
        if (getCurrentPosition() == 0) {
            finish();
        } else {
            runOnUiThread(this::slideToPreviousCard);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void showNoMoreCardsDialog() {
        NoMoreCardsToRepeatDialog dialog = new NoMoreCardsToRepeatDialog();
        dialog.show(this.getSupportFragmentManager(), NoMoreCardsToRepeatDialog.class.getSimpleName());
    }

    /* -----------------------------------------------------------------------------------------
     * ViewPager2 - Sliding fragments
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public View.OnClickListener getOnClickAgain() {
        return v -> runOnUiThread(this::forgetAndShowNextCard);
    }

    @NonNull
    public View.OnClickListener getOnClickMemorize() {
        return v -> runOnUiThread(this::removeAndShowNextCard);
    }

    @UiThread
    protected void forgetAndShowNextCard() {
        boolean exceededForgottenCards = maxForgottenCards != 0
                && countForgottenCards >= maxForgottenCards;
        if (isLastCard() || exceededForgottenCards) {
            slideToFirstCard();
            countForgottenCards = 0;
        } else {
            slideToNextCard();
            countForgottenCards++;
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Get/sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public StudyCardSliderAdapter getAdapter() {
        return (StudyCardSliderAdapter) super.getAdapter();
    }

    @NonNull
    public StudyCardsSliderViewModel getActivityModel() {
        return (StudyCardsSliderViewModel) super.getActivityModel();
    }
}