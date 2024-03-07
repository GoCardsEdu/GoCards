package pl.gocards.ui.decks.xml.recent;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.entity.app.Deck;
import pl.gocards.ui.decks.xml.empty.NoDecksViewAdapter;
import pl.gocards.ui.main.xml.MainActivity;

/**
 * D_R_05 Show recent used decks
 * @author Grzegorz Ziemski
 * @noinspection ALL
 */
public class RecentDeckViewAdapter extends NoDecksViewAdapter {

    protected final ArrayList<Deck> decks = new ArrayList<>();

    public RecentDeckViewAdapter(
            @NonNull MainActivity activity,
            @NonNull ListRecentDecksFragment listDecksFragment
    ) {
        super(listDecksFragment);
    }

    @NonNull
    @Override
    protected RecentDeckViewHolder onCreateDeckViewHolder(@NonNull View view) {
        return new RecentDeckViewHolder(view, this);
    }

    @Override
    public void loadItems(@NonNull Path openFolder) {
        Disposable disposable = getAppDatabase().deckRxDao()
                .findByLastUpdatedAt(15)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(decks -> runOnUiThread(() -> onSuccessLoadItems(decks), this::onErrorItemLoading))
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorItemLoading);
        addToDisposable(disposable);
    }

    @UiThread
    @SuppressLint("NotifyDataSetChanged")
    private void onSuccessLoadItems(@NonNull List<Deck> decks) {
        List<Path> mewPaths = decks.stream()
                .map(deck -> Paths.get(deck.getPath()))
                .collect(Collectors.toList());

        if (!this.decks.equals(decks)) {
            paths.clear();
            this.decks.clear();

            paths.addAll(mewPaths);
            this.decks.addAll(decks);

            this.notifyDataSetChanged();
        }
        if (paths.isEmpty()) {
            getFragment().setEmptyDeckListView(this::onErrorItemLoading);
        } else {
            getFragment().setNotEmptyDeckListView(this::onErrorItemLoading);
        }
    }

    protected ListRecentDecksFragment getFragment() {
        return (ListRecentDecksFragment) super.getFragment();
    }
}