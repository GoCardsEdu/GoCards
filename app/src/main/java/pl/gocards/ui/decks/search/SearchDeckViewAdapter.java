package pl.gocards.ui.decks.search;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.io.IOException;

import pl.gocards.ui.decks.folder.FolderDeckViewAdapter;

/**
 * D_R_03 Search decks
 * @author Grzegorz Ziemski
 */
public class SearchDeckViewAdapter extends FolderDeckViewAdapter {

    public SearchDeckViewAdapter(@NonNull SearchDecksFragment listDecksFragment) {
        super(listDecksFragment);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @UiThread
    @SuppressLint("NotifyDataSetChanged")
    public void searchItems(@NonNull String query) {
        if (query.isEmpty()) {
            loadItems();
        } else {
            paths.clear();
            try {
                paths.addAll(getDeckDatabaseUtil().searchDatabases(getRootFolder(), query));
                paths.addAll(getDeckDatabaseUtil().searchFolders(getRootFolder(), query));
            } catch (IOException e) {
                this.onErrorItemLoading(e);
            }
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(int position) {
        getFragment().clearSearch();
        super.onItemClick(position);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    protected SearchDecksFragment getFragment() {
        return (SearchDecksFragment) super.getFragment();
    }
}
