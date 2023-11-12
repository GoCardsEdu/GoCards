package pl.gocards.ui.cards.list.exception;

import android.view.MenuItem;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.list.file_sync.FileSyncListCardsActivity;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.list.file_sync.FileSyncCardViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionCardViewHolder extends FileSyncCardViewHolder {

    private static final String TAG = "ExceptionCardViewHolder";

    public ExceptionCardViewHolder(
            @NonNull ItemCardBinding binding,
            ExceptionListCardsAdapter adapter
    ) {
        super(binding, adapter);
    }

    /* -----------------------------------------------------------------------------------------
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        try {
            return super.onPopupMenuItemClick(item);
        } catch (Exception e) {
            this.onError(e);
            return false;
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Implementation of GestureDetector
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        getExceptionHandler().tryRun(
                () -> super.onLongPress(e),
                this::onError
        );
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent event) {
        try {
            return super.onSingleTapUp(event);
        } catch (Exception e) {
            this.onError(e);
            return false;
        }
    }

    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, getActivity(), TAG);
    }

    public FileSyncListCardsActivity getActivity() {
        return  getAdapter().requireActivity();
    }
}