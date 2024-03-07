package pl.gocards.ui.cards.xml.study.file_sync;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import pl.gocards.R;
import pl.gocards.ui.cards.xml.study.display_ratio.DisplayRatioStudyCardFragment;

/**
 * @author Grzegorz Ziemski
 */
public class FileSyncStudyCardFragment extends DisplayRatioStudyCardFragment {

    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getEditingLocked().setVisibility(GONE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isEditingLocked()) {
            lockEditing();
        } else {
            unlockEditing();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (isEditingLocked()) {
            menu.findItem(R.id.edit_card).setVisible(false);
            menu.findItem(R.id.add_card).setVisible(false);
            menu.findItem(R.id.delete_card).setVisible(false);
        }
    }

    @UiThread
    public void lockEditing() {
        getEditingLocked().setVisibility(VISIBLE);
    }

    @UiThread
    public void unlockEditing() {
        getEditingLocked().setVisibility(GONE);
    }

    private boolean isEditingLocked() {
        return requireSliderActivity().isEditingLocked();
    }

    @NonNull
    public FileSyncStudyCardActivity requireSliderActivity() {
        return (FileSyncStudyCardActivity) super.requireActivity();
    }
}
