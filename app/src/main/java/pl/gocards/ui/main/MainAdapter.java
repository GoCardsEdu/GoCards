package pl.gocards.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import pl.gocards.ui.base.BaseFragmentStateAdapter;
import pl.gocards.ui.decks.all_decks_exception.ExceptionListDecksFragment;
import pl.gocards.ui.decks.recent.ListRecentDecksFragment;
import pl.gocards.ui.decks.recent_exception.ExceptionListRecentDecksFragment;
import pl.gocards.ui.decks.search.SearchDecksFragment;
import pl.gocards.ui.decks.standard.ListDecksFragment;

/**
 * @author Grzegorz Ziemski
 */
public class MainAdapter extends BaseFragmentStateAdapter {

    @NotNull
    private ListRecentDecksFragment recentDecksFragment = new ExceptionListRecentDecksFragment();

    @NotNull
    private ListDecksFragment allDecksFragment = new ExceptionListDecksFragment();

    public MainAdapter(@NonNull MainActivity mainActivity) {
        super(mainActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return recentDecksFragment;
        } else {
            return allDecksFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NotNull
    public ListRecentDecksFragment getRecentDecksFragment() {
        return recentDecksFragment;
    }

    public void setRecentDecksFragment(@NotNull ListRecentDecksFragment recentDecksFragment) {
        this.recentDecksFragment = recentDecksFragment;
    }

    @NotNull
    public SearchDecksFragment getAllDecksFragment() {
        return (SearchDecksFragment) allDecksFragment;
    }

    public void setAllDecksFragment(@NotNull ListDecksFragment allDecksFragment) {
        this.allDecksFragment = allDecksFragment;
    }
}
