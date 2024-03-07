package pl.gocards.ui.cards.xml.study.slider.fragment;

import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.slider.edit.EditCardFragment;
import pl.gocards.ui.cards.xml.study.slider.StudyCardSliderActivity;

/**
 * C_C_24 Edit the card
 * @author Grzegorz Ziemski
 */
public class StudyEditCardFragment extends EditCardFragment {
    
    @Override
    protected void setTitle() {
        requireSliderActivity().setDeckTitleActionBar(getString(R.string.card_edit_title));
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void doOnCompleteSaveCard() {
        super.doOnCompleteSaveCard();
        runOnUiThread(() -> requireSliderActivity().stopEditCurrentCard());
    }

    @UiThread
    @Override
    protected void doOnCancel() {
        requireSliderActivity().stopEditCurrentCard();
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
