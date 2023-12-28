package pl.gocards.ui.decks.standard;

import android.annotation.SuppressLint;
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

import pl.gocards.filesync.FileSyncLauncher;
import pl.gocards.ui.settings.SettingsActivity;
import pl.gocards.util.Config;
import pl.gocards.R;
import pl.gocards.databinding.FragmentListDecksBinding;
import pl.gocards.databinding.FragmentNoDecksBinding;
import pl.gocards.ui.ExportImportDbUtil;
import pl.gocards.ui.base.IconInToolbarFragment;
import pl.gocards.ui.decks.standard.dialog.CreateDeckDialog;
import pl.gocards.ui.main.MainActivity;

/**
 * D_R_02 Show all decks
 * @author Grzegorz Ziemski
 */
public class ListDecksFragment extends IconInToolbarFragment {

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private FragmentListDecksBinding binding;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private DeckViewAdapter adapter;

    private boolean refreshCards = true;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivity(requireActivity());
        adapter = onCreateAdapter();
        askPermissionManageExternalStorage();
        setHasOptionsMenu(true);
    }

    @NonNull
    protected DeckViewAdapter onCreateAdapter() {
        return new DeckViewAdapter(this);
    }

    protected void askPermissionManageExternalStorage() {
        Config config = Config.getInstance(requireActivity().getApplicationContext());
        if (config.isDatabaseExternalStorage() || config.isTestFilesExternalStorage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentListDecksBinding.inflate(inflater, container, false);
        initRecyclerView();
        getPathTextView().setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()));
        return binding.getRoot();
    }

    protected void initRecyclerView() {
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.addItemDecoration(createDividerItemDecoration());
        recyclerView.setAdapter(Objects.requireNonNull(adapter));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    @NonNull
    protected DividerItemDecoration createDividerItemDecoration() {
        DividerItemDecoration divider = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        int color = SurfaceColors.SURFACE_1.getColor(requireContext());
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{color, color});
        drawable.setSize(1, dpToPx(1));
        divider.setDrawable(drawable);
        return divider;
    }

    /* -----------------------------------------------------------------------------------------
     * Lifecycle
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onResume() {
        super.onResume();
        onResumeFragment();
        if (refreshCards) {
            adapter.loadItems();
            refreshCards = false;
        }
    }

    protected void onResumeFragment() {
        requireMainActivity().getNavView().setSelectedItemId(R.id.all_decks);
        // Fix for screen rotation
        requireMainActivity().getAdapter().setAllDecksFragment(this);
    }

    /* -----------------------------------------------------------------------------------------
     * Menu
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.decks_list_menu, menu);
        menuIconWithText(
                menu.findItem(R.id.new_deck),
                R.drawable.ic_outline_add_24
        );
        menuIconWithText(
                menu.findItem(R.id.new_folder),
                R.drawable.ic_baseline_create_new_folder_24
        );
        menuIconWithText(
                menu.findItem(R.id.import_excel),
                R.drawable.ic_round_file_download_24
        );
        menuIconWithText(
                menu.findItem(R.id.import_csv),
                R.drawable.ic_round_file_download_24
        );
        menuIconWithText(
                menu.findItem(R.id.import_db),
                R.drawable.ic_round_file_download_24
        );
        menuIconWithText(
                menu.findItem(R.id.discord),
                R.drawable.discord
        );
        menuIconWithText(
                menu.findItem(R.id.app_settings),
                R.drawable.ic_baseline_settings_24
        );
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home -> {
                return false;
            }
            case R.id.new_deck -> {
                newDeck();
                return true;
            }
            case R.id.import_excel, R.id.import_csv -> {
                importFile();
                return true;
            }
            case R.id.import_db -> {
                importDb();
                return true;
            }
            case R.id.discord -> {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://discord.gg/jYyRnD27JP")
                ));
                return true;
            }
            case R.id.app_settings -> {
                startAppSettingsActivity();
                return true;
            }
        }
        throw new UnsupportedOperationException(
                String.format(
                        "Not implemented itemId=\"%d\" name=\"%s\" title=\"%s\"",
                        item.getItemId(),
                        getResources().getResourceEntryName(item.getItemId()),
                        item.getTitle()
                )
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    /**
     * D_C_06 Create a new deck
     */
    protected void newDeck() {
        DialogFragment dialog = new CreateDeckDialog(requireActivity(), adapter.getCurrentFolder());
        dialog.show(requireActivity().getSupportFragmentManager(), "CreateDeckDialog");
    }

    protected void importFile() {
        Objects.requireNonNull(getFileSyncLauncher())
                .launchImportFile(getAdapter().getCurrentFolder().toString());
    }

    /**
     * D_C_13 Import database
     */
    protected void importDb() {
        getExportImportDbUtil().launchImportDb(getAdapter().getCurrentFolder().toString());
    }

    protected void startAppSettingsActivity() {
        Intent intent = new Intent(requireActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets for View
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected FragmentListDecksBinding getBinding() {
        return binding;
    }

    @NonNull
    protected RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @NonNull
    protected FragmentNoDecksBinding getNoDecksBinding() {
        return getBinding().noDecks;
    }

    @NonNull
    public TextView getPathTextView() {
        return getBinding().pathTextView;
    }

    @NonNull
    protected ConstraintLayout getPathLayout() {
        return getBinding().pathLayout;
    }

    @NonNull
    protected TextView getEmptyTextView() {
        return getNoDecksBinding().emptyTextView;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected DeckViewAdapter getAdapter() {
        return adapter;
    }

    @NonNull
    protected MainActivity requireMainActivity() {
        return (MainActivity) super.requireActivity();
    }

    @NonNull
    protected ExportImportDbUtil getExportImportDbUtil() {
        return requireMainActivity().getExportImportDbUtil();
    }

    @Nullable
    protected FileSyncLauncher getFileSyncLauncher() {
        return requireMainActivity().getFileSyncLauncher();
    }

    public void setRefreshCards(boolean refreshCards) {
        this.refreshCards = refreshCards;
    }
}