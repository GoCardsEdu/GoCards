package pl.gocards.ui.cards.xml.study.slider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

/**
 * C_R_31 No more cards to repeat
 * @author Grzegorz Ziemski
 */
public class NoMoreCardsToRepeatDialog extends DialogFragment {
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.card_study_no_more_cards_repeat))
                .setPositiveButton(R.string.ok, (dialog, which) -> requireActivity().finish())
                .create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        requireActivity().finish();
    }
}
