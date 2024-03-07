package pl.gocards.ui.cards.xml.list.select;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.xml.list.drag_swipe.DragSwipeListCardsActivity;

/**
 * C_R_08 Select (cut) the card
 * @author Grzegorz Ziemski
 */
public class SelectListCardsActivity extends DragSwipeListCardsActivity {

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected SelectListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new SelectListCardsAdapter(this);
    }

    @NonNull
    @Override
    protected SelectCardTouchHelper onCreateTouchHelper() {
        return new SelectCardTouchHelper(getAdapter());
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu);
        menuIconWithText(
                menu.findItem(R.id.deselect_all_cards),
                R.drawable.ic_baseline_deselect_24
        );
        menuIconWithText(
                menu.findItem(R.id.delete_selected_cards),
                R.drawable.ic_baseline_delete_sweep_24
        );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        if (getAdapter().isSelectionMode()) {
            menu.findItem(R.id.deselect_all_cards).setVisible(true);
            menu.findItem(R.id.delete_selected_cards).setVisible(true);
            menu.findItem(R.id.new_card).setVisible(false);
            menu.findItem(R.id.deck_settings).setVisible(false);
            menu.findItem(R.id.sync_excel).setVisible(false);
            menu.findItem(R.id.export_excel).setVisible(false);
            menu.findItem(R.id.export_csv).setVisible(false);
        } else {
            menu.findItem(R.id.deselect_all_cards).setVisible(false);
            menu.findItem(R.id.delete_selected_cards).setVisible(false);
            menu.findItem(R.id.new_card).setVisible(true);
            menu.findItem(R.id.deck_settings).setVisible(true);
            menu.findItem(R.id.sync_excel).setVisible(true);
            menu.findItem(R.id.export_excel).setVisible(true);
            menu.findItem(R.id.export_csv).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deselect_all_cards -> {
                getAdapter().onClickDeselectAll();
                return true;
            }
            case R.id.delete_selected_cards -> {
                getAdapter().onClickDeleteSelected();
                return true;
            }
            default -> {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected SelectListCardsAdapter getAdapter() {
        return (SelectListCardsAdapter) super.getAdapter();
    }
}