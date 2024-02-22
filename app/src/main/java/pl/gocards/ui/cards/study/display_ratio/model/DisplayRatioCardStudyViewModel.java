package pl.gocards.ui.cards.study.display_ratio.model;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import io.reactivex.rxjava3.core.Completable;
import pl.gocards.room.entity.deck.CardConfig;
import pl.gocards.ui.cards.study.zoom.model.ZoomCardStudyViewModel;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class DisplayRatioCardStudyViewModel extends ZoomCardStudyViewModel {

    @Nullable
    private Float displayRatioToSave;

    @SuppressLint("CheckResult")
    public Completable loadDisplayRatio() {
        return getDeckDb().cardConfigRxDao()
                .load(
                        getCardId(),
                        CardConfig.STUDY_CARD_TD_DISPLAY_RATIO,
                        getCardsViewModel().getDisplayRatio(),
                        this::setDisplayRatio
                ).ignoreElements();
    }

    @SuppressLint("CheckResult")
    public Completable saveDisplayRatio() {
        if (displayRatioToSave == null) return Completable.complete();

        return getDeckDb().cardConfigRxDao()
                .update(
                        getCardId(),
                        CardConfig.STUDY_CARD_TD_DISPLAY_RATIO,
                        Float.toString(displayRatioToSave)
                );
    }

    public float getDisplayRatio() {
        return getCardsViewModel().getDisplayRatio();
    }

    public void setDisplayRatio(float displayRatio) {
        getCardsViewModel().setDisplayRatio(displayRatio);
    }

    public void setDisplayRatioToSave(float displayRatioToSave) {
        this.displayRatioToSave = displayRatioToSave;
    }

    @Override
    public DisplayRatioCardsStudyViewModel getCardsViewModel() {
        return (DisplayRatioCardsStudyViewModel) super.getCardsViewModel();
    }
}
