package pl.gocards.ui.decks.xml.recent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import java.util.Objects;

import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.R;
import pl.gocards.ui.decks.xml.empty.NoDecksFragment;

/**
 * D_R_05 Show recent used decks
 * @author Grzegorz Ziemski
 */
public class ListRecentDecksFragment extends NoDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected RecentDeckViewAdapter onCreateAdapter() {
        return new RecentDeckViewAdapter(requireMainActivity(), this);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getPathLayout().setVisibility(View.GONE);
        getEmptyTextView().setText(R.string.no_decks_recently_used);
        getNewFolderButton().setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.search_cards).setVisible(false);
        menu.findItem(R.id.new_folder).setVisible(false);
        menu.findItem(R.id.import_db).setVisible(false);
    }

    /**
     * Don't create search
     */
    protected void initSearchActionBar(@NonNull Menu menu) {}

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResumeFragment() {
        requireMainActivity().getNavView().setSelectedItemId(R.id.recent_decks);
        // Fix for screen rotation
        requireMainActivity().getAdapter().setRecentDecksFragment(this);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        requireMainActivity().hideBackArrow();
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void setEmptyDeckListView(@NonNull Consumer<? super Throwable> onError) {
        if (getActivity() != null) {
            runOnUiThread(() -> {
                getNoDecksBinding().getRoot().setVisibility(View.VISIBLE);
                getRecyclerView().setVisibility(View.INVISIBLE);
            }, onError);
        }
    }

    public void setNotEmptyDeckListView(@NonNull Consumer<? super Throwable> onError) {
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

    @NonNull
    protected ActionBar getSupportActionBar() {
        return Objects.requireNonNull(requireMainActivity().getSupportActionBar());
    }
}