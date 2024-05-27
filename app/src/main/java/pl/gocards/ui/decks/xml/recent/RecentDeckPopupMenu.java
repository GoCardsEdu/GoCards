package pl.gocards.ui.decks.xml.recent;

import android.view.View;
import android.widget.PopupMenu;

import pl.gocards.R;
import pl.gocards.ui.decks.xml.standard.DeckPopupMenu;
import pl.gocards.ui.decks.xml.standard.DeckViewAdapter;

/**
 * @author Grzegorz Ziemski
 */
public class RecentDeckPopupMenu extends DeckPopupMenu {

    public RecentDeckPopupMenu(
            DeckViewAdapter adapter,
            int position,
            View view
    ) {
        super(adapter, position, view);
    }

    @Override
    protected PopupMenu createPopupMenu() {
        PopupMenu popup = super.createPopupMenu();
        popup.getMenu().removeItem(R.id.cut_card);
        return popup;
    }
}
