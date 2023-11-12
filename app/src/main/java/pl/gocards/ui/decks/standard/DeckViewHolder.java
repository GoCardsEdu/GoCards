package pl.gocards.ui.decks.standard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.decks.standard.dialog.NoCardsToRepeatDialog;

/**
 * D_R_02 Show all decks
 * @author Grzegorz Ziemski
 */
public class DeckViewHolder extends BaseViewHolder implements View.OnClickListener {

    protected boolean noCardsToRepeat;

    public DeckViewHolder(@NonNull View itemView, @NonNull DeckViewAdapter adapter) {
        super(itemView, adapter);
        initMoreImageView();
        itemView.setOnClickListener(this);
    }

    protected void initMoreImageView() {
        View moreTextView = getMoreImageView();
        moreTextView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), moreTextView);
            popup.getMenuInflater().inflate(R.menu.decks_list_popup_deck, popup.getMenu());
            popup.setOnMenuItemClickListener(this::onMenuMoreClick);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
            popup.show();
        });
    }

    @SuppressLint("NonConstantResourceId")
    protected boolean onMenuMoreClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_cards -> {
                getAdapter().newListCardsActivity(getBindingAdapterPosition());
                return true;
            }
            case R.id.add_card -> {
                getAdapter().newNewCardActivity(getBindingAdapterPosition());
                return true;
            }
            case R.id.delete_deck -> {
                getAdapter().showDeleteDeckDialog(getBindingAdapterPosition());
                return true;
            }
            case R.id.rename_deck -> {
                getAdapter().showRenameDeckDialog(getBindingAdapterPosition());
                return true;
            }
            case R.id.more -> {
                DeckBottomMenu deckBottomMenu = new DeckBottomMenu(getAdapter(), getBindingAdapterPosition());
                deckBottomMenu.show(requireActivity().getSupportFragmentManager(), "DeckBottomMenu");
                return true;
            }
        }
        throw new UnsupportedOperationException(
                String.format(
                        "Not implemented itemId=\"%d\" name=\"%s\" title=\"%s\"",
                        item.getItemId(),
                        requireActivity().getResources().getResourceEntryName(item.getItemId()),
                        item.getTitle()
                )
        );
    }

    @Override
    public void onClick(View view) {
        if (noCardsToRepeat) {
            showNoCardsToRepeatDialog();
        } else {
            getAdapter().onItemClick(getBindingAdapterPosition());
        }
    }

    /**
     * C_R_31 No more cards to repeat
     */
    protected void showNoCardsToRepeatDialog() {
        NoCardsToRepeatDialog dialog = new NoCardsToRepeatDialog();
        dialog.show(requireActivity().getSupportFragmentManager(), "NoCardsToRepeatDialog");
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for View
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected TextView getNameTextView() {
        return itemView.findViewById(R.id.nameTextView);
    }

    @NonNull
    protected TextView getTotalTextView() {
        return itemView.findViewById(R.id.totalTextView);
    }

    @NonNull
    protected TextView getNewTextView() {
        return itemView.findViewById(R.id.newTextView);
    }

    @NonNull
    protected TextView getRevTextView() {
        return itemView.findViewById(R.id.revTextView);
    }

    @NonNull
    protected View getMoreImageView() {
        return itemView.findViewById(R.id.moreImageView);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public DeckViewAdapter getAdapter() {
        return (DeckViewAdapter) super.getAdapter();
    }

    public void setNoCardsToRepeat(boolean noCardsToRepeat) {
        this.noCardsToRepeat = noCardsToRepeat;
    }
}