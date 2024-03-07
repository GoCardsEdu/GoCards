package pl.gocards.ui.decks.xml.folder;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.decks.xml.standard.DeckViewHolder;

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
public class FolderDeckViewHolder extends DeckViewHolder {

    public FolderDeckViewHolder(@NonNull View itemView, @NonNull FolderDeckViewAdapter adapter) {
        super(itemView, adapter);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected boolean onMenuMoreClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cut_card) {
            getAdapter().cut(getBindingAdapterPosition());
            return true;
        }
        return super.onMenuMoreClick(item);
    }

    @NonNull
    public FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }
}