package pl.softfly.flashcards.ui.decks.standard;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

import pl.softfly.flashcards.Config;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.databinding.FragmentListDecksBinding;
import pl.softfly.flashcards.databinding.FragmentNoDecksBinding;
import pl.softfly.flashcards.ui.ExportImportDbUtil;
import pl.softfly.flashcards.ui.app.settings.AppSettingsActivity;
import pl.softfly.flashcards.ui.base.IconInTopbarFragment;
import pl.softfly.flashcards.ui.decks.standard.dialog.CreateDeckDialog;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ListDecksFragment extends IconInTopbarFragment {

    private FragmentListDecksBinding binding;

    private DeckViewAdapter adapter;

    private ExportImportDbUtil exportImportDbUtil = new ExportImportDbUtil(this);

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = onCreateAdapter();
        askPermissionManageExternalStorage();
        setHasOptionsMenu(true);
    }

    protected DeckViewAdapter onCreateAdapter() {
        return new DeckViewAdapter((MainActivity) getActivity(), this);
    }

    protected void askPermissionManageExternalStorage() {
        Config config = Config.getInstance(getActivity().getApplicationContext());
        if (config.isDatabaseExternalStorage() || config.isTestFilesExternalStorage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListDecksBinding.inflate(inflater, container, false);
        initRecyclerView();
        getPathTextView().setBackgroundColor(SurfaceColors.SURFACE_2.getColor(getContext()));
        return binding.getRoot();
    }

    protected void initRecyclerView() {
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.addItemDecoration(createDividerItemDecoration());
        recyclerView.setAdapter(Objects.requireNonNull(adapter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    protected DividerItemDecoration createDividerItemDecoration() {
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        int color = SurfaceColors.SURFACE_1.getColor(getActivity());
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{color, color});
        drawable.setSize(1, dpToPx(1));
        divider.setDrawable(drawable);
        return divider;
    }

    /* -----------------------------------------------------------------------------------------
     * Fragment methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshItems();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_decks, menu);
        menuIconWithText(
                menu.findItem(R.id.new_deck),
                R.drawable.ic_outline_add_24,
                "New deck"
        );
        menuIconWithText(
                menu.findItem(R.id.new_folder),
                R.drawable.ic_baseline_create_new_folder_24,
                "New folder"
        );
        menuIconWithText(
                menu.findItem(R.id.import_excel),
                R.drawable.ic_round_file_download_24,
                "Import Excel"
        );
        menuIconWithText(
                menu.findItem(R.id.import_db),
                R.drawable.ic_round_file_download_24,
                "Import DB"
        );
        menuIconWithText(
                menu.findItem(R.id.settings),
                R.drawable.ic_baseline_settings_24,
                "Settings"
        );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_deck:
                newDeck();
                return true;
            case R.id.import_excel:
                importExcel();
                return true;
            case R.id.import_db:
                importDb();
                return true;
            case R.id.settings:
                startActivitySettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void newDeck() {
        DialogFragment dialog = new CreateDeckDialog(adapter.getCurrentFolder());
        dialog.show(requireActivity().getSupportFragmentManager(), "CreateDeck");
    }

    protected void importExcel() {
        ((MainActivity)getActivity()).getFileSyncUtil().launchImportExcel(getAdapter().getCurrentFolder().toString());
    }

    protected void importDb() {
        exportImportDbUtil.launchImportDb(getAdapter().getCurrentFolder().toString());
    }

    protected void startActivitySettings() {
        Intent intent = new Intent(getActivity(), AppSettingsActivity.class);
        startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected FragmentListDecksBinding getBinding() {
        return binding;
    }

    protected RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    public DeckViewAdapter getAdapter() {
        return adapter;
    }

    public void refreshItems() {
        adapter.refreshItems();
    }

    public ExportImportDbUtil getExportImportDbUtil() {
        return exportImportDbUtil;
    }

    protected FragmentNoDecksBinding getNoDecksBinding() {
        return getBinding().noDecks;
    }

    public TextView getPathTextView() {
        return getBinding().pathTextView;
    }

    protected ConstraintLayout getPathLayout() {
        return getBinding().pathLayout;
    }

    protected TextView getEmptyTextView() {
        return getNoDecksBinding().emptyTextView;
    }

    protected ConstraintLayout getNoDecksButtons() {
        return getNoDecksBinding().noDecksButtons;
    }
}