package pl.softfly.flashcards.ui.cards.select;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.cards.drag_swipe.DragSwipeListCardsActivity;

public class SelectListCardsActivity extends DragSwipeListCardsActivity {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected SelectCardBaseViewAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new SelectCardBaseViewAdapter(this, getDeckDbPath());
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
                menu.findItem(R.id.deselect_all),
                R.drawable.ic_baseline_deselect_24,
                "Deselect all"
        );
        menuIconWithText(
                menu.findItem(R.id.delete_selected),
                R.drawable.ic_baseline_delete_sweep_24,
                "Delete cards"
        );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (getAdapter().isSelectionMode()) {
            menu.findItem(R.id.deselect_all).setVisible(true);
            menu.findItem(R.id.delete_selected).setVisible(true);
            menu.findItem(R.id.new_card).setVisible(false);
            menu.findItem(R.id.settings).setVisible(false);
            menu.findItem(R.id.sync_excel).setVisible(false);
            menu.findItem(R.id.export_excel).setVisible(false);
        } else {
            menu.findItem(R.id.deselect_all).setVisible(false);
            menu.findItem(R.id.delete_selected).setVisible(false);
            menu.findItem(R.id.new_card).setVisible(true);
            menu.findItem(R.id.settings).setVisible(true);
            menu.findItem(R.id.sync_excel).setVisible(true);
            menu.findItem(R.id.export_excel).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deselect_all:
                getAdapter().onClickDeselectAll();
                return true;
            case R.id.delete_selected:
                getAdapter().onClickDeleteSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @Override
    public SelectCardBaseViewAdapter getAdapter() {
        return (SelectCardBaseViewAdapter) super.getAdapter();
    }
}