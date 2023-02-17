package pl.softfly.flashcards.ui.cards.learning_progress;

import android.view.View;

import pl.softfly.flashcards.databinding.ItemCardBinding;
import pl.softfly.flashcards.ui.cards.select.SelectCardViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class LearningProgressViewHolder extends SelectCardViewHolder {

    public LearningProgressViewHolder(
            ItemCardBinding binding,
            LearningProgressViewAdapter adapter
    ) {
        super(binding, adapter);
    }

    @Override
    public LearningProgressViewAdapter getAdapter() {
        return (LearningProgressViewAdapter) super.getAdapter();
    }

    public View getLeftEdgeBarView() {
        return getBinding().leftEdgeBar;
    }

    public View getRightEdgeBarView() {
        return getBinding().rightEdgeBar;
    }

    public final void runOnUiThread(Runnable action) {
        getAdapter().getActivity().runOnUiThread(() -> action.run());
    }
}