package pl.softfly.flashcards.ui.decks.recent_exception;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pl.softfly.flashcards.ui.decks.recent.RecentDeckViewAdapter;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionRecentDeckViewAdapter extends RecentDeckViewAdapter {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public ExceptionRecentDeckViewAdapter(
            @NonNull MainActivity activity,
            ExceptionListRecentDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDeckViewHolder(View view) {
        return new ExceptionRecentDeckViewHolder(view, this);
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
