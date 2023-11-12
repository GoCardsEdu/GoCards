package pl.gocards.ui.cards.study.zoom;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.cards.study.slider.fragment.StudyCardSliderFragment;
import pl.gocards.ui.cards.study.zoom.model.ZoomCardStudyViewModel;

/**
 * C_U_36 Pinch-to-zoom the term/definition
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardFragment extends StudyCardSliderFragment {

    /* -----------------------------------------------------------------------------------------
     * Menu options
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuIconWithText(
                menu.findItem(R.id.reset_view),
                R.drawable.ic_round_undo_24
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset_view) {
            resetView();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * C_U_38 Reset view settings
     */
    protected void resetView() {
        setTermFontSize(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT);
        setDefinitionFontSize(DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT);
    }

    /* -----------------------------------------------------------------------------------------
     * Create a fragment
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ZoomCardStudyViewModel createModel() {
        return new ViewModelProvider(this).get(ZoomCardStudyViewModel.class);
    }

    @Override
    protected void initTermView() {
        super.initTermView();
        getTermTextView().setTextSizeListener(textSize -> getCardModel().setTermFontSizeToSave(textSize));
    }

    @Override
    protected void initDefinitionView() {
        super.initDefinitionView();
        getDefinitionTextView().setTextSizeListener(textSize -> getCardModel().setDefinitionFontSizeToSave(textSize));
    }

    /* -----------------------------------------------------------------------------------------
     * Callbacks
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCardChanged(@NonNull Card card) {
        addToDisposable(
                getCardModel()
                        .loadTermFontSize()
                        .doOnComplete(() -> runOnUiThread(
                                () -> setTermFontSize(getCardModel().getTermFontSize()),
                                this::onErrorLoadViewSettings))
                        .subscribe(EMPTY_ACTION, this::onErrorLoadViewSettings)
        );
        addToDisposable(
                getCardModel()
                        .loadDefinitionFontSize()
                        .doOnComplete(() -> runOnUiThread(
                                () -> setDefinitionFontSize(getCardModel().getDefinitionFontSize()),
                                this::onErrorLoadViewSettings
                        ))
                        .subscribe(EMPTY_ACTION, this::onErrorLoadViewSettings)
        );
        super.onCardChanged(card);
    }

    protected void onErrorLoadViewSettings(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireSliderActivity(), "Error while loading deck view settings.");
    }

    /* -----------------------------------------------------------------------------------------
     * Lifecycle methods
     * ----------------------------------------------------------------------------------------- */

    @Override
    @SuppressLint("CheckResult")
    public void onPause() {
        super.onPause();
        Disposable disposable = getCardModel().saveTermFontSize()
                .subscribe(EMPTY_ACTION, this::getOnErrorSavingViewSettings);
        addToDisposable(disposable);

        disposable = getCardModel().saveDefinitionFontSize()
                .subscribe(EMPTY_ACTION, this::getOnErrorSavingViewSettings);
        addToDisposable(disposable);
    }

    protected void getOnErrorSavingViewSettings(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireSliderActivity(), "Error while saving deck view settings.");
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets
     * ----------------------------------------------------------------------------------------- */

    @UiThread
    protected void setTermFontSize(float size) {
        runOnUiThread(() -> getTermTextView().setTextSize(size));
    }

    @UiThread
    protected void setDefinitionFontSize(float size) {
        runOnUiThread(() -> getDefinitionTextView().setTextSize(size));
    }

    @NonNull
    public ZoomStudyCardActivity requireSliderActivity() {
        return (ZoomStudyCardActivity) super.requireActivity();
    }

    @NonNull
    protected ZoomCardStudyViewModel getCardModel() {
        return (ZoomCardStudyViewModel) super.getCardModel();
    }
}
