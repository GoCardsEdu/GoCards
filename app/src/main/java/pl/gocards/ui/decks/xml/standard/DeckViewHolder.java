package pl.gocards.ui.decks.xml.standard;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.decks.xml.standard.dialog.NoCardsToRepeatDialog;

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

    private void initMoreImageView() {
        View moreTextView = getMoreImageView();
        moreTextView.setOnClickListener(this::showPopupMenu);
    }

    protected void showPopupMenu(View moreTextView) {
        new DeckPopupMenu(
                getAdapter(),
                getBindingAdapterPosition(),
                moreTextView
        ).showPopupMenu();
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