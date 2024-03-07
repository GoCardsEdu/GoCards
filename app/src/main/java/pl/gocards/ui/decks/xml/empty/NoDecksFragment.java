package pl.gocards.ui.decks.xml.empty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.rxjava3.functions.Consumer;
import pl.gocards.ui.decks.xml.folder.FolderDeckViewAdapter;
import pl.gocards.ui.decks.xml.search.SearchDecksFragment;
import pl.gocards.util.CreateSampleDeck;
import pl.gocards.db.storage.DatabaseException;

/**
 * D_R_01 No deck and folders
 * @author Grzegorz Ziemski
 */
public class NoDecksFragment extends SearchDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getNewDeckButton().setOnClickListener(button -> newDeck());
        getCreateSampleDeckButton().setOnClickListener(button -> {
            try {
                createSampleDeck();
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
        });
        getNewFolderButton().setOnClickListener(button -> newFolder());
        getImportFileButton().setOnClickListener(button -> importFile());
        return view;
    }

    @NonNull
    @Override
    protected FolderDeckViewAdapter onCreateAdapter() {
        return new NoDecksViewAdapter(this);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    protected void createSampleDeck() throws DatabaseException {
        (new CreateSampleDeck()).create(
                getApplicationContext(),
                getAdapter().getCurrentFolder(),
                () -> getAdapter().loadItems(),
                this.requireActivity(),
                getDisposable()
        );
    }

    public void setEmptyDeckListView(@NonNull Consumer<? super Throwable> onError) {
        runOnUiThread(() -> {
            getNoDecksBinding().getRoot().setVisibility(View.VISIBLE);
            getRecyclerView().setVisibility(View.INVISIBLE);
        }, onError);
    }

    public void setNotEmptyDeckListView(@NonNull Consumer<? super Throwable> onError) {
        runOnUiThread(() -> {
            getNoDecksBinding().getRoot().setVisibility(View.INVISIBLE);
            getRecyclerView().setVisibility(View.VISIBLE);
        }, onError);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected Button getNewDeckButton() {
        return  getNoDecksBinding().newDeckButton;
    }

    @NonNull
    protected Button getCreateSampleDeckButton() {
        return  getNoDecksBinding().createSampleDeckButton;
    }

    @NonNull
    protected Button getNewFolderButton() {
        return  getNoDecksBinding().newFolderButton;
    }

    @NonNull
    protected Button getImportFileButton() {
        return  getNoDecksBinding().importFileButton;
    }
}