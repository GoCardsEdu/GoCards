package pl.softfly.flashcards.ui.decks.search;

import android.app.SearchManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.decks.folder.FolderDeckViewAdapter;
import pl.softfly.flashcards.ui.decks.folder.ListFoldersDecksFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class SearchDecksFragment extends ListFoldersDecksFragment
        implements SearchView.OnQueryTextListener {

    private MenuItem searchMenu;

    protected boolean isClearSearch;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new SearchDeckViewAdapter((MainActivity) getActivity(), this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuIconWithText(
                menu.findItem(R.id.search),
                R.drawable.ic_baseline_search_24,
                "Search"
        );
        initSearchActionBar(menu);
    }

    protected void initSearchActionBar(Menu menu) {
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenu = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnCloseListener(this::onCloseListener);
        searchView.setOnQueryTextListener(this);
    }

    protected boolean onCloseListener() {
        getAdapter().refreshItems();
        return false; // Iconified, collapse
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!isClearSearch && query.isEmpty()) {
            getPathLayout().setVisibility(View.VISIBLE);
            getAdapter().refreshItems();
            return true;
        } else if (!isClearSearch || !query.isEmpty()) {
            isClearSearch = false;
            getPathLayout().setVisibility(View.GONE);
            getAdapter().searchItems(query);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return this.onQueryTextSubmit(query);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void clearSearch() {
        isClearSearch = true;
        searchMenu.collapseActionView();
        getPathLayout().setVisibility(View.VISIBLE);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @Override
    public SearchDeckViewAdapter getAdapter() {
        return (SearchDeckViewAdapter) super.getAdapter();
    }

}