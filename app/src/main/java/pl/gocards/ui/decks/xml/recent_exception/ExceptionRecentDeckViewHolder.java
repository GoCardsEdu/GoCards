package pl.gocards.ui.decks.xml.recent_exception;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.xml.recent.RecentDeckViewHolder;

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
    public void onClick(View view) {
        getExceptionHandler().tryRun(
                () -> super.onClick(view),
                getActivity(), TAG,
                "Error clicking on item."
        );
    }
}