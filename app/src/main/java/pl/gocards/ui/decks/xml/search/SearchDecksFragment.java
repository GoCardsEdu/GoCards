package pl.gocards.ui.decks.xml.search;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import java.util.Objects;

import pl.gocards.R;
import pl.gocards.ui.decks.xml.folder.FolderDeckViewAdapter;
import pl.gocards.ui.decks.xml.folder.ListFoldersDecksFragment;

/**
 * D_R_03 Search decks
 * @author Grzegorz Ziemski
 */
public class SearchDecksFragment extends ListFoldersDecksFragment
        implements SearchView.OnQueryTextListener {

    private MenuItem searchMenu;

    private boolean isClearSearch;

    @NonNull
    private String lastSearch = "";

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new SearchDeckViewAdapter(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuIconWithText(
                menu.findItem(R.id.search_cards),
                R.drawable.ic_baseline_search_24
        );
        initSearchActionBar(menu);
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search_cards) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initSearchActionBar(@NonNull Menu menu) {
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenu = menu.findItem(R.id.search_cards);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        Objects.requireNonNull(searchView).setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnCloseListener(this::onCloseSearchListener);
        searchView.setOnQueryTextListener(this);
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean onCloseSearchListener() {
        getAdapter().loadItems();
        return false; // Iconified, collapse
    }

    @Override
    public boolean onQueryTextSubmit(@NonNull String query) {
        if (lastSearch.equals(query)) {
            // Optimize before reload: This is often called when an activity is initialized
            return false;
        } else if (!isClearSearch && query.isEmpty()) {
            lastSearch = query;
            getPathLayout().setVisibility(View.VISIBLE);
            getAdapter().loadItems();
            return true;
        } else if (!isClearSearch || !query.isEmpty()) {
            isClearSearch = false;
            lastSearch = query;
            getPathLayout().setVisibility(View.GONE);
            getAdapter().searchItems(query);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(@NonNull String query) {
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

    public boolean isRootFolder() {
        return getAdapter().isRootFolder();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public SearchDeckViewAdapter getAdapter() {
        return (SearchDeckViewAdapter) super.getAdapter();
    }
}