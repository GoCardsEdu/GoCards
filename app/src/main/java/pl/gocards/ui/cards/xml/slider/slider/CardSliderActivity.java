package pl.gocards.ui.cards.xml.slider.slider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import pl.gocards.R;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.base.IconInToolbarActivity;
import pl.gocards.ui.cards.xml.slider.slider.anim.SwipeCardsDepthPageTransformer;

/**
 * C_R_22 Swipe the cards left and right
 * @author Grzegorz Ziemski
 */
public abstract class CardSliderActivity extends IconInToolbarActivity {

    public static final String DECK_DB_PATH = "DECK_DB_PATH";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private CardsSliderViewModel activityModel;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private CardSliderAdapter adapter;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private RecyclerView recyclerView;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private String deckDbPath;

    @Nullable
    private CardFragment activeFragment;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_study_card_slide);
        setBarSameColoursAsToolbar();
        showBackArrow();

        initViewPager();
        recyclerView = getRecyclerView(getViewPager());
        reduceDragSensitivity(5);

        deckDbPath = Objects.requireNonNull(getIntent().getStringExtra(DECK_DB_PATH));
        deckDb = getDeckDb(deckDbPath);
        setDeckTitleActionBar(deckDbPath);

        activityModel = setupModel(createModel());
        adapter = createAdapter();

        getViewPager().setAdapter(adapter);
    }

    public void setDeckTitleActionBar(@NonNull String deckDbPath) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getDeckName(deckDbPath));
    }

    protected abstract CardSliderAdapter createAdapter();

    @NonNull
    protected CardsSliderViewModel createModel() {
        return new ViewModelProvider(this).get(CardsSliderViewModel.class);
    }

    @NonNull
    protected CardsSliderViewModel setupModel(@NonNull CardsSliderViewModel model) {
        model.setDeckDb(getDeckDb(deckDbPath));
        return model;
    }

    @NonNull
    protected ViewPager2 initViewPager() {
        ViewPager2 viewPager = getViewPager();
        viewPager.setPageTransformer(new SwipeCardsDepthPageTransformer(this));
        return viewPager;
    }

    @SuppressWarnings({"SameParameterValue"})
    private void reduceDragSensitivity(int sensitivity) {
        try {
            Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);
            int touchSlop = (int) Objects.requireNonNull(touchSlopField.get(recyclerView));
            touchSlopField.set(recyclerView, touchSlop * sensitivity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    protected abstract void onSuccessLoadCards();

    protected void showNoMoreCardsDialog() {
        NoMoreCardsToDisplayDialog dialog = new NoMoreCardsToDisplayDialog();
        dialog.show(this.getSupportFragmentManager(), NoMoreCardsToDisplayDialog.class.getSimpleName());
    }

    /* -----------------------------------------------------------------------------------------
     * ViewPager2 - Sliding fragments
     * ----------------------------------------------------------------------------------------- */

    @UiThread
    protected void slideToNextCardWithRotate() {
        if (isLastCard()) {
            slideToFirstCard();
        } else {
            slideToNextCard();
        }
    }

    @UiThread
    protected void slideToPreviousCardWithRotate() {
        if (isFirstCard()) {
            slideToLastCard();
        } else {
            slideToPreviousCard();
        }
    }

    @UiThread
    protected void slideToFirstCard() {
        getViewPager().setCurrentItem(0);
    }

    @UiThread
    protected void slideToLastCard() {
        getViewPager().setCurrentItem(getLastPosition());
    }

    @UiThread
    protected void slideToNextCard() {
        getViewPager().setCurrentItem(getNextPosition());
    }

    @UiThread
    public void slideToPreviousCard() {
        getViewPager().setCurrentItem(getCurrentPosition() - 1);
    }

    public int getCurrentPosition() {
        return getViewPager().getCurrentItem();
    }

    protected int getNextPosition() {
        return getViewPager().getCurrentItem() + 1;
    }

    protected int getPreviousPositionWithRotate(int position) {
        if (isFirstCard()) {
            return getLastPosition();
        }
        return position - 1;
    }

    protected int getNextPositionWithRotate(int position) {
        if (position + 1 > getLastPosition()) {
            return 0;
        }
        return position + 1;
    }

    protected int getLastPosition() {
        return getCurrentList().size() - 1;
    }

    protected boolean isFirstCard() {
        return getCurrentPosition() == 0;
    }

    protected boolean isLastCard() {
        return getCurrentPosition() >= getLastPosition();
    }


    /* -----------------------------------------------------------------------------------------
     * Get/Sets View
     * ----------------------------------------------------------------------------------------- */

    protected ViewPager2 getViewPager() {
        return findViewById(R.id.viewPager);
    }

    @NonNull
    private RecyclerView getRecyclerView(ViewPager2 viewPager) {
        try {
            Field ff = ViewPager2.class.getDeclaredField("mRecyclerView");
            ff.setAccessible(true);
            return (RecyclerView) Objects.requireNonNull(ff.get(viewPager));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets ViewModel
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public CardsSliderViewModel getActivityModel() {
        return activityModel;
    }

    @NonNull
    public List<CardSlider> getCurrentList() {
        return activityModel.getCards();
    }

    @Nullable
    public Card getActiveCard() {
        CardFragment fragment = getActiveFragment();
        if (fragment == null) return null;
        return getActiveFragment().getCard();
    }

    protected Integer getActiveCardId() {
        CardFragment fragment = getActiveFragment();
        if (fragment == null) return null;
        return getActiveFragment().getCardId();
    }

    @Nullable
    public CardFragment getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(@Nullable CardFragment activeFragment) {
        this.activeFragment = activeFragment;
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    protected DeckDatabase getDeckDb() {
        return deckDb;
    }
    
    @NonNull
    public CardSliderAdapter getAdapter() {
        return adapter;
    }

    @NonNull
    private String getDeckName(@NonNull String dbPath) {
        return AppDeckDbUtil.getDeckName(dbPath);
    }
 }