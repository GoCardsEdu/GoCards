package pl.gocards.ui.decks.xml.standard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import pl.gocards.R;

/**
 * @author Grzegorz Ziemski
 */
public class DeckBottomMenu extends BottomSheetDialogFragment {

    @NonNull
    protected final DeckViewAdapter adapter;

    protected final int position;

    public DeckBottomMenu(@NonNull DeckViewAdapter adapter, int position) {
        this.adapter = adapter;
        this.position = position;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_deck_menu, container, false);

        NavigationView navigationView = view.findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            dismiss();
            switch (item.getItemId()) {
                case R.id.sync_excel -> {
                    adapter.launchSyncFile(position);
                    return true;
                }
                case R.id.export_excel -> {
                    adapter.launchExportToExcel(position);
                    return true;
                }
                case R.id.export_csv -> {
                    adapter.launchExportToCsv(position);
                    return true;
                }
                case R.id.export_db -> {
                    adapter.launchExportDb(position);
                    return true;
                }
                case R.id.deck_settings -> {
                    adapter.startDeckSettingsActivity(position);
                    return true;
                }
            }
            return false;
        });
        return view;
    }
}