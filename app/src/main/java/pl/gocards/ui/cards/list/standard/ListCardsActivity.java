package pl.gocards.ui.cards.list.standard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

import pl.gocards.R;
import pl.gocards.databinding.ActivityListCardsBinding;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.base.IconInToolbarActivity;
import pl.gocards.ui.cards.slider.add.AddCardSliderActivity;
import pl.gocards.ui.cards.slider.delete.DeleteCardSliderActivity;
import pl.gocards.ui.cards.slider.slider.CardSliderActivity;
import pl.gocards.ui.settings.SettingsActivity;

/**
 * C_R_01 Display all cards
 * @author Grzegorz Ziemski
 */
public class ListCardsActivity extends IconInToolbarActivity {

    public static final String DECK_DB_PATH = "DECK_DB_PATH";

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private String deckDbPath;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ListCardsAdapter adapter;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ActivityListCardsBinding binding;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public ListCardsActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarSameColoursAsToolbar();

        binding = ActivityListCardsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Remove shadow under bar
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getHeaderTableRow().setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));
        deckDbPath = Objects.requireNonNull(getIntent().getStringExtra(DECK_DB_PATH));
        setDeckTitleActionBar();

        try {
            onCreateRecyclerView();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDeckTitleActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(getDeckName(deckDbPath));
    }

    protected void onCreateRecyclerView() throws DatabaseException {
        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = onCreateRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.getViewTreeObserver()
                .addOnDrawListener(
                        getCalcCardIdWidth().calcIdWidth(recyclerView, binding.idHeader)
                );
    }

    @NonNull
    protected ListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new ListCardsAdapter(this);
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    /**
     * To not use parentActivityName in AndroidManifest
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.onRestart();
    }

    /* -----------------------------------------------------------------------------------------
     * Menu
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.cards_list_menu, menu);
        menuIconWithText(
                menu.findItem(R.id.new_card),
                R.drawable.ic_outline_add_24,
                "New card"
        );
        menuIconWithText(
                menu.findItem(R.id.deck_settings),
                R.drawable.ic_baseline_settings_24,
                "Settings"
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.new_card) {
            startNewCardActivity();
            return true;
        }
        if (item.getItemId() == R.id.deck_settings) {
            startDeckSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshMenuOnAppBar() {
        invalidateOptionsMenu();
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_C_23 Create a new card
     */
    protected void startNewCardActivity() {
        Intent intent = new Intent(this, DeleteCardSliderActivity.class);
        intent.putExtra(CardSliderActivity.DECK_DB_PATH, deckDbPath);
        intent.putExtra(AddCardSliderActivity.ADD_NEW_CARD, true);
        this.startActivity(intent);
    }

    protected void startDeckSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(ListCardsActivity.DECK_DB_PATH, deckDbPath);
        startActivity(intent);
    }

    public void loadItems() {
        adapter.loadItems();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected String getDeckName(@NonNull String dbPath) {
        return AppDeckDbUtil.getDeckName(dbPath);
    }

    @NonNull
    protected CalcCardIdWidth getCalcCardIdWidth() {
        return CalcCardIdWidth.getInstance();
    }

    @NonNull
    protected ActivityListCardsBinding getBinding() {
        return binding;
    }

    @NonNull
    protected RecyclerView getRecyclerView() {
        return getBinding().cardListView;
    }

    @NonNull
    public String getDeckDbPath() {
        return deckDbPath;
    }

    @NonNull
    protected ListCardsAdapter getAdapter() {
        return adapter;
    }

    @NonNull
    public ViewGroup getListCardsView() {
        return getBinding().listCards;
    }

    @NonNull
    public TableRow getHeaderTableRow() {
        return getBinding().headerTableRow;
    }
}