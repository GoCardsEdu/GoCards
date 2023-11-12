package pl.gocards.ui.decks.all_decks_exception;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.folder.FolderViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionFolderViewHolder extends FolderViewHolder {

    private static final String TAG = "ExceptionDeckViewHolder";

    public ExceptionFolderViewHolder(
            @NonNull View itemView,
            @NonNull ExceptionDeckViewAdapter adapter
    ) {
        super(itemView, adapter);
    }

    @Override
    protected boolean onMenuPopupClick(@NonNull MenuItem item) {
        try {
            return super.onMenuPopupClick(item);
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