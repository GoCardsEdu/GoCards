package pl.gocards.ui.cards.xml.list.select.popup.menu;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.list.select.SelectCardViewHolder;
import pl.gocards.ui.cards.xml.list.select.SelectListCardsActivity;
import pl.gocards.ui.cards.xml.list.select.SelectListCardsAdapter;
import pl.gocards.ui.cards.xml.list.standard.popup.menu.PopupMenuAtPos;

/**
 * C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
 *
 * @author Grzegorz Ziemski
 */
public class NotSelectedCardPopupMenu extends PopupMenuAtPos {
    private final SelectCardViewHolder holder;

    public NotSelectedCardPopupMenu(
            AppCompatActivity activity,
            SelectCardViewHolder holder
    ) {
        super(
                activity,
                holder.itemView,
                holder.getBindingAdapterPosition(),
                holder.getLastTouchX(),
                holder.getLastTouchY(),
                holder::unfocusItemView
        );
        this.holder = holder;
    }

    protected void createPopupMenu(@NonNull PopupMenu popupMenu) {
        popupMenu.getMenuInflater().inflate(R.menu.cards_list_popup_select_mode, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener((menuItem) -> onPopupMenuItemClick(menuItem, getHolder()));
        popupMenu.getMenu().findItem(R.id.select_card).setVisible(true);
        popupMenu.getMenu().findItem(R.id.unselect_card).setVisible(false);

        showPaste(popupMenu);
    }

    /**
     * Show "Paste" for the first item, otherwise show "Paste Before", "Paste After".
     */
    protected void showPaste(@NonNull PopupMenu popup) {
        if (getPosition() == 0) {
            popup.getMenu().findItem(R.id.paste_card).setVisible(false);
            popup.getMenu().findItem(R.id.paste_card_after).setVisible(true);
            popup.getMenu().findItem(R.id.paste_card_before).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.paste_card).setVisible(true);
            popup.getMenu().findItem(R.id.paste_card_after).setVisible(false);
            popup.getMenu().findItem(R.id.paste_card_before).setVisible(false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    protected boolean onPopupMenuItemClick(
            @NonNull MenuItem item,
            @NonNull SelectCardViewHolder holder
    ) {
        switch (item.getItemId()) {
            case R.id.select_card -> {
                getAdapter().onCardSelect(holder);
                return true;
            }
            case R.id.unselect_card -> {
                getAdapter().onCardUnselect(holder);
                return true;
            }
            case R.id.delete_selected_cards -> {
                getAdapter().onClickDeleteSelected();
                return true;
            }
            case R.id.paste_card_before -> {
                getAdapter().onClickPasteCardsBefore(getPosition());
                return true;
            }
            case R.id.paste_card, R.id.paste_card_after -> {
                getAdapter().onClickPasteCardsAfter(getPosition());
                return true;
            }
        }
        return super.onPopupMenuItemClick(item);
    }

    @NonNull
    protected SelectListCardsActivity requireActivity() {
        return (SelectListCardsActivity) super.getActivity();
    }

    @NonNull
    protected SelectListCardsAdapter getAdapter() {
        return requireActivity().getAdapter();
    }

    protected SelectCardViewHolder getHolder() {
        return holder;
    }
}