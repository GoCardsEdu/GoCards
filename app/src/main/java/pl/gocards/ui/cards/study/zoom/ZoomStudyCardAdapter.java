package pl.gocards.ui.cards.study.zoom;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.study.undo_learning.UndoLearningStudyCardAdapter;

/**
 * C_U_36 Pinch-to-zoom the term/definition
 * @author Grzegorz Ziemski
 */
public class ZoomStudyCardAdapter extends UndoLearningStudyCardAdapter {

    public ZoomStudyCardAdapter(@NonNull ZoomStudyCardActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public ZoomStudyCardFragment createStudyFragment() {
        return new ZoomStudyCardFragment();
    }
}
