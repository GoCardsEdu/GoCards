package pl.gocards.ui.decks.all_decks_exception;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.folder.FolderDeckViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionDeckViewHolder extends FolderDeckViewHolder {

    private static final String TAG = "ExceptionDeckViewHolder";

    public ExceptionDeckViewHolder(
            @NonNull View itemView,
            @NonNull ExceptionDeckViewAdapter adapter
    ) {
        super(itemView, adapter);
    }

    @Override
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