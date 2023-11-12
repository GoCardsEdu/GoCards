package pl.gocards.ui.cards.list.select;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.list.drag_swipe.DragSwipeListCardsAdapter;
import pl.gocards.ui.cards.list.standard.CardViewHolder;

/**
 * C_R_08 Select (cut) the card
 * @author Grzegorz Ziemski
 */
public class SelectListCardsAdapter extends DragSwipeListCardsAdapter {

    protected final Set<Card> selectedCards = new HashSet<>();

    protected boolean hintsOnce = true;

    public SelectListCardsAdapter(@NonNull SelectListCardsActivity activity) throws DatabaseException {
        super(activity);
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectCardViewHolder(onCreateView(parent), this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        SelectCardViewHolder selectCardViewHolder = (SelectCardViewHolder) holder;
        if (isCardSelected(position)) {
            selectCardViewHolder.selectItemView();
        } else {
            holder.unfocusItemView();
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Select cards - features
     * ----------------------------------------------------------------------------------------- */

    public boolean isSelectionMode() {
        return !selectedCards.isEmpty();
    }

    public boolean isCardSelected(int position) {
        Card card = getItem(position);
        return card != null && selectedCards.contains(card);
    }

    /**
     * C_02_02 When no card is selected and long pressing on the card, select the card.
     */
    public void onCardInvertSelect(@NonNull SelectCardViewHolder viewHolder) {
        if (isCardSelected(viewHolder.getBindingAdapterPosition())) {
            onCardUnselect(viewHolder);
        } else {
            onCardSelect(viewHolder);
        }
    }

    /**
     * C_R_08 Select (cut) the card.
     */
    public void onCardSelect(@NonNull SelectCardViewHolder holder) {
        holder.selectItemView();
        Card card = getItem(holder.getBindingAdapterPosition());
        onCardSelect(card);
    }

    protected void onCardSelect(Card card) {
        selectedCards.add(card);
        countSelectedCardsOnSupportActionBar();
        if (selectedCards.size() == 1) {
            // Add selection mode options to the menu.
            this.requireActivity().refreshMenuOnAppBar();

            int colorItemSelected = MaterialColors.getColor(
                    getActivity().findViewById(android.R.id.content).getRootView(),
                    R.attr.colorItemSelected
            );
            requireActivity().getWindow().setStatusBarColor(colorItemSelected);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorItemSelected));

            if (hintsOnce) {
                showShortToastMessage(R.string.cards_list_toast_single_tap);
                showShortToastMessage(R.string.cards_list_toast_long_press);
                hintsOnce = false;
            }
        }
    }

    private void countSelectedCardsOnSupportActionBar() {
        if (selectedCards.size() > 0) {
            getSupportActionBar().setTitle(
                    String.format(
                            Locale.getDefault(),
                            getString(R.string.cards_list_cards_selected),
                            selectedCards.size()
                    )
            );
        } else {
            this.requireActivity().setDeckTitleActionBar();
        }
    }

    /**
     * C_R_09 Deselect the card.
     */
    public void onCardUnselect(@NonNull SelectCardViewHolder holder) {
        Card card = getItem(holder.getBindingAdapterPosition());
        onCardUnselect(card);
        holder.unfocusItemView();
    }

    protected void onCardUnselect(@NonNull Card card) {
        selectedCards.remove(card);
        countSelectedCardsOnSupportActionBar();
        if (selectedCards.isEmpty()) {
            resetSupportActionBar();
            // Remove selection mode options from the menu.
            this.requireActivity().refreshMenuOnAppBar();
        }
    }

    /**
     * C_D_10 Deselect the cards.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void onClickDeselectAll() {
        if (!selectedCards.isEmpty()) {
            resetSupportActionBar();
            selectedCards.clear();
            // Remove selection mode options from the menu.
            this.requireActivity().refreshMenuOnAppBar();
            // Clear background on items.
            this.notifyDataSetChanged();
        }
    }

    protected void resetSupportActionBar() {
        int defaultColor = SurfaceColors.SURFACE_2.getColor(this.requireActivity());
        requireActivity().getWindow().setStatusBarColor(defaultColor);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(defaultColor));

        this.requireActivity().setDeckTitleActionBar();
    }

    /**
     * C_D_11 Delete selected cards
     */
    @SuppressLint("CheckResult")
    public void onClickDeleteSelected() {
        Set<Card> deleteCards = new HashSet<>(selectedCards);

        // Delete the items in the order from the end.
        List<Integer> deleteCardsOrderByLast = deleteCards
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getOrdinal(), o1.getOrdinal()))
                .map(card -> getCurrentList().indexOf(card))
                .collect(Collectors.toList());

        Disposable disposable = getDeckDb().cardRxDao().delete(deleteCards)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> runOnUiThread(() -> {
                    // Must be after deleting from DB, otherwise #unfocusItemView works wrong.
                    getCurrentList().removeAll(deleteCards);
                    // It must be before refresh recycler so that the colors are right when binding.
                    selectedCards.clear();
                    resetSupportActionBar();

                    onCompleteDeleteSelectedRefreshDataSet(deleteCardsOrderByLast);
                    showCardsDeletedSnackbar(deleteCards);
                }, this::onErrorOnClickDeleteSelected))
                .subscribe(EMPTY_ACTION, this::onErrorOnClickDeleteSelected);

        addToDisposable(disposable);

        onClickDeselectAll();
    }

    @UiThread
    private void onCompleteDeleteSelectedRefreshDataSet(@NonNull List<Integer> deleteCardsOrderByLast) {
        deleteCardsOrderByLast.forEach(this::notifyItemRemoved);
        int firstCardPosition = deleteCardsOrderByLast.get(deleteCardsOrderByLast.size() - 1);
        int countItem = getItemCount() - firstCardPosition + 1;
        loadItems(firstCardPosition, countItem);

        // Disable the toolbar menu in selection mode.
        this.requireActivity().refreshMenuOnAppBar();
    }

    @UiThread
    private void showCardsDeletedSnackbar(@NonNull Set<Card> cardsToRevert) {
        int text = R.string.cards_list_toast_deleted_card;
        if (cardsToRevert.size() > 1) {
            text = R.string.cards_list_toast_deleted_cards;
        }

        Snackbar.make(this.requireActivity().getListCardsView(), text, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> revertCards(cardsToRevert))
                .show();
    }

    protected void onErrorOnClickDeleteSelected(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireActivity(), "Error while deleting the selected cards.");
    }

    /**
     * C_U_12 Undelete the selected cards
     */
    @SuppressLint("CheckResult")
    private void revertCards(@NonNull Set<Card> cards) {
        Disposable disposable = getDeckDb().cardRxDao().restore(cards)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> runOnUiThread(() -> {
                    selectedCards.addAll(cards);
                    loadItems();
                    // Enable the toolbar menu in selection mode.
                    this.requireActivity().refreshMenuOnAppBar();
                    showCardsRestoredToast(cards);
                }, this::onErrorRevertCards))
                .subscribe(EMPTY_ACTION, this::onErrorRevertCards);
        addToDisposable(disposable);
    }

    @UiThread
    private void showCardsRestoredToast(@NonNull Set<Card> cards) {
        int text = R.string.cards_list_toast_restore_card;
        if (cards.size() > 1) {
            text = R.string.cards_list_toast_restore_cards;
        }
        showShortToastMessage(text);
    }

    private void onErrorRevertCards(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireActivity(), "Error while restoring the deleted cards.");
    }

    /**
     * C_U_13 Paste cards before
     * @param pasteBeforePosition ordinal = getAdapterPosition() + 1
     */
    @UiThread
    public void onClickPasteCardsBefore(int pasteBeforePosition) {
        onClickPasteCards(getOrdinalFromItemPosition(pasteBeforePosition) - 1);
    }

    /**
     * C_U_14 Paste cards after
     * @param pasteAfterPosition ordinal = getAdapterPosition() + 1
     */
    @UiThread
    public void onClickPasteCardsAfter(int pasteAfterPosition) {
        onClickPasteCards(getOrdinalFromItemPosition(pasteAfterPosition));
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent"})
    @SuppressLint("CheckResult")
    public void onClickPasteCards(int pasteAfterOrdinal) {
        int minPosition = Math.min(pasteAfterOrdinal, selectedCards.stream()
                .mapToInt(Card::getOrdinal)
                .min()
                .getAsInt()) - 1;

        int maxPosition = Math.max(pasteAfterOrdinal, selectedCards.stream()
                .mapToInt(Card::getOrdinal)
                .max()
                .getAsInt());

        Disposable disposable = Completable.fromAction(() -> pasteCards(pasteAfterOrdinal))
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> loadItems(minPosition, maxPosition - minPosition))
                .subscribe(EMPTY_ACTION, this::onErrorOnClickPasteCards);
        addToDisposable(disposable);
    }

    protected void pasteCards(int pasteAfterPosition) {
        getDeckDb().cardRxDao().pasteCards(selectedCards, pasteAfterPosition);
        // Refresh ordinal numbers.
        runOnUiThread(this::refreshSelectedCards, this::onErrorOnClickPasteCards);
    }

    /*
     * Instead of the fake position from the recycler, it is better to use the real position of the card.
     * Sometimes the item position may not be adequate because some cards may be hidden, e.g. during a search.
     */
    @UiThread
    private int getOrdinalFromItemPosition(int position) {
        return getCurrentList().get(position).getOrdinal();
    }

    @UiThread
    private void refreshSelectedCards() {
        int[] selectedIds = selectedCards.stream()
                .mapToInt(card -> Objects.requireNonNull(card.getId()))
                .toArray();
        selectedCards.clear();

        Disposable disposable = getDeckDb().cardRxDao().findByIds(selectedIds)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(cards -> runOnUiThread(() -> selectedCards.addAll(cards), this::onErrorOnClickPasteCards))
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorOnClickPasteCards);
        addToDisposable(disposable);
    }

    protected void onErrorOnClickPasteCards(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, this.requireActivity(), "Error while paste the selected cards.");
    }

    @UiThread
    @Override
    protected void doOnSuccessDeleteCard(int position, Card card) {
        super.doOnSuccessDeleteCard(position, card);
        showCardHasBeenDeleted(card, selectedCards.contains(card));
        selectedCards.remove(card);
        onCardUnselect(card);
    }

    @UiThread
    @Override
    protected void onCompleteRevertCard(@NonNull Card card, boolean wasSelected) {
        if (wasSelected) onCardSelect(card);
        super.onCompleteRevertCard(card, wasSelected);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public SelectListCardsActivity requireActivity() {
        return (SelectListCardsActivity) super.requireActivity();
    }

    @NonNull
    protected ActionBar getSupportActionBar() {
        return Objects.requireNonNull(this.requireActivity().getSupportActionBar());
    }
}
