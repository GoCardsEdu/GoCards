package pl.softfly.flashcards.ui.decks.recent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.functions.Consumer;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.decks.search.SearchDecksFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ListRecentDecksFragment extends SearchDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected RecentDeckViewAdapter onCreateAdapter() {
        return new RecentDeckViewAdapter((MainActivity) getActivity(), this);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getPathLayout().setVisibility(View.GONE);
        getEmptyTextView().setText("No recently used decks.");
        getNoDecksButtons().setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.new_folder).setVisible(false);
        menu.findItem(R.id.import_db).setVisible(false);
    }

    /**
     * Don't create search
     */
    protected void initSearchActionBar(Menu menu) {}

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void setEmptyDeckListView(Consumer<? super Throwable> onError) {
        if (getActivity() != null) {
            runOnUiThread(() -> {
                getNoDecksBinding().getRoot().setVisibility(View.VISIBLE);
                getRecyclerView().setVisibility(View.INVISIBLE);
            }, onError);
        }
    }

    public void setNotEmptyDeckListView(Consumer<? super Throwable> onError) {
        if (getActivity() != null) {
            runOnUiThread(() -> {
                getNoDecksBinding().getRoot().setVisibility(View.INVISIBLE);
                getRecyclerView().setVisibility(View.VISIBLE);
            }, onError);
        }
    }

    /**
     * Do nothing as search is disabled.
     */
    @Override
    public void clearSearch() {}

}