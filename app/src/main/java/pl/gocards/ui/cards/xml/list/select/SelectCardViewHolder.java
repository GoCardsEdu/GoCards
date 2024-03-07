package pl.gocards.ui.cards.xml.list.select;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import pl.gocards.R;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.xml.list.drag_swipe.DragSwipeCardViewHolder;

/**
 * C_R_08 Select (cut) the card
 * @author Grzegorz Ziemski
 */
public class SelectCardViewHolder extends DragSwipeCardViewHolder {

    public SelectCardViewHolder(@NonNull ItemCardBinding binding, @NonNull SelectListCardsAdapter adapter) {
        super(binding, adapter);
    }

    /* -----------------------------------------------------------------------------------------
     * C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void createSingleTapMenu(@NonNull PopupMenu popupMenu) {
        super.createSingleTapMenu(popupMenu);
        popupMenu.getMenu().findItem(R.id.select_card).setVisible(!getAdapter().isSelectionMode());
    }

    /**
     * C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
     */
    public void showSelectPopupMenu() {
        showPopupMenu(this::createSelectSingleTapMenu);
    }

    protected void createSelectSingleTapMenu(@NonNull PopupMenu popupMenu) {
        popupMenu.getMenuInflater().inflate(R.menu.cards_list_popup_select_mode, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onPopupMenuItemClick);
        showSelectOrUnselect(popupMenu);
        showPaste(popupMenu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_card -> {
                getAdapter().onCardSelect(this);
                return true;
            }
            case R.id.unselect_card -> {
                getAdapter().onCardUnselect(this);
                return true;
            }
            case R.id.deselect_all_cards -> {
                getAdapter().onClickDeselectAll();
                return true;
            }
            case R.id.delete_selected_cards -> {
                getAdapter().onClickDeleteSelected();
                return true;
            }
            case R.id.paste_card_before -> {
                getAdapter().onClickPasteCardsBefore(getBindingAdapterPosition());
                return true;
            }
            case R.id.paste_card, R.id.paste_card_after -> {
                getAdapter().onClickPasteCardsAfter(getBindingAdapterPosition());
                return true;
            }
        }
        return super.onPopupMenuItemClick(item);
    }

    private void showSelectOrUnselect(@NonNull PopupMenu popup) {
        if (getAdapter().isCardSelected(getBindingAdapterPosition())) {
            popup.getMenu().findItem(R.id.select_card).setVisible(false);
            popup.getMenu().findItem(R.id.unselect_card).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.select_card).setVisible(true);
            popup.getMenu().findItem(R.id.unselect_card).setVisible(false);
        }
    }

    /**
     * Show "Paste" for the first item, otherwise show "Paste Before", "Paste After".
     */
    private void showPaste(@NonNull PopupMenu popup) {
        if (getBindingAdapterPosition() == 0) {
            popup.getMenu().findItem(R.id.paste_card).setVisible(false);
            popup.getMenu().findItem(R.id.paste_card_after).setVisible(true);
            popup.getMenu().findItem(R.id.paste_card_before).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.paste_card).setVisible(true);
            popup.getMenu().findItem(R.id.paste_card_after).setVisible(false);
            popup.getMenu().findItem(R.id.paste_card_before).setVisible(false);
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
}
