package pl.gocards.ui.cards.xml.list.select;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import pl.gocards.R;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.xml.list.drag_swipe.DragSwipeCardViewHolder;
import pl.gocards.ui.cards.xml.list.select.popup.menu.NotSelectedCardPopupMenu;
import pl.gocards.ui.cards.xml.list.select.popup.menu.SelectCardPopupMenu;
import pl.gocards.ui.cards.xml.list.select.popup.menu.SelectedCardPopupMenu;

/**
 * C_R_08 Select (cut) the card
 *
 * @author Grzegorz Ziemski
 */
public class SelectCardViewHolder extends DragSwipeCardViewHolder {

    public SelectCardViewHolder(@NonNull ItemCardBinding binding, @NonNull SelectListCardsAdapter adapter) {
        super(binding, adapter);
    }

    /**
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     */
    @Override
    protected void showSingleTapMenu() {
        new SelectCardPopupMenu(requireActivity(), this).showPopupMenu();
    }

    /**
     * C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
     */
    public void showSelectPopupMenu() {
        if (getAdapter().isCardSelected(getBindingAdapterPosition())) {
            new SelectedCardPopupMenu(requireActivity(), this).showPopupMenu();
        } else {
            new NotSelectedCardPopupMenu(requireActivity(), this).showPopupMenu();
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Implementation of GestureDetector
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_02_03 When any card is selected and tap on the card, select or unselect the card.
     */
    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        if (getAdapter().isSelectionMode()) {
            getAdapter().onCardInvertSelect(this);
            return false;
        } else {
            return super.onSingleTapUp(e);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Change the look of the view.
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void unfocusItemView() {
        // Check that the card has not been previously selected.
        int position = getBindingAdapterPosition();
        if (-1 < position && position < getAdapter().getItemCount()) {
            if (getAdapter().isCardSelected(position)) {
                selectItemView();
            } else {
                super.unfocusItemView();
            }
        }
    }

    protected void selectItemView() {
        this.itemView.setSelected(true);
        this.itemView.setBackgroundColor(
                MaterialColors.getColor(this.itemView, R.attr.colorItemSelected)
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected SelectListCardsAdapter getAdapter() {
        return (SelectListCardsAdapter) super.getAdapter();
    }

    @NonNull
    @Override
    protected SelectListCardsActivity requireActivity() {
        return (SelectListCardsActivity) super.requireActivity();
    }
}
