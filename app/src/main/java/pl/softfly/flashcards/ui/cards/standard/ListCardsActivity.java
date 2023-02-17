package pl.softfly.flashcards.ui.cards.standard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.elevation.SurfaceColors;

import java.util.Objects;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.databinding.ActivityListCardsBinding;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.base.IconInTopbarActivity;
import pl.softfly.flashcards.ui.card.NewCardActivity;
import pl.softfly.flashcards.ui.deck.settings.DeckSettingsActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ListCardsActivity extends IconInTopbarActivity {

    public static final String DECK_DB_PATH = "deckDbPath";
    private String deckDbPath;
    private CardBaseViewAdapter adapter;
    private ActivityListCardsBinding binding;

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarSameColours();

        binding = ActivityListCardsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Remove shadow under bar
        getSupportActionBar().setElevation(0);
        getHeaderTableRow().setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this));

        Intent intent = getIntent();
        deckDbPath = intent.getStringExtra(DECK_DB_PATH);
        Objects.requireNonNull(deckDbPath);

        try {
            onCreateRecyclerView();
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
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

    protected CardBaseViewAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new CardBaseViewAdapter(this, deckDbPath);
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onRestart() {
        super.onRestart();
        adapter.onRestart();
    }

    /**
     * To not use parentActivityName in AndroidManifest
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_cards, menu);
        menuIconWithText(
                menu.findItem(R.id.new_card),
                R.drawable.ic_outline_add_24,
                "New card"
        );
        menuIconWithText(
                menu.findItem(R.id.settings),
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
        if (item.getItemId() == R.id.settings) {
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

    protected void startNewCardActivity() {
        Intent intent = new Intent(this, NewCardActivity.class);
        intent.putExtra(NewCardActivity.DECK_DB_PATH, deckDbPath);
        this.startActivity(intent);
    }

    protected void startDeckSettingsActivity() {
        Intent intent = new Intent(this, DeckSettingsActivity.class);
        intent.putExtra(NewCardActivity.DECK_DB_PATH, deckDbPath);
        startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    protected CalcCardIdWidth getCalcCardIdWidth() {
        return CalcCardIdWidth.getInstance();
    }

    protected RecyclerView getRecyclerView() {
        return binding.cardListView;
    }

    protected String getDeckDbPath() {
        return deckDbPath;
    }

    protected CardBaseViewAdapter getAdapter() {
        return adapter;
    }

    public ViewGroup getListCardsView() {
        return binding.listCards;
    }

    public TextView getIdHeader() {
        return binding.idHeader;
    }

    protected TableRow getHeaderTableRow() {
        return binding.headerTableRow;
    }
}