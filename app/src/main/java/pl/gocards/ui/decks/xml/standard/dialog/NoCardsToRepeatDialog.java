package pl.gocards.ui.decks.xml.standard.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.gocards.R;

/**
 * D_R_04 No cards to repeat
 * @author Grzegorz Ziemski
 */
public class NoCardsToRepeatDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.decks_list_no_more_cards_repeat)
                .setPositiveButton(R.string.ok, (dialog, which) -> {})
                .create();
    }

}
