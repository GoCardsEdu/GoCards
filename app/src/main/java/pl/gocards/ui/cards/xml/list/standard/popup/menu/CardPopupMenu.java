package pl.gocards.ui.cards.xml.list.standard.popup.menu;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.list.standard.CardViewHolder;
import pl.gocards.ui.cards.xml.list.standard.ListCardsActivity;
import pl.gocards.ui.cards.xml.list.standard.ListCardsAdapter;

/**
 * C_02_01 When no card is selected and tap on the card, show the popup menu.
 *
 * @author Grzegorz Ziemski
 */
public class CardPopupMenu extends PopupMenuAtPos {

    public CardPopupMenu(
            ListCardsActivity activity,
            CardViewHolder holder
    ) {
        super(
                activity,
                holder.itemView,
                holder.getBindingAdapterPosition(),
                holder.getLastTouchX(),
                holder.getLastTouchY(),
                holder::unfocusItemView
        );
    }

    protected void createPopupMenu(@NonNull PopupMenu popupMenu) {
        popupMenu.getMenuInflater().inflate(R.menu.cards_list_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onPopupMenuItemClick);
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_card -> {
                getAdapter().startEditCardActivity(getPosition());
                return true;
            }
            case R.id.add_card -> {
                getAdapter().startNewCardActivity(getPosition());
                return true;
            }
            case R.id.delete_card -> {
                getAdapter().onClickDeleteCard(getPosition());
                return true;
            }
        }
        return super.onPopupMenuItemClick(item);
    }

    @NonNull
    protected ListCardsActivity requireActivity() {
        return (ListCardsActivity) super.getActivity();
    }

    @NonNull
    private ListCardsAdapter getAdapter() {
        return requireActivity().getAdapter();
    }
}
