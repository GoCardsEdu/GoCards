package pl.gocards.ui.decks.xml.all_decks_exception;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.xml.folder.FolderDeckViewHolder;

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
    public void onClick(View view) {
        getExceptionHandler().tryRun(
                () -> super.onClick(view),
                getActivity(), TAG,
                "Error clicking on item."
        );
    }
}