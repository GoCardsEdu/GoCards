package pl.gocards.ui.cards.study.slider.fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;
import static pl.gocards.db.deck.DeckDbUtil.getDeckName;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.color.MaterialColors;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.R;
import pl.gocards.databinding.ActivityStudyCardBinding;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardLearningHistory;
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory;
import pl.gocards.room.util.HtmlUtil;
import pl.gocards.ui.cards.slider.slider.CardFragment;
import pl.gocards.ui.cards.study.slider.StudyCardSliderActivity;
import pl.gocards.ui.cards.study.slider.model.StudyCardViewModel;
import pl.gocards.ui.cards.study.zoom.ZoomTextView;

/**
 * C_R_30 Study the cards
 * @author Grzegorz Ziemski
 */
public class StudyCardSliderFragment extends CardFragment {

    public final static String BUNDLE_DECK_DB_PATH = "DECK_DB_PATH";
    public final static String BUNDLE_CARD_ID = "CARD_ID";
    private final static float DIVIDE_MINUTES_TO_HOURS = 60;
    private final static float DIVIDE_MINUTES_TO_DAYS = 24 * 60;
    private static final int ONE_DAY = 24 * 60;
    private static final int TWO_DAYs = 2 * ONE_DAY;
    private int cardId;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private StudyCardViewModel cardModel;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private String deckDbPath;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ActivityStudyCardBinding binding;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private View.OnClickListener onClickAgain;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private View.OnClickListener onClickMemorize;

    @NonNull
    private final HtmlUtil htmlUtil = HtmlUtil.getInstance();

    public StudyCardSliderFragment() {
        // Screen rotation only works with the default constructor.
    }

    /* -----------------------------------------------------------------------------------------
     * Create a fragment
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // setActivity(requireActivity());
        onRestoreBundle(bundle);
        setHasOptionsMenu(true);

        cardModel = setupModel(createModel());
        cardModel.preLoadCard(cardId, this::onErrorLoadCard);
        onClickAgain = requireSliderActivity().getOnClickAgain();
        onClickMemorize = requireSliderActivity().getOnClickMemorize();
    }

    @NonNull
    protected StudyCardViewModel createModel() {
       return new ViewModelProvider(this).get(StudyCardViewModel.class);
    }

    @NonNull
    protected StudyCardViewModel setupModel(@NonNull StudyCardViewModel model) {
        model.setDeckDbPath(deckDbPath);
        model.setDeckDb(getDeckDb(deckDbPath));
        model.setAppDb(getAppDb());
        model.setCardsViewModel(requireSliderActivity().getActivityModel());
        return model;
    }

    /*
     * Screen rotation only works with the default constructor.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_DECK_DB_PATH, deckDbPath);
        outState.putInt(BUNDLE_CARD_ID, cardId);
    }

    /*
     * Screen rotation only works with the default constructor.
     */
    private void onRestoreBundle(@Nullable Bundle bundle) {
        if (bundle == null) bundle = getArguments();
        Objects.requireNonNull(bundle);
        this.deckDbPath = Objects.requireNonNull(bundle.getString(BUNDLE_DECK_DB_PATH));
        this.cardId = bundle.getInt(BUNDLE_CARD_ID);
    }

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = ActivityStudyCardBinding.inflate(getLayoutInflater());

        initTermView();
        initTermWebView();
        initDefinitionView();
        initShowDefinitionView();
        initDefinitionWebView();

        // Fix for screen rotation
        getCardModel().setCardsViewModel(requireSliderActivity().getActivityModel());

        getCardModel().getCard().observe(getViewLifecycleOwner(), this::onCardChanged);
        getCardModel().getShowDefinition().observe(getViewLifecycleOwner(), this::onShowDefinition);

        getGradeButtonsLayout().setVisibility(INVISIBLE);
        getAgainButton().setOnClickListener(this::onClickAgain);

        getQuickButton().setVisibility(GONE);
        getQuickButton().setOnClickListener(this::onQuickClick);
        getCardModel().getNextAfterQuick().observe(getViewLifecycleOwner(), this::onQuickChanged);

        getEasyButton().setOnClickListener(this::onEasyClick);
        getCardModel().getNextAfterEasy().observe(getViewLifecycleOwner(), this::onEasyChanged);

        getHardButton().setOnClickListener(this::onHardClick);
        getCardModel().getNextAfterHard().observe(getViewLifecycleOwner(), this::onHardChanged);

        return getBinding().getRoot();
    }

    protected void onErrorLoadCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, getActivity(), "Error while read the card.");
    }

    @UiThread
    protected void initTermView() {
        getTermTextView().setMovementMethod(new ScrollingMovementMethod());
        getTermTextView().setVisibility(GONE);
    }

    @UiThread
    protected void initTermWebView() {
        getTermWebView().setBackgroundColor(Color.TRANSPARENT);
        getTermWebView().getSettings().setBuiltInZoomControls(true);
        getTermWebView().getSettings().setDisplayZoomControls(false);
        getTermWebView().setVisibility(GONE);
    }

    @UiThread
    protected void initDefinitionView() {
        getDefinitionTextView().setMovementMethod(new ScrollingMovementMethod());
    }

    @UiThread
    protected void initDefinitionWebView() {
        getDefinitionWebView().setBackgroundColor(Color.TRANSPARENT);
        getDefinitionWebView().getSettings().setBuiltInZoomControls(true);
        getDefinitionWebView().getSettings().setDisplayZoomControls(false);
        getDefinitionWebView().setVisibility(GONE);
    }

    @UiThread
    protected void initShowDefinitionView() {
        getShowDefinitionView().setOnClickListener(this::onClickShowDefinition);
    }

    /* -----------------------------------------------------------------------------------------
     * Menu options
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Bug with double display while scrolling
        MenuItem menuItem = menu.findItem(R.id.save_card);
        if (menuItem != null) {
            menuItem.setVisible(false);
        }

        inflater.inflate(R.menu.card_study_menu, menu);
        menuIconWithText(
                menu.findItem(R.id.edit_card),
                R.drawable.ic_round_edit_24
        );
        menuIconWithText(
                menu.findItem(R.id.add_card),
                R.drawable.ic_outline_add_24
        );
        menuIconWithText(
                menu.findItem(R.id.delete_card),
                R.drawable.ic_round_delete_24
        );

        // Bug with double display while scrolling
        menu.findItem(R.id.edit_card).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_card) {
            requireSliderActivity().deleteCard();
            return true;
        } else if (item.getItemId() == R.id.edit_card) {
            requireSliderActivity().startEditCurrentCard();
            return true;
        } else if (item.getItemId() == R.id.add_card) {
            requireSliderActivity().createNewCardAfter(cardId);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Lifecycle methods
     * ----------------------------------------------------------------------------------------- */

    public void onResume() {
        super.onResume();
        onRestoreBundle(null);

        // Fix for screen rotation
        cardModel.setCardsViewModel(requireSliderActivity().getActivityModel());

        // Refresh the card, it could have been edited
        cardModel.loadCard(cardId, this::onErrorLoadCard);
        setTitle();
    }

    @Override
    public void onPause() {
        super.onPause();
        getCardModel().hideDefinition();
    }

    protected void setTitle() {
        requireSliderActivity().setDeckTitleActionBar(getDeckName(getDeckDbPath()));
    }

    /* -----------------------------------------------------------------------------------------
     * Callbacks
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_U_32 Again
     */
    @SuppressLint("CheckResult")
    private void onClickAgain(View v) {
        Disposable disposable = getCardModel().onAgainClick()
                .doOnComplete(() -> runOnUiThread(
                        () -> {
                            getCardModel().hideDefinition();
                            // Fix when it is the last card.
                            getCardModel().setupLearningProgress(cardId, this::onErrorUpdateCardLearningProgress);
                            onClickAgain.onClick(v);
                        },
                        this::onErrorUpdateCardLearningProgress
                ))
                .subscribe(EMPTY_ACTION, this::onErrorUpdateCardLearningProgress);
        addToDisposable(disposable);
    }

    /**
     * C_U_33 Quick Repetition (5 min)
     */
    private void onQuickClick(View v) {
        updateLearningProgress(getCardModel().onQuickClick(), v);
    }

    /**
     * C_U_35 Easy (5 days)
     */
    private void onEasyClick(View v) {
        updateLearningProgress(getCardModel().onEasyClick(), v);
    }

    /**
     * C_U_34 Hard (3 days)
     */
    private void onHardClick(View v) {
        updateLearningProgress(getCardModel().onHardClick(), v);
    }

    @SuppressLint("CheckResult")
    private void updateLearningProgress(@NonNull Completable completable, View v) {
        Disposable disposable = completable
                .doOnComplete(() -> onClickMemorize.onClick(v))
                .subscribe(EMPTY_ACTION, this::onErrorUpdateCardLearningProgress);
        addToDisposable(disposable);
    }

    private void onErrorUpdateCardLearningProgress(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, getActivity(), "Error while update learning progress.");
    }

    protected void onCardChanged(@NonNull Card card) {
        runOnUiThread(() -> {

            if (card.isTermFullHtml()) {
                setText(getTermWebView(), card.getTerm());
                getTermWebView().setVisibility(VISIBLE);
            } else if (card.isTermSimpleHtml()) {
                getTermTextView().setText(htmlUtil.fromHtml(card.getTerm()));
                getTermTextView().setVisibility(VISIBLE);
            } else {
                getTermTextView().setText(card.getTerm());
                getTermTextView().setVisibility(VISIBLE);
            }

            if (card.isDefinitionFullHtml()) {
                setText(getDefinitionWebView(), card.getDefinition());
            } else if (card.isDefinitionSimpleHtml()) {
                getDefinitionTextView().setText(htmlUtil.fromHtml(card.getDefinition()));
                getDefinitionTextView().setVisibility(VISIBLE);
            } else {
                getDefinitionTextView().setText(card.getDefinition());
                getDefinitionTextView().setVisibility(VISIBLE);
            }

        });
    }

    @UiThread
    private void setText(@NonNull WebView webView, @NotNull String value) {
        int intColor = MaterialColors.getColor(getDefinitionTextView(), com.google.android.material.R.attr.colorOnSurfaceVariant, Color.BLACK);
        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));

        value = value.replace("\n", "<br/>");
        String content = "<header><style>*{margin:0;padding:0;} img{max-width: 95%}</style></header><body style='color:" + hexColor +";font-family:Roboto;font-size:x-large;display:flex;height:100%;text-align:center;'><div style='margin:auto;'><div style='margin-top:20px;margin-bottom:20px;'>" + value + "</div></div></body>";
        webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
    }

    private void onClickShowDefinition(View ignoredView) {
        getCardModel().showDefinition();
    }

    private void onShowDefinition(@NonNull Boolean showDefinition) {
        if (showDefinition) {
            showDefinition();
        } else {
            hideDefinition();
        }
    }

    private void showDefinition() {
        runOnUiThread(() -> {
            Card card = Objects.requireNonNull(getCard());
            if (card.isDefinitionFullHtml()) {
                getDefinitionWebView().setVisibility(VISIBLE);
                getDefinitionTextView().setVisibility(INVISIBLE);
            } else {
                getDefinitionTextView().setVisibility(VISIBLE);
                getDefinitionWebView().setVisibility(INVISIBLE);
            }
            getGradeButtonsLayout().setVisibility(VISIBLE);
            getShowDefinitionView().setVisibility(INVISIBLE);
        });
    }

    private void hideDefinition() {
        runOnUiThread(() -> {
            getDefinitionTextView().setVisibility(INVISIBLE);
            getDefinitionWebView().setVisibility(INVISIBLE);
            getGradeButtonsLayout().setVisibility(INVISIBLE);
            getDefinitionTextView().setVisibility(INVISIBLE);
            getShowDefinitionView().setVisibility(VISIBLE);
        });
    }

    private void onQuickChanged(@Nullable CardLearningProgressAndHistory progressAndHistory) {
        runOnUiThread(() -> {
            if (progressAndHistory != null) {
                getQuickButton().setVisibility(VISIBLE);
                getQuickButton().setText(
                        String.format(
                                displayIntervalWithTime(
                                        progressAndHistory.getHistory().getInterval(),
                                        R.string.card_study_quick_repetition_min,
                                        R.string.card_study_quick_repetition_hours
                                )
                        )
                );
            } else {
                getQuickButton().setVisibility(GONE);
            }
        });
    }

    private void onEasyChanged(@NonNull CardLearningProgressAndHistory progressAndHistory) {
        runOnUiThread(() -> {
            CardLearningHistory history = progressAndHistory.getHistory();
            if (getCardModel().wasNeverMemorized()) {
                // It always shows 1. It looks better without the day.
                getEasyButton().setText(
                        displayIntervalWithDays(
                                history.getInterval(),
                                R.string.card_study_easy_min,
                                R.string.card_study_easy_hours,
                                R.string.card_study_easy_day_only_again,
                                R.string.card_study_easy_days
                        )
                );
            } else {
                getEasyButton().setText(
                        displayIntervalWithDays(
                                history.getInterval(),
                                R.string.card_study_easy_min,
                                R.string.card_study_easy_hours,
                                R.string.card_study_easy_day,
                                R.string.card_study_easy_days
                        )
                );
            }
        });
    }

    private void onHardChanged(@NonNull CardLearningProgressAndHistory progressAndHistory) {
        runOnUiThread(() -> {
            CardLearningHistory history = progressAndHistory.getHistory();
            if (getCardModel().wasNeverMemorized()) {
                // It always shows 1. It looks better without the day.
                getHardButton().setText(
                        displayIntervalWithDays(
                                history.getInterval(),
                                R.string.card_study_hard_min,
                                R.string.card_study_hard_hours,
                                R.string.card_study_hard_day_only_again,
                                R.string.card_study_hard_days
                        )
                );
            } else {
                getHardButton().setText(
                        displayIntervalWithDays(
                                history.getInterval(),
                                R.string.card_study_hard_min,
                                R.string.card_study_hard_hours,
                                R.string.card_study_hard_day,
                                R.string.card_study_hard_days
                        )
                );
            }
        });
    }

    @NonNull
    private String displayIntervalWithDays(
            float interval,
            @StringRes int min,
            @StringRes int hours,
            @StringRes int day,
            @StringRes int days
    ) {
        if (interval < ONE_DAY) {
            return displayIntervalWithTime(interval, min, hours);
        } else if (interval < TWO_DAYs) {
            return String.format(getString(day), interval / DIVIDE_MINUTES_TO_DAYS);
        }
        return String.format(getString(days), interval / DIVIDE_MINUTES_TO_DAYS);
    }

    @NonNull
    private String displayIntervalWithTime(
            float interval,
            @StringRes int min,
            @StringRes int hours
    ) {
        if (interval < 60) {
            return String.format(
                    getString(min),
                    (int) interval
            );
        } else if (interval < 24 * 60) {
            return String.format(getString(hours), interval / DIVIDE_MINUTES_TO_HOURS);
        }
        throw new UnsupportedOperationException("It more than 1 day.");
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for view
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ActivityStudyCardBinding getBinding() {
        return binding;
    }

    @NonNull
    protected TextView getEditingLocked() {
        return getBinding().editingLocked;
    }

    @NonNull
    protected ConstraintLayout getStudyCardLayout() {
        return getBinding().studyCardLayout;
    }

    @NonNull
    protected ZoomTextView getTermTextView() {
        return getBinding().termTextView;
    }

    @NonNull
    protected WebView getTermWebView() {
        return getBinding().termWebView;
    }

    @NonNull
    protected ZoomTextView getDefinitionTextView() {
        return getBinding().definitionTextView;
    }

    @NonNull
    protected WebView getDefinitionWebView() {
        return getBinding().definitionWebView;
    }

    @NonNull
    protected TextView getShowDefinitionView() {
        return getBinding().showDefinitionView;
    }

    @NonNull
    private LinearLayout getGradeButtonsLayout() {
        return getBinding().gradeButtonsLayout;
    }

    @NonNull
    private Button getAgainButton() {
        return getBinding().againButton;
    }

    @NonNull
    private Button getQuickButton() {
        return getBinding().quickButton;
    }

    @NonNull
    private Button getEasyButton() {
        return getBinding().easyButton;
    }

    @NonNull
    private Button getHardButton() {
        return getBinding().hardButton;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected StudyCardSliderActivity requireSliderActivity() {
        return (StudyCardSliderActivity) super.requireActivity();
    }

    @NonNull
    protected String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    protected StudyCardViewModel getCardModel() {
        return cardModel;
    }

    @Nullable
    @Override
    public Card getCard() {
        return cardModel.getCard().getValue();
    }

    @Override
    public int getCardId() {
        Card card = Objects.requireNonNull(cardModel.getCard().getValue());
        return Objects.requireNonNull(card.getId());
    }
}