package pl.gocards.ui.cards.xml.list.exception;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.xml.list.file_sync.FileSyncCardViewHolder;
import pl.gocards.ui.cards.xml.list.file_sync.FileSyncListCardsActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionCardViewHolder extends FileSyncCardViewHolder {

    private static final String TAG = "ExceptionCardViewHolder";

    public ExceptionCardViewHolder(
            @NonNull ItemCardBinding binding,
            @NonNull ExceptionListCardsAdapter adapter
    ) {
        super(binding, adapter);
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