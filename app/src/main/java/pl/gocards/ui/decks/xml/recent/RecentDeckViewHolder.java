package pl.gocards.ui.decks.xml.recent;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.xml.standard.DeckViewAdapter;
import pl.gocards.ui.decks.xml.standard.DeckViewHolder;

/**
 * D_R_05 Show recent used decks
 * @author Grzegorz Ziemski
 */
public class RecentDeckViewHolder extends DeckViewHolder {

    public RecentDeckViewHolder(@NonNull View itemView, @NonNull DeckViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void showPopupMenu(View moreTextView) {
        new RecentDeckPopupMenu(
                getAdapter(),
                getBindingAdapterPosition(),
                moreTextView
        ).showPopupMenu();
    }
}