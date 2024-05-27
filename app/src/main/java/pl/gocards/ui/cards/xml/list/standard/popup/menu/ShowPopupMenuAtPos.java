package pl.gocards.ui.cards.xml.list.standard.popup.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Grzegorz Ziemski
 */
public class ShowPopupMenuAtPos {

    @SuppressLint("RestrictedApi")
    public void showPopupMenu(
            @NonNull CreatePopupMenu createPopupMenu,
            @NonNull AppCompatActivity activity,
            @NonNull View itemView,
            float lastTouchX,
            float lastTouchY,
            @Nullable Runnable onDismiss
    ) {
        final View view = createParentViewPopupMenu(
                activity,
                itemView,
                lastTouchX,
                lastTouchY
        );

        ViewGroup root = activity.findViewById(android.R.id.content);
        root.addView(view);

        PopupMenu popupMenu = new PopupMenu(
                view.getContext(),
                view,
                Gravity.TOP | Gravity.START
        );
        createPopupMenu.create(popupMenu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            popupMenu.setForceShowIcon(true);

        popupMenu.setOnDismissListener(menu -> {
            root.removeView(view);
            if (onDismiss != null) {
                onDismiss.run();
            }
        });

        popupMenu.show();
    }

    /**
     * A view that allows to display a popup with coordinates.
     */
    @NonNull
    private View createParentViewPopupMenu(
            Context context,
            View itemView,
            float lastTouchX,
            float lastTouchY
    ) {
        final View newView = new View(context);
        newView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        newView.setX(lastTouchX);

        final float maxY = itemView.getY() + itemView.getHeight();
        newView.setY(Math.min(lastTouchY, maxY));
        return newView;
    }

    public interface CreatePopupMenu {
        void create(PopupMenu popupMenu);
    }
}
