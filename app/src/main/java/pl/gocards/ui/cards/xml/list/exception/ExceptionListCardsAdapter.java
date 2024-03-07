package pl.gocards.ui.cards.xml.list.exception;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.xml.list.file_sync.FileSyncListCardsAdapter;
import pl.gocards.ui.cards.xml.list.standard.CardViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionListCardsAdapter extends FileSyncListCardsAdapter {

    private static final String TAG = "ExceptionCardViewHolder";

    public ExceptionListCardsAdapter(@NonNull ExceptionListCardsActivity activity) throws DatabaseException {
        super(activity);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExceptionCardViewHolder(onCreateView(parent), this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        getExceptionHandler().tryRun(
                () -> super.onBindViewHolder(holder, position),
                this::onError
        );
    }

    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, this.requireActivity(),
                TAG
        );
    }
}