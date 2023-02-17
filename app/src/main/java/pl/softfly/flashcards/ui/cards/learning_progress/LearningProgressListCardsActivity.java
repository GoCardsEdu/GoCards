package pl.softfly.flashcards.ui.cards.learning_progress;

import android.os.Bundle;

import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.cards.select.SelectListCardsActivity;

/**
 * @author Grzegorz Ziemski
 */
public class LearningProgressListCardsActivity extends SelectListCardsActivity {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected LearningProgressViewAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new LearningProgressViewAdapter(this, getDeckDbPath());
    }

    @Override
    public LearningProgressViewAdapter getAdapter() {
        return (LearningProgressViewAdapter) super.getAdapter();
    }
}
