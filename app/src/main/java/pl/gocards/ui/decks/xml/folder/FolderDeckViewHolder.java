package pl.gocards.ui.decks.xml.folder;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.xml.standard.DeckViewHolder;

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
public class FolderDeckViewHolder extends DeckViewHolder {

    public FolderDeckViewHolder(@NonNull View itemView, @NonNull FolderDeckViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void showPopupMenu(View moreTextView) {
        new FolderDeckPopupMenu(
                getAdapter(),
                getBindingAdapterPosition(),
                moreTextView
        ).showPopupMenu();
    }

    @NonNull
    public FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }
}