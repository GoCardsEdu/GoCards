package pl.gocards.ui.cards.xml.list.drag_swipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.xml.list.standard.ListCardsActivity;

/**
 * C_U_03 Dragging the card to another position.
 * C_D_04 Delete the card by swiping.
 *
 * @author Grzegorz Ziemski
 */
public class DragSwipeListCardsActivity extends ListCardsActivity {

    private boolean dragSwipeEnabled = true;

    private ItemTouchHelper itemTouchHelper;

    /* -----------------------------------------------------------------------------------------
     * OnCreate
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreateRecyclerView() throws DatabaseException {
        super.onCreateRecyclerView();
        itemTouchHelper = new ItemTouchHelper(onCreateTouchHelper());
        getAdapter().setTouchHelper(itemTouchHelper);
        setDragSwipeEnabled(dragSwipeEnabled);
    }

    @NonNull
    @Override
    protected DragSwipeListCardsAdapter onCreateRecyclerViewAdapter() throws DatabaseException {
        return new DragSwipeListCardsAdapter(this);
    }

    @NonNull
    protected DragSwipeCardTouchHelper onCreateTouchHelper() {
        return new DragSwipeCardTouchHelper(getAdapter());
    }

    /* -----------------------------------------------------------------------------------------
     * Drag/swipe features
     * ----------------------------------------------------------------------------------------- */

    protected void setDragSwipeEnabled(boolean enabled) {
        if (itemTouchHelper != null) {
            if (enabled) {
                itemTouchHelper.attachToRecyclerView(getRecyclerView());
            } else {
                itemTouchHelper.attachToRecyclerView(null);
            }
        }
        this.dragSwipeEnabled = enabled;
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected DragSwipeListCardsAdapter getAdapter() {
        return (DragSwipeListCardsAdapter) super.getAdapter();
    }
}