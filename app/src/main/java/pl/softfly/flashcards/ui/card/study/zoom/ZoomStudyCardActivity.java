package pl.softfly.flashcards.ui.card.study.zoom;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.entity.deck.DeckConfig;
import pl.softfly.flashcards.ui.card.study.slide.SlideStudyCardActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardActivity extends SlideStudyCardActivity {

    private float termFontSize = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT;
    private float definitionFontSize = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInitialTermFontSize();
        setInitialDefinitionFontSize();
    }

    @SuppressLint({"ClickableViewAccessibility", "CheckResult"})
    protected void setInitialTermFontSize() {
        if (getDeckDb() == null) return;

        getDeckDb().deckConfigAsyncDao().getFloatByKey(DeckConfig.STUDY_CARD_TERM_FONT_SIZE)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(termFontSize -> {
                    setTermFontSize(termFontSize);
                    ZoomStudyCardFragment fragment = getAdapter().getActiveFragment();
                    if (fragment != null) fragment.setTermFontSize(termFontSize);
                })
                .subscribe(deckConfig -> {}, this::onErrorReadDeckSettings);
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void setInitialDefinitionFontSize() {
        if (getDeckDb() == null) return;

        getDeckDb().deckConfigAsyncDao().getFloatByKey(DeckConfig.STUDY_CARD_DEFINITION_FONT_SIZE)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(definitionFontSize -> {
                    setDefinitionFontSize(definitionFontSize);
                    ZoomStudyCardFragment fragment = getAdapter().getActiveFragment();
                    if (fragment != null) fragment.setDefinitionFontSize(definitionFontSize);
                })
                .subscribe(deckConfig -> {}, this::onErrorReadDeckSettings);
    }

    protected void onErrorReadDeckSettings(Throwable e) {
        getExceptionHandler().handleException(
                e, getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while read the deck view settings."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onStop() {
        getDeckDb().deckConfigAsyncDao().update(
                DeckConfig.STUDY_CARD_TERM_FONT_SIZE,
                Float.toString(termFontSize),
                Integer.toString(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT),
                this::getOnErrorSavingViewSettings
        );
        getDeckDb().deckConfigAsyncDao().update(
                DeckConfig.STUDY_CARD_DEFINITION_FONT_SIZE,
                Float.toString(definitionFontSize),
                Integer.toString(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT),
                this::getOnErrorSavingViewSettings
        );
        super.onStop();
    }

    @NonNull
    protected void getOnErrorSavingViewSettings(Throwable e) {
        getExceptionHandler().handleException(
                e, getSupportFragmentManager(),
                this.getClass().getSimpleName() + "_OnStop",
                "Error while saving deck view settings."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    protected float getTermFontSize() {
        return termFontSize;
    }

    protected void setTermFontSize(float termFontSize) {
        this.termFontSize = termFontSize;
    }

    protected float getDefinitionFontSize() {
        return definitionFontSize;
    }

    protected void setDefinitionFontSize(float definitionFontSize) {
        this.definitionFontSize = definitionFontSize;
    }

    protected ZoomStudyCardAdapter getAdapter() {
        return (ZoomStudyCardAdapter) super.getAdapter();
    }
}
