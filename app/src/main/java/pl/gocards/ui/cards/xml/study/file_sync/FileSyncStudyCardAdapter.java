package pl.gocards.ui.cards.xml.study.file_sync;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.xml.study.display_ratio.DisplayRatioStudyCardAdapter;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncStudyCardAdapter extends DisplayRatioStudyCardAdapter {

    public FileSyncStudyCardAdapter(@NonNull FileSyncStudyCardActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public FileSyncStudyCardFragment createStudyFragment() {
        return new FileSyncStudyCardFragment();
    }
}