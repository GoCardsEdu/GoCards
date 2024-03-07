package pl.gocards.ui.cards.xml.study.display_ratio;

import static android.view.View.OnTouchListener;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardConfig;
import pl.gocards.ui.cards.xml.study.display_ratio.model.DisplayRatioCardStudyViewModel;
import pl.gocards.ui.cards.xml.study.zoom.ZoomStudyCardFragment;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class DisplayRatioStudyCardFragment extends ZoomStudyCardFragment {

    /**
     * Move divider to move term / definition view boundary.
     */
    private final OnTouchListener moveDividerTouchListener = new OnTouchListener() {

        @SuppressWarnings("FieldCanBeLocal")
        private final int SAFE_DRAG_AREA_PX = 200;

        private boolean modeDrag;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, @NonNull MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_UP:
                        if (modeDrag) {
                            modeDrag = false;
                            return true;
                        }
                    case MotionEvent.ACTION_MOVE:
                        if (isTouchDividerView(event)) {
                            int height = getDisplayRatioLayout().getHeight();
                            int minY = (int) (0.2 * height);
                            int maxY = height - minY;
                            int[] point = new int[2];
                            getDisplayRatioLayout().getLocationOnScreen(point);
                            int absoluteTopY = point[1];

                            float newY = (event.getRawY() - absoluteTopY);
                            if (newY > minY && newY < maxY) {
                                setDisplayRatioByTouch(newY / height);
                            }
                            modeDrag = true;
                            return true;
                        } else {
                            return false;
                        }
                }
            return false;
        }

        private boolean isTouchDividerView(@NonNull MotionEvent event) {
            int[] guideLinePoint = new int[2];
            getGuideline().getLocationOnScreen(guideLinePoint);

            int startX = guideLinePoint[0];
            int endX = guideLinePoint[0] + getDivider().getWidth();
            int startY = guideLinePoint[1] - SAFE_DRAG_AREA_PX;
            int endY = guideLinePoint[1] + SAFE_DRAG_AREA_PX;

            if (startX < event.getRawX() && event.getRawX() < endX) {
                return startY < event.getRawY() && event.getRawY() < endY;
            }
            return false;
        }
    };


    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected DisplayRatioCardStudyViewModel createModel() {
        return new ViewModelProvider(this).get(DisplayRatioCardStudyViewModel.class);
    }

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDivider().setOnTouchListener(moveDividerTouchListener);
        return view;
    }

    @Override
    protected void onCardChanged(@NonNull Card card) {
        addToDisposable(
                getCardModel()
                        .loadDisplayRatio()
                        .doOnComplete(() -> runOnUiThread(
                                () -> setDisplayRatio(getCardModel().getDisplayRatio()),
                                this::onErrorLoadViewSettings
                        ))
                        .subscribe(EMPTY_ACTION, this::onErrorLoadViewSettings)
        );
        super.onCardChanged(card);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void initTermView() {
        super.initTermView();
        getTermTextView().setOnTouchListener(this::onTouch);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void initDefinitionView() {
        super.initDefinitionView();
        getDefinitionTextView().setOnTouchListener(this::onTouch);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void initShowDefinitionView() {
        super.initShowDefinitionView();
        getShowDefinitionView().setOnTouchListener(this::onTouch);
    }

    @SuppressWarnings("unused")
    private boolean onTouch(View v, MotionEvent event) {
        return moveDividerTouchListener.onTouch(getStudyCardLayout(), event);
    }

    @Override
    @SuppressLint("CheckResult")
    public void onPause() {
        super.onPause();
        Disposable disposable = getCardModel().saveDisplayRatio()
                .subscribe(EMPTY_ACTION, this::getOnErrorSavingViewSettings);
        requireSliderActivity().addToDisposable(disposable);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void resetView() {
        setDisplayRatioByTouch(CardConfig.STUDY_CARD_TD_DISPLAY_RATIO_DEFAULT);
        super.resetView();
    }

    @UiThread
    protected void setDisplayRatioByTouch(Float displayRatio) {
        setDisplayRatio(displayRatio);
        getCardModel().setDisplayRatioToSave(displayRatio);
    }

    @UiThread
    protected void setDisplayRatio(Float displayRatio) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getGuideline().getLayoutParams();
        params.guidePercent = displayRatio;
        getGuideline().setLayoutParams(params);
        getCardModel().setDisplayRatio(displayRatio);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for view
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    private Guideline getGuideline() {
        return getBinding().guideline;
    }

    @NonNull
    private View getDivider() {
        return getBinding().divider;
    }

    @NonNull
    private View getDisplayRatioLayout() {
        return getBinding().displayRatioLayout;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected DisplayRatioCardStudyViewModel getCardModel() {
        return (DisplayRatioCardStudyViewModel) super.getCardModel();
    }
}