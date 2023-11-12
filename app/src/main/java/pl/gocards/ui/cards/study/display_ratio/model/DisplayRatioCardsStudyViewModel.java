package pl.gocards.ui.cards.study.display_ratio.model;

import pl.gocards.room.entity.deck.CardConfig;
import pl.gocards.ui.cards.study.zoom.model.ZoomStudyCardsSliderViewModel;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class DisplayRatioCardsStudyViewModel extends ZoomStudyCardsSliderViewModel {

    private float displayRatio = CardConfig.STUDY_CARD_TD_DISPLAY_RATIO_DEFAULT;

    protected float getDisplayRatio() {
        return displayRatio;
    }

    public void setDisplayRatio(float displayRatio) {
        this.displayRatio = displayRatio;
    }
}
