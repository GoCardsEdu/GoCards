package pl.gocards.ui.cards.xml.study.zoom.model;

import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.cards.xml.study.slider.model.StudyCardsSliderViewModel;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardsSliderViewModel extends StudyCardsSliderViewModel {

    private float termFontSize = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT;

    private float definitionFontSize = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT;

    public float getTermFontSize() {
        return termFontSize;
    }

    public void setTermFontSize(float termFontSize) {
        this.termFontSize = termFontSize;
    }

    public float getDefinitionFontSize() {
        return definitionFontSize;
    }

    public void setDefinitionFontSize(float definitionFontSize) {
        this.definitionFontSize = definitionFontSize;
    }
}
