package pl.gocards.ui.cards.xml.list.select.popup.menu;

import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.list.select.SelectCardViewHolder;
import pl.gocards.ui.cards.xml.list.select.SelectListCardsActivity;

/**
 * C_02_04 When any card is selected and long pressing on the card, show the selected popup menu.
 *
 * @author Grzegorz Ziemski
 */
public class SelectedCardPopupMenu extends NotSelectedCardPopupMenu {

    public SelectedCardPopupMenu(
            SelectListCardsActivity activity,
            SelectCardViewHolder holder
    ) {
        super(activity, holder);
    }

    protected void createPopupMenu(@NonNull PopupMenu popupMenu) {
        super.createPopupMenu(popupMenu);
        popupMenu.getMenu().findItem(R.id.select_card).setVisible(false);
        popupMenu.getMenu().findItem(R.id.unselect_card).setVisible(true);
        showPaste(popupMenu);
    }
}