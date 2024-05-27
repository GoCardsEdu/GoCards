package pl.gocards.ui.cards.xml.list.standard.popup.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Grzegorz Ziemski
 */
public abstract class PopupMenuAtPos {

    private final AppCompatActivity activity;
    private final View itemView;
    private final int position;
    private final float lastTouchX;
    private final float lastTouchY;
    private final Runnable onDismiss;

    public PopupMenuAtPos(
            AppCompatActivity activity,
            View itemView,
            int position,
            float lastTouchX,
            float lastTouchY,
            Runnable onDismiss
    ) {
        this.activity = activity;
        this.itemView = itemView;
        this.position = position;
        this.lastTouchX = lastTouchX;
        this.lastTouchY = lastTouchY;
        this.onDismiss = onDismiss;
    }

    public void showPopupMenu() {
        new ShowPopupMenuAtPos().showPopupMenu(
                this::createPopupMenu,
                activity,
                itemView,
                lastTouchX,
                lastTouchY,
                onDismiss
        );
    }

    protected abstract void createPopupMenu(@NonNull PopupMenu popupMenu);

    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        throw new UnsupportedOperationException(
                String.format(
                        "Not implemented itemId=\"%d\" name=\"%s\" title=\"%s\"",
                        item.getItemId(),
                        activity.getResources().getResourceEntryName(item.getItemId()),
                        item.getTitle()
                )
        );
    }

    protected AppCompatActivity getActivity() {
        return activity;
    }

    protected int getPosition() {
        return position;
    }
}
