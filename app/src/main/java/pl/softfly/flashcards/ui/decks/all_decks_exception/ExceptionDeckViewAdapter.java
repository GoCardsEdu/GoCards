package pl.softfly.flashcards.ui.decks.all_decks_exception;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.softfly.flashcards.ui.decks.empty.NoDecksViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionDeckViewAdapter extends NoDecksViewAdapter {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public ExceptionDeckViewAdapter(
            @NonNull MainActivity activity,
            ExceptionListDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFolderViewHolder(View view) {
        return new ExceptionFolderViewHolder(view, this);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDeckViewHolder(View view) {
        return new ExceptionDeckViewHolder(view, this);
    }

    /* -----------------------------------------------------------------------------------------
     * Methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        getExceptionHandler().tryRun(
                () -> super.onBindViewHolder(holder, position),
                getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName() + "_OnBindViewHolder"
        );
    }
}
