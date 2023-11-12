package pl.gocards.ui.cards.study.slider.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.slider.add.NewCardFragment;
import pl.gocards.ui.cards.study.slider.StudyCardSliderActivity;

/**
 * C_C_62 Create a new card
 * @author Grzegorz Ziemski
 */
public class StudyNewCardFragment extends NewCardFragment {

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void doOnCancel() {
        requireSliderActivity().deleteNoSavedCard();
    }

    /* -----------------------------------------------------------------------------------------
     * Menu options
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected StudyCardSliderActivity requireSliderActivity() {
        return (StudyCardSliderActivity) super.requireSliderActivity();
    }
}
