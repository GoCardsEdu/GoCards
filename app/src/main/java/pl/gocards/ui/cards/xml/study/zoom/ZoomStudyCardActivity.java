package pl.gocards.ui.cards.xml.study.zoom;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.xml.study.undo_learning.UndoLearningStudyCardActivity;

/**
 * C_U_36 Pinch-to-zoom the term/definition
 * <p>
 * The term/definition size is saved.
 * The next card will be opened with the same term/definition size.
 * There is only one configuration for all cards.
 *
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardActivity extends UndoLearningStudyCardActivity {

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    public ZoomStudyCardAdapter getAdapter() {
        return (ZoomStudyCardAdapter) super.getAdapter();
    }
}