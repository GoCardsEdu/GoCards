package pl.gocards.ui.cards.slider.slider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

/**
 * C_R_21 No more cards to display
 * @author Grzegorz Ziemski
 */
public class NoMoreCardsToDisplayDialog extends DialogFragment {
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO Undo deleting can be added.
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.cards_slider_no_more_cards))
                .setPositiveButton(R.string.close, (dialog, which) -> requireActivity().finish())
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
    }
}
