package pl.gocards.ui.cards.study.display_ratio;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.study.zoom.ZoomStudyCardAdapter;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * @author Grzegorz Ziemski
 */
public class DisplayRatioStudyCardAdapter extends ZoomStudyCardAdapter {

    public DisplayRatioStudyCardAdapter(@NonNull DisplayRatioStudyCardActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public DisplayRatioStudyCardFragment createStudyFragment() {
        return new DisplayRatioStudyCardFragment();
    }
}
