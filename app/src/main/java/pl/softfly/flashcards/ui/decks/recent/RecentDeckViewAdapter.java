package pl.softfly.flashcards.ui.decks.recent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.entity.app.Deck;
import pl.softfly.flashcards.ui.decks.search.SearchDeckViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class RecentDeckViewAdapter extends SearchDeckViewAdapter {

    protected final ArrayList<Deck> decks = new ArrayList<>();

    public RecentDeckViewAdapter(
            @NonNull MainActivity activity,
            ListRecentDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_DECK == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deck, parent, false);
            return new RecentDeckViewHolder(view, this);
        } else {
            throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void loadItems(@NonNull Path openFolder) {
        getAppDatabase().deckDaoAsync()
                .findByLastUpdatedAt(15)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .doOnSuccess(decks -> {
                    paths.clear();
                    this.decks.clear();
                    this.decks.addAll(decks);
                    paths.addAll(
                            decks.stream()
                                    .map(deck -> Paths.get(deck.getPath()))
                                    .collect(Collectors.toList())
                    );
                    runOnUiThread(() -> notifyDataSetChanged(), this::onErrorBindView);
                    if (paths.isEmpty()) {
                        getFragment().setEmptyDeckListView(this::onErrorBindView);
                    } else {
                        getFragment().setNotEmptyDeckListView(this::onErrorBindView);
                    }
                })
                .subscribe();
    }

    protected ListRecentDecksFragment getFragment() {
        return (ListRecentDecksFragment) super.getFragment();
    }

}
