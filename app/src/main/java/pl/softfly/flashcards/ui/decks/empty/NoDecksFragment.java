package pl.softfly.flashcards.ui.decks.empty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.functions.Consumer;
import pl.softfly.flashcards.CreateSampleDeck;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.decks.folder.FolderDeckViewAdapter;
import pl.softfly.flashcards.ui.decks.search.SearchDecksFragment;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class NoDecksFragment extends SearchDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getNewDeckButton().setOnClickListener(button -> newDeck());
        getSampleDeckButton().setOnClickListener(button -> {
            try {
                createSampleDeck();
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });
        getNewFolderButton().setOnClickListener(button -> newFolder());
        getImportExcelButton().setOnClickListener(button -> importExcel());
        return view;
    }

    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new NoDecksViewAdapter((MainActivity) getActivity(), this);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    protected void createSampleDeck() throws DatabaseException {
        (new CreateSampleDeck()).create(getContext(),  getAdapter().getCurrentFolder(), () -> refreshItems());
    }

    public void setEmptyDeckListView(Consumer<? super Throwable> onError) {
        runOnUiThread(() -> {
            getNoDecksBinding().getRoot().setVisibility(View.VISIBLE);
            getRecyclerView().setVisibility(View.INVISIBLE);
        }, onError);
    }

    public void setNotEmptyDeckListView(Consumer<? super Throwable> onError) {
        runOnUiThread(() -> {
            getNoDecksBinding().getRoot().setVisibility(View.INVISIBLE);
            getRecyclerView().setVisibility(View.VISIBLE);
        }, onError);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    protected Button getNewDeckButton() {
        return  getNoDecksBinding().newDeckButton;
    }

    protected Button getSampleDeckButton() {
        return  getNoDecksBinding().sampleDeckButton;
    }

    protected Button getNewFolderButton() {
        return  getNoDecksBinding().newFolderButton;
    }

    protected Button getImportExcelButton() {
        return  getNoDecksBinding().importExcelButton;
    }
}