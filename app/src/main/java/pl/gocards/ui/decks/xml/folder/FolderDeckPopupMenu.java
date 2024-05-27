package pl.gocards.ui.decks.xml.folder;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.decks.xml.standard.DeckPopupMenu;

/**
 * @author Grzegorz Ziemski
 */
public class FolderDeckPopupMenu extends DeckPopupMenu {

    public FolderDeckPopupMenu(
            FolderDeckViewAdapter adapter,
            int position,
            View view
    ) {
        super(adapter, position, view);

    }

    @Override
    protected boolean onMenuMoreClick(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cut_card) {
            getAdapter().cut(getPosition());
            return true;
        } else {
            return super.onMenuMoreClick(item);
        }
    }

    @NonNull
    protected FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }
}
