package pl.gocards.ui.cards.xml.list.drag_swipe;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.xml.list.standard.ListCardsAdapter;
import pl.gocards.ui.cards.xml.list.standard.CardViewHolder;

/**
 * C_U_03 Dragging the card to another position.
 * @author Grzegorz Ziemski
 */
public class DragSwipeListCardsAdapter extends ListCardsAdapter {

    private ItemTouchHelper touchHelper;

    public DragSwipeListCardsAdapter(@NonNull DragSwipeListCardsActivity activity) throws DatabaseException {
        super(activity);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DragSwipeCardViewHolder(onCreateView(parent), this);
    }

    /**
     * C_U_03 Dragging the card to another position.
     * Invoked when a card is being moved over other cards.
     */
    public void onMoveCard(int fromPosition, int toPosition) {
        Card card = getItem(fromPosition);
        getCurrentList().remove(card);
        getCurrentList().add(toPosition, card);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * C_U_03 Dragging the card to another position.
     * Invoked when the card is lowered and the position is saved.
     */
    @SuppressLint("CheckResult")
    public void onMovedCard(@NonNull Card cutCard, int toPosition) {
       Disposable disposable = getDeckDb().cardRxDao().changeCardOrdinal(cutCard, toPosition)
                .subscribeOn(Schedulers.io())
                .doOnComplete(this::loadItems)
                .subscribe(EMPTY_ACTION, this::onErrorMoveCard);
       addToDisposable(disposable);
    }

    private void onErrorMoveCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, requireActivity(), "Error while moving card.");
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public ItemTouchHelper getTouchHelper() {
        return touchHelper;
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }
}
