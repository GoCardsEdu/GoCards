package pl.gocards.ui.cards.list.learning_progress;

import android.os.Bundle;

import androidx.annotation.NonNull;

import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.list.search.SearchListCardsActivity;

/**
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
public class LearningProgressListCardsActivity extends SearchListCardsActivity {

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected LearningProgressListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new LearningProgressListCardsAdapter(this);
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public LearningProgressListCardsAdapter getAdapter() {
        return (LearningProgressListCardsAdapter) super.getAdapter();
    }
}
