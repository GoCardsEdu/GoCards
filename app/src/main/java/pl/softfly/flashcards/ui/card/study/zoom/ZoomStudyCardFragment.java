package pl.softfly.flashcards.ui.card.study.zoom;

import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.ui.card.study.slide.SlideStudyCardAdapter;
import pl.softfly.flashcards.ui.card.study.slide.SlideStudyCardFragment;

/**
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardFragment extends SlideStudyCardFragment  {

    public ZoomStudyCardFragment(SlideStudyCardAdapter adapter, String deckDbPath, int cardId) {
        super(adapter, deckDbPath, cardId);
    }

    @Override
    protected void initTermView() {
        super.initTermView();
        setTermFontSize(getStudyActivity().getTermFontSize());
        getTermView().setTextSizeListener(textSize -> getStudyActivity().setTermFontSize(textSize));
    }

    @Override
    protected void initDefinitionView() {
        super.initDefinitionView();
        setDefinitionFontSize(getStudyActivity().getDefinitionFontSize());
        getDefinitionView().setTextSizeListener(textSize -> getStudyActivity().setDefinitionFontSize(textSize));
    }

    /* -----------------------------------------------------------------------------------------
     * Menu actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void resetView() {
        super.resetView();
        getTermView().setTextSize(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT);
        getDefinitionView().setTextSize(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT);
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets
     * ----------------------------------------------------------------------------------------- */

    protected void setTermFontSize(Float termFontSize) {
        getTermView().setTextSize(termFontSize);
    }

    protected void setDefinitionFontSize(Float definitionFontSize) {
        getDefinitionView().setTextSize(definitionFontSize);
    }

    public ZoomStudyCardActivity getStudyActivity() {
        return (ZoomStudyCardActivity) super.getActivity();
    }
}
