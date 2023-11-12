package pl.gocards.ui.decks.recent_exception;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.recent.RecentDeckViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionRecentDeckViewHolder extends RecentDeckViewHolder {

    private static final String TAG = "ExceptionRecentDeckViewHolder";

    public ExceptionRecentDeckViewHolder(
            @NonNull View itemView,
            @NonNull ExceptionRecentDeckViewAdapter adapter
    ) {
        super(itemView, adapter);
    }

    @Override
    @SuppressWarnings("unused")
    protected boolean onMenuMoreClick(@NonNull MenuItem item) {
        try {
            return super.onMenuMoreClick(item);
        } catch (Exception e) {
            getExceptionHandler().handleException(
                    e, getActivity(), TAG,
                    "Error clicking on item in the more menu."
            );
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        getExceptionHandler().tryRun(
                () -> super.onClick(view),
                getActivity(), TAG,
                "Error clicking on item."
        );
    }
}