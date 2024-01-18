package pl.gocards.ui.cards.list.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.SearchView;

import java.util.Objects;

import pl.gocards.R;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.list.select.SelectListCardsActivity;

/**
 * C_R_02 Search cards
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class SearchListCardsActivity extends SelectListCardsActivity implements SearchView.OnQueryTextListener {

    /**
     * Used to restore the search after refreshing the menu
     */
    @Nullable
    private String restoreSearchQuery = null;

    /**
     * This is to avoid double loading of cards when opened.
     */
    private boolean skipInitSearch = true;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
    }

    @NonNull
    @Override
    protected SearchListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new SearchListCardsAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menuIconWithText(
                menu.findItem(R.id.search_cards),
                R.drawable.ic_baseline_search_24
        );
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_cards).getActionView();
        Objects.requireNonNull(searchView);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(this::onCloseListener);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean onCloseListener() {
        getAdapter().setSearchQuery(null);
        getAdapter().loadItems();
        return false; // Iconified, collapse
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_cards).getActionView();
        Objects.requireNonNull(searchView);
        if (restoreSearchQuery != null) {
            searchView.setQuery(restoreSearchQuery, false);
            restoreSearchQuery = null;
        }
        return true;
    }

    @Override
    @UiThread
    public void refreshMenuOnAppBar() {
        restoreSearchQuery = getAdapter().getSearchQuery();
        invalidateOptionsMenu();
    }

    /* -----------------------------------------------------------------------------------------
     * OnQueryTextListener
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (restoreSearchQuery == null) {
            getAdapter().setSearchQuery(query);
            getAdapter().loadItems();
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (restoreSearchQuery == null) {
            if (skipInitSearch) {
                skipInitSearch = false;
            } else {
                getAdapter().setSearchQuery(query);
                getAdapter().loadItems();
            }
        }
        return true;
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public boolean isSearchMode() {
        return restoreSearchQuery != null;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected SearchListCardsAdapter getAdapter() {
        return (SearchListCardsAdapter) super.getAdapter();
    }
}
