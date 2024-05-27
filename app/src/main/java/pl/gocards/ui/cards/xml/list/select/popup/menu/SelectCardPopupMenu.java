package pl.gocards.ui.cards.xml.list.select.popup.menu;

import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.list.select.SelectCardViewHolder;
import pl.gocards.ui.cards.xml.list.select.SelectListCardsActivity;
import pl.gocards.ui.cards.xml.list.select.SelectListCardsAdapter;
import pl.gocards.ui.cards.xml.list.standard.popup.menu.CardPopupMenu;

/**
 * C_02_01 When no card is selected and tap on the card, show the popup menu.
 *
 * @author Grzegorz Ziemski
 */
public class SelectCardPopupMenu extends CardPopupMenu {

    private final SelectCardViewHolder holder;

    public SelectCardPopupMenu(
            SelectListCardsActivity activity,
            SelectCardViewHolder holder
    ) {
        super(activity, holder);
        this.holder = holder;
    }

    @Override
    protected void createPopupMenu(@NonNull PopupMenu popupMenu) {
        super.createPopupMenu(popupMenu);
        popupMenu.setOnMenuItemClickListener((menuItem) -> onPopupMenuItemClick(menuItem, getHolder()));
    }

    protected boolean onPopupMenuItemClick(
            @NonNull MenuItem item,
            @NonNull SelectCardViewHolder holder
    ) {
        if (item.getItemId() == R.id.select_card) {
            getAdapter().onCardSelect(holder);
            return true;
        } else {
            return super.onPopupMenuItemClick(item);
        }
    }

    @NonNull
    protected SelectListCardsActivity requireActivity() {
        return (SelectListCardsActivity) super.requireActivity();
    }

    @NonNull
    protected SelectListCardsAdapter getAdapter() {
        return requireActivity().getAdapter();
    }

    protected SelectCardViewHolder getHolder() {
        return holder;
    }
}
