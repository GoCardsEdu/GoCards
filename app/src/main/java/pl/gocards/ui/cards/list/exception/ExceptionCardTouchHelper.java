package pl.gocards.ui.cards.list.exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import pl.gocards.ui.cards.list.select.SelectCardTouchHelper;
import pl.gocards.ui.cards.list.select.SelectListCardsActivity;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionCardTouchHelper extends SelectCardTouchHelper {

    private static final String TAG = "ExceptionCardTouchHelper";

    public ExceptionCardTouchHelper(ExceptionListCardsAdapter adapter) {
        super(adapter);
    }

    @Override
    public void clearView(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder
    ) {
        getExceptionHandler().tryRun(
                () -> super.clearView(recyclerView, viewHolder),
                this::onError
        );
    }

    @Override
    public void onSelectedChanged(
            @Nullable RecyclerView.ViewHolder viewHolder,
            int actionState
    ) {
        getExceptionHandler().tryRun(
                () -> super.onSelectedChanged(viewHolder, actionState),
                this::onError
        );
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target
    ) {
        try {
            return super.onMove(recyclerView, viewHolder, target);
        } catch (Exception e) {
            onError(e);
            return false;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        getExceptionHandler().tryRun(
                () -> super.onSwiped(viewHolder, direction),
                this::onError
        );
    }

    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, getActivity(), TAG);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    public SelectListCardsActivity getActivity() {
        return  getAdapter().requireActivity();
    }
}
