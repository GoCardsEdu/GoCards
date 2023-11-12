package pl.gocards.ui.decks.all_decks_exception;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.decks.empty.NoDecksViewAdapter;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionDeckViewAdapter extends NoDecksViewAdapter {

    private static final String TAG = "ExceptionDeckViewAdapter";

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public ExceptionDeckViewAdapter(@NonNull ExceptionListDecksFragment listDecksFragment) {
        super(listDecksFragment);
    }

    @NonNull
    @Override
    protected ExceptionFolderViewHolder onCreateFolderViewHolder(@NonNull View view) {
        return new ExceptionFolderViewHolder(view, this);
    }

    @NonNull
    @Override
    protected ExceptionDeckViewHolder onCreateDeckViewHolder(@NonNull View view) {
        return new ExceptionDeckViewHolder(view, this);
    }

    /* -----------------------------------------------------------------------------------------
     * Methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        getExceptionHandler().tryRun(
                () -> super.onBindViewHolder(holder, position),
                this::onErrorBindViewHolder
        );
    }

    protected void onErrorBindViewHolder(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity(), TAG,
                (dialog, which) -> getActivity().getOnBackPressedDispatcher().onBackPressed()
        );
    }
}
