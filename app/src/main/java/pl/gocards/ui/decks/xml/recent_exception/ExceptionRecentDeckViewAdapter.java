package pl.gocards.ui.decks.xml.recent_exception;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.decks.xml.recent.RecentDeckViewAdapter;
import pl.gocards.ui.main.xml.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionRecentDeckViewAdapter extends RecentDeckViewAdapter {

    private static final String TAG = "ExceptionRecentDeckViewAdapter";

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public ExceptionRecentDeckViewAdapter(
            @NonNull MainActivity activity,
            @NonNull ExceptionListRecentDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }
    
    @NonNull
    @Override
    @SuppressWarnings("unused")
    protected ExceptionRecentDeckViewHolder onCreateDeckViewHolder(@NonNull View view) {
        return new ExceptionRecentDeckViewHolder(view, this);
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
