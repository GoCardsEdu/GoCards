package pl.softfly.flashcards.ui.cards.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.cards.standard.ListCardsActivity;

/**
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
public class SearchListCardsActivity extends ListCardsActivity {

    private String lastSearchQuery = null;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected SearchViewAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new SearchViewAdapter(this, getDeckDbPath());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menuIconWithText(
                menu.findItem(R.id.search),
                R.drawable.ic_baseline_search_24,
                "Search"
        );
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(this::onCloseListener);
        searchView.setOnQueryTextListener(onQueryTextListener());
        return true;
    }

    protected boolean onCloseListener() {
        getAdapter().setSearchQuery(null);
        getAdapter().loadItems();
        return false; // Iconified, collapse
    }

    protected SearchView.OnQueryTextListener onQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (lastSearchQuery == null) {
                    getAdapter().setSearchQuery(query);
                    getAdapter().loadItems();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (lastSearchQuery == null && getAdapter() != null) {
                    getAdapter().setSearchQuery(query);
                    getAdapter().loadItems();
                }
                return true;
            }
        };
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (lastSearchQuery != null) {
            searchView.setQuery(lastSearchQuery, false);
            lastSearchQuery = null;
        }
        return true;
    }

    @Override
    public void refreshMenuOnAppBar() {
        lastSearchQuery = getAdapter().getSearchQuery();
        invalidateOptionsMenu();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected SearchViewAdapter getAdapter() {
        return (SearchViewAdapter) super.getAdapter();
    }
}
