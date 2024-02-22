package pl.gocards.ui.cards.study.zoom.model;

import androidx.annotation.Nullable;

import io.reactivex.rxjava3.core.Completable;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.cards.study.slider.model.StudyCardViewModel;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class ZoomCardStudyViewModel extends StudyCardViewModel {

    @Nullable
    private Float termFontSizeToSave = null;

    @Nullable
    private Float definitionFontSizeToSave = null;

    public Completable loadTermFontSize() {
        return getDeckDb().cardConfigRxDao()
                .load(
                        getCardId(),
                        DeckConfig.STUDY_CARD_TERM_FONT_SIZE,
                        getCardsViewModel().getTermFontSize(),
                        this::setTermFontSize
                ).ignoreElements();
    }

    public Completable loadDefinitionFontSize() {
        return getDeckDb().cardConfigRxDao()
                .load(
                        getCardId(),
                        DeckConfig.STUDY_CARD_DEFINITION_FONT_SIZE,
                        getCardsViewModel().getDefinitionFontSize(),
                        this::setDefinitionFontSize
                ).ignoreElements();
    }

    public Completable saveTermFontSize() {
        if (termFontSizeToSave == null) return Completable.complete();

        return getDeckDb().cardConfigRxDao()
                .update(
                        getCardId(),
                        DeckConfig.STUDY_CARD_TERM_FONT_SIZE,
                        Float.toString(termFontSizeToSave)
                );
    }

    public Completable saveDefinitionFontSize() {
        if (definitionFontSizeToSave == null) return Completable.complete();

        return getDeckDb().cardConfigRxDao()
                .update(
                        getCardId(),
                        DeckConfig.STUDY_CARD_DEFINITION_FONT_SIZE,
                        Float.toString(definitionFontSizeToSave)
                );
    }

    public float getTermFontSize() {
        return getCardsViewModel().getTermFontSize();
    }

    public void setTermFontSize(float termFontSize) {
        getCardsViewModel().setTermFontSize(termFontSize);
    }

    public float getDefinitionFontSize() {
        return getCardsViewModel().getDefinitionFontSize();
    }

    public void setDefinitionFontSize(float definitionFontSize) {
        getCardsViewModel().setDefinitionFontSize(definitionFontSize);
    }

    public void setTermFontSizeToSave(float termFontSizeToSave) {
        this.termFontSizeToSave = termFontSizeToSave;
        setTermFontSize(termFontSizeToSave);
    }

    public void setDefinitionFontSizeToSave(float definitionFontSizeToSave) {
        this.definitionFontSizeToSave = definitionFontSizeToSave;
        setDefinitionFontSize(definitionFontSizeToSave);
    }

    @Override
    public ZoomStudyCardsSliderViewModel getCardsViewModel() {
        return (ZoomStudyCardsSliderViewModel) super.getCardsViewModel();
    }
}
