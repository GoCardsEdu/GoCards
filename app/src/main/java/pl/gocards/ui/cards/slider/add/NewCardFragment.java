package pl.gocards.ui.cards.slider.add;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.databinding.FragmentNewCardBinding;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardSlider;
import pl.gocards.ui.cards.slider.delete.DeleteCardSliderActivity;
import pl.gocards.ui.cards.slider.slider.CardFragment;
import pl.gocards.ui.cards.slider.slider.CardSliderActivity;
import pl.gocards.ui.cards.slider.slider.CardsSliderViewModel;
import pl.gocards.util.FirebaseAnalyticsHelper;

/**
 * C_C_23 Create a new card
 * @author Grzegorz Ziemski
 */
public class NewCardFragment extends CardFragment {

    private static final String TAG = "NewCardFragment";

    public static final String DECK_DB_PATH = CardSliderActivity.DECK_DB_PATH;

    public static final String CARD_ID = "CARD_ID";

    private int cardId;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private String deckDbPath;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckDatabase deckDb;
    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private AppDatabase appDb;
    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private FragmentNewCardBinding binding;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestoreBundle(savedInstanceState);
        setHasOptionsMenu(true);
        deckDb = getDeckDb(deckDbPath);
        appDb = getAppDb();
    }

    /*
     * Screen rotation only works with the default constructor.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DECK_DB_PATH, deckDbPath);
        outState.putInt(CARD_ID, cardId);
    }

    /*
     * Screen rotation only works with the default constructor.
     */
    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    protected Bundle onRestoreBundle(@Nullable Bundle bundle) {
        if (bundle == null) bundle = getArguments();
        Objects.requireNonNull(bundle);
        deckDbPath = Objects.requireNonNull(bundle.getString(DECK_DB_PATH));
        cardId = bundle.getInt(CARD_ID);
        return bundle;
    }

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentNewCardBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
    }

    protected void setTitle() {
        requireSliderActivity().setDeckTitleActionBar(getString(R.string.card_new_title));
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
        menuItem = menu.findItem(R.id.delete_card);
        if (menuItem != null) {
            menuItem.setVisible(false);
        }

        inflater.inflate(R.menu.card_new_menu, menu);
        menuIconWithText(
                menu.findItem(R.id.add_card),
                R.drawable.ic_outline_add_24
        );
        menuIconWithText(
                menu.findItem(R.id.delete_card),
                R.drawable.ic_round_delete_24
        );

        // Bug with double displaying while scrolling
        menu.findItem(R.id.save_card).setVisible(true);
        menu.findItem(R.id.delete_card).setVisible(true);
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home -> {
                //case R.id.cancel:
                doOnCancel();
                return true;
            }
            case R.id.save_card -> {
                onClickSaveCard();
                return true;
            }
            case R.id.add_card -> {
                requireSliderActivity().createNewCardAfter(cardId);
                return true;
            }
            case R.id.delete_card -> {
                requireSliderActivity().deleteNoSavedCard();
                return true;
            }
            default -> {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    protected void doOnCancel() {
        onBackPressed();
    }

    @SuppressLint("CheckResult")
    protected void onClickSaveCard() {
        int currentCardIndex = getActivityModel()
                .getCardIds()
                .indexOf(cardId);

        CardSlider previousCardSlider = getActivityModel()
                .findFirstSavedBefore(currentCardIndex);

        boolean isFirstCard = previousCardSlider == null;
        if (isFirstCard) {
            saveCard(1);
        } else {
            Disposable disposable = getDeckDb()
                    .cardRxDao()
                    .getOrdinal(Objects.requireNonNull(previousCardSlider.getId()))
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess(ordinal -> saveCard(ordinal + 1))
                    .ignoreElement()
                    .subscribe(EMPTY_ACTION, this::onErrorCreateCard);
            requireSliderActivity().addToDisposable(disposable);
        }
    }

    @SuppressLint("CheckResult")
    protected void saveCard(int ordinal) {
        int currentCardIndex = getActivityModel()
                .getCardIds()
                .indexOf(cardId);

        CardSlider currentCardSlider = getActivityModel()
                .getCard(currentCardIndex);

        currentCardSlider.setSaved(true);

        Disposable disposable = getDeckDb().cardRxDao()
                .insertAfter(createCard(), ordinal)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    doOnCompleteSaveCard();
                    refreshLastUpdatedAt();
                })
                .subscribe(EMPTY_ACTION, this::onErrorCreateCard);
        requireSliderActivity().addToDisposable(disposable);
    }

    @NonNull
    private Card createCard() {
        Card card = new Card();
        card.setId(cardId);
        Card.Companion.setTerm(card, getTermEditText().getText().toString());
        Card.Companion.setDefinition(card, getDefinitionEditText().getText().toString());
        Card.Companion.setHtmlFlags(card);
        card.setDisabled(getDisableSwitch().isChecked());
        return card;
    }

    protected void doOnCompleteSaveCard() {
        showCardAddedToast();
        runOnUiThread(() -> requireSliderActivity().stopNewCard(cardId), this::onErrorCreateCard);

        FirebaseAnalyticsHelper
                .getInstance(getApplicationContext())
                .createCard();
    }

    private void showCardAddedToast() {
        runOnUiThread(
                () -> showShortToastMessage(R.string.card_edit_card_added_toast),
                this::onErrorCreateCard
        );
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored") // must always be updated and does not touch the UI
    protected void refreshLastUpdatedAt() {
        appDb.deckRxDao()
                .refreshLastUpdatedAt(getDeckDbPath())
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorCreateCard);
    }

    protected void onErrorCreateCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(), TAG,
                "Error while creating card.",
                (dialog, which) -> onBackPressed()
        );
    }

    protected void onBackPressed() {
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets View
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    private FragmentNewCardBinding getBinding() {
        return binding;
    }

    @NonNull
    protected EditText getTermEditText() {
        return getBinding().termEditText;
    }

    @NonNull
    protected EditText getDefinitionEditText() {
        return getBinding().definitionEditText;
    }

    @NonNull
    protected SwitchMaterial getDisableSwitch() {
        return getBinding().disableSwitch;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected CardsSliderViewModel getActivityModel() {
        return requireSliderActivity().getActivityModel();
    }

    @Nullable
    @Override
    public Card getCard() {
        return null;
    }

    @Override
    public int getCardId() {
        return cardId;
    }

    @NonNull
    protected DeckDatabase getDeckDb() {
        return deckDb;
    }

    @NonNull
    protected String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    protected DeleteCardSliderActivity requireSliderActivity() {
        return (DeleteCardSliderActivity) super.requireActivity();
    }
}