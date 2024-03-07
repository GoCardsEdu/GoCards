package pl.gocards.ui.cards.xml.slider.edit;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;
import static pl.gocards.db.deck.DeckDbUtil.getDeckName;

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

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.databinding.FragmentEditCardBinding;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.CardLearningHistory;
import pl.gocards.ui.cards.xml.slider.add.NewCardFragment;
import pl.gocards.ui.cards.xml.slider.delete.DeleteCardSliderActivity;
import pl.gocards.room.util.TimeUtil;

/**
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
public class EditCardFragment extends NewCardFragment {

    @NotNull
    @SuppressWarnings("NotNullFieldNotInitialized") // {@link #onSuccessLoadCard}
    private Card card;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private FragmentEditCardBinding binding;

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditCardBinding.inflate(getLayoutInflater());
        loadCard(getCardId());
        loadCurrentLearningProgress(getCardId());
        return binding.getRoot();
    }

    @SuppressLint("CheckResult")
    protected void loadCard(int cardId) {
        Disposable disposable = getDeckDb().cardRxDao().getCard(cardId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(this::onSuccessLoadCard)
                .doOnEvent((value, error) -> {
                    if (value == null && error == null) {
                        throw new UnsupportedOperationException("No card to edit found.");
                    }
                })
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorCreateCard);

        compositeDisposable.add(disposable);
    }

    protected void onSuccessLoadCard(@NonNull Card card) {
        this.card = card;
        runOnUiThread(() -> {
            getTermEditText().setText(card.getTerm());
            getDefinitionEditText().setText(card.getDefinition());
            getDisableSwitch().setChecked(card.getDisabled());

            if (Objects.requireNonNull(card.getTerm()).isEmpty()) {
                getTermEditText().setHint(R.string.card_new_term_hint);
            }
            if (Objects.requireNonNull(card.getDefinition()).isEmpty()) {
                getDefinitionEditText().setHint(R.string.card_new_definition_hint);
            }
        }, this::onErrorCreateCard);
    }

    protected void loadCurrentLearningProgress(int cardId) {
        Disposable disposable = getDeckDb().cardLearningHistoryRxDao().findCurrentByCardId(cardId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(this::onSuccessLoadCurrentLearningProgress)
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorCreateCard);

        compositeDisposable.add(disposable);
    }

    protected void onSuccessLoadCurrentLearningProgress(
            @NonNull CardLearningHistory currentLearningProgress
    ) {
            if (currentLearningProgress.getNextReplayAt() != null) {
                runOnUiThread(
                        () -> getNextReplayAtTextDate().setText(
                                currentLearningProgress.getNextReplayAt().toString()
                        ),
                        this::onErrorCreateCard
                );
            }
    }

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void setTitle() {
        requireSliderActivity().setDeckTitleActionBar(getDeckName(getDeckDbPath()));
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

        inflater.inflate(R.menu.card_edit_menu, menu);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_card) {
            requireSliderActivity().deleteCard();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @SuppressLint("CheckResult")
    protected void onClickSaveCard() {
        Objects.requireNonNull(card);
        Card.Companion.setTerm(card, getTermEditText().getText().toString());
        Card.Companion.setDefinition(card, getDefinitionEditText().getText().toString());
        Card.Companion.setHtmlFlags(card);
        card.setDisabled(getDisableSwitch().isChecked());
        card.setUpdatedAt(TimeUtil.getNowEpochSec());

        Disposable disposable = getDeckDb().cardRxDao()
                .updateAll(card)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    doOnCompleteSaveCard();
                    refreshLastUpdatedAt();
                })
                .subscribe(EMPTY_ACTION, this::onErrorCreateCard);
        compositeDisposable.add(disposable);
    }

    @Override
    protected void doOnCompleteSaveCard() {
        showCardUpdatedToast();
    }

    protected void showCardUpdatedToast() {
        runOnUiThread(
                () -> showShortToastMessage(R.string.card_edit_card_updated_toast),
                this::onErrorCreateCard
        );
    }

    @Override
    protected void onErrorCreateCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, requireActivity(), "Error while updating card.");
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets View
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    private FragmentEditCardBinding getBinding() {
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
    protected EditText getNextReplayAtTextDate() {
        return getBinding().nextReplayAtTextDate;
    }

    @NonNull
    protected SwitchMaterial getDisableSwitch() {
        return getBinding().disableSwitch;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */
    @NonNull
    protected DeleteCardSliderActivity requireSliderActivity() {
        return super.requireSliderActivity();
    }

    @Override
    @NotNull
    public Card getCard() {
        return card;
    }
}