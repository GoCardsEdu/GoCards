package pl.gocards.ui.cards.study.display_ratio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import pl.gocards.ui.cards.study.display_ratio.model.DisplayRatioCardsStudyViewModel;
import pl.gocards.ui.cards.study.zoom.ZoomStudyCardActivity;

/**
 * C_U_37 Adjust the term/definition ratio by scrolling the slider.
 * <p>
 * The slider position is saved.
 * The next card will be opened with the same slider position size.
 *
 * @author Grzegorz Ziemski
 */
public class DisplayRatioStudyCardActivity extends ZoomStudyCardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected DisplayRatioStudyCardAdapter createAdapter() {
        return new DisplayRatioStudyCardAdapter(this);
    }

    @NonNull
    protected DisplayRatioCardsStudyViewModel createModel() {
        return new ViewModelProvider(this).get(DisplayRatioCardsStudyViewModel.class);
    }
}