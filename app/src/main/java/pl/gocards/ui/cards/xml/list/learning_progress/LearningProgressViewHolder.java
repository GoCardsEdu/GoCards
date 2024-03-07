package pl.gocards.ui.cards.xml.list.learning_progress;

import android.view.View;

import androidx.annotation.NonNull;

import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.cards.xml.list.search.SearchViewHolder;

/**
 * C_R_05 Show card status: disabled, forgotten on the right/left edge bar.
 * @author Grzegorz Ziemski
 */
public class LearningProgressViewHolder extends SearchViewHolder {

    public LearningProgressViewHolder(
            @NonNull ItemCardBinding binding,
            @NonNull LearningProgressListCardsAdapter adapter
    ) {
        super(binding, adapter);
    }

    @NonNull
    @Override
    public LearningProgressListCardsAdapter getAdapter() {
        return (LearningProgressListCardsAdapter) super.getAdapter();
    }

    @NonNull
    public View getLeftEdgeBarView() {
        return getBinding().leftEdgeBar;
    }

    @NonNull
    public View getRightEdgeBarView() {
        return getBinding().rightEdgeBar;
    }
}