package pl.softfly.flashcards.ui.decks.standard;

import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.base.recyclerview.BaseViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class DeckViewHolder extends BaseViewHolder implements View.OnClickListener {

    protected DeckViewAdapter adapter;

    public DeckViewHolder(@NonNull View itemView, DeckViewAdapter adapter) {
        super(itemView);
        this.adapter = adapter;
        initMoreImageView();
        itemView.setOnClickListener(this);
    }

    protected void initMoreImageView() {
        View moreTextView = getMoreImageView();
        moreTextView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), moreTextView);
            popup.getMenuInflater().inflate(R.menu.popup_menu_deck, popup.getMenu());
            popup.setOnMenuItemClickListener(this::onMenuMoreClick);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
            popup.show();
        });
    }

    protected boolean onMenuMoreClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listCards:
                adapter.newListCardsActivity(getBindingAdapterPosition());
                return true;
            case R.id.addCard:
                adapter.newNewCardActivity(getBindingAdapterPosition());
                return true;
            case R.id.deleteDeck:
                adapter.showDeleteDeckDialog(getBindingAdapterPosition());
                return true;
            case R.id.renameDeck:
                adapter.showRenameDeckDialog(getBindingAdapterPosition());
                return true;
            case R.id.more:
                DeckBottomMenu deckBottomMenu = new DeckBottomMenu(getAdapter(), getBindingAdapterPosition());
                deckBottomMenu.show(getAdapter().getActivity().getSupportFragmentManager(), "MoreButton");
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        adapter.onItemClick(getBindingAdapterPosition());
    }

    public DeckViewAdapter getAdapter() {
        return adapter;
    }

    protected TextView getNameTextView() {
        return itemView.findViewById(R.id.nameTextView);
    }

    protected View getDeckListItem() {
        return itemView.findViewById(R.id.deckListItem);
    }

    protected TextView getTotalTextView() {
        return itemView.findViewById(R.id.totalTextView);
    }

    protected TextView getNewTextView() {
        return itemView.findViewById(R.id.newTextView);
    }

    protected TextView getRevTextView() {
        return itemView.findViewById(R.id.revTextView);
    }

    protected View getMoreImageView() {
        return itemView.findViewById(R.id.moreImageView);
    }
}