package pl.gocards.ui.cards.list.standard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import pl.gocards.R;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;

/**
 * C_R_01 Display all cards
 * @author Grzegorz Ziemski
 */
public class CardViewHolder extends BaseViewHolder
        implements View.OnTouchListener, GestureDetector.OnGestureListener {

    @NonNull
    private final GestureDetector gestureDetector;
    @NonNull
    private final ItemCardBinding binding;

    /**
     * Needed to define where the popup menu should be displayed.
     */
    private float lastTouchX;

    /**
     * Needed to define where the popup menu should be displayed.
     */
    private float lastTouchY;

    public CardViewHolder(@NonNull ItemCardBinding binding, @NonNull ListCardsAdapter adapter) {
        super(binding.getRoot(), adapter);
        this.binding = binding;
        gestureDetector = new GestureDetector(itemView.getContext(), this);
        itemView.setOnTouchListener(this);
    }

    /* -----------------------------------------------------------------------------------------
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     * ----------------------------------------------------------------------------------------- */

    protected interface CreatePopupMenu {
        /** @noinspection unused*/
        void create(PopupMenu popupMenu);
    }

    /**
     * Show popup at last tap location.
     */
    @SuppressLint("RestrictedApi")
    public void showPopupMenu(@NonNull CreatePopupMenu createPopupMenu) {
        // A view that allows to display a popup with coordinates.
        final ViewGroup layout = requireActivity().getListCardsView();
        final View view = createParentViewPopupMenu();
        layout.addView(view);

        PopupMenu popupMenu = new PopupMenu(
                itemView.getContext(),
                view,
                Gravity.TOP | Gravity.START
        );
        createPopupMenu.create(popupMenu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popupMenu.setForceShowIcon(true);
        popupMenu.setOnDismissListener(menu -> {
            layout.removeView(view);
            unfocusItemView();
        });
        popupMenu.show();
    }

    /**
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     */
    protected void createSingleTapMenu(@NonNull PopupMenu popupMenu) {
        popupMenu.getMenuInflater().inflate(R.menu.cards_list_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(CardViewHolder.this::onPopupMenuItemClick);
    }

    /**
     * A view that allows to display a popup with coordinates.
     */
    @NonNull
    protected View createParentViewPopupMenu() {
        final View view = new View(requireActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        view.setX(getLastTouchX());

        final float maxY = itemView.getY() + itemView.getHeight();
        view.setY(Math.min(getLastTouchY(), maxY));
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    protected boolean onPopupMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_card -> {
                getAdapter().startEditCardActivity(getBindingAdapterPosition());
                return true;
            }
            case R.id.add_card -> {
                getAdapter().startNewCardActivity(getBindingAdapterPosition());
                return true;
            }
            case R.id.delete_card -> {
                getAdapter().onClickDeleteCard(getBindingAdapterPosition());
                return true;
            }
        }
        throw new UnsupportedOperationException(
                String.format(
                        "Not implemented itemId=\"%d\" name=\"%s\" title=\"%s\"",
                        item.getItemId(),
                        requireActivity().getResources().getResourceEntryName(item.getItemId()),
                        item.getTitle()
                )
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Implementation of GestureDetector
     * ----------------------------------------------------------------------------------------- */

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {}

    /**
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     */
    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent event) {
        lastTouchX = event.getRawX();
        lastTouchY = event.getRawY();
        focusSingleTapItemView();
        showPopupMenu(this::createSingleTapMenu);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false; // It might be used when ItemTouchHelper is not attached.
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        // Do nothing
    }

    @Override
    public boolean onFling(MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, @NonNull MotionEvent event) {
        lastTouchX = event.getRawX();
        lastTouchY = event.getRawY();
        gestureDetector.onTouchEvent(event);
        return false;
    }

    /* -----------------------------------------------------------------------------------------
     * Change the look of the view.
     * ----------------------------------------------------------------------------------------- */

    public void focusSingleTapItemView() {
        itemView.setActivated(true);
        itemView.setBackgroundColor(
                MaterialColors.getColor(itemView, R.attr.colorItemActive)
        );
    }

    public void unfocusItemView() {
        this.itemView.setSelected(false);
        this.itemView.setBackgroundColor(0);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected ItemCardBinding getBinding() {
        return binding;
    }

    @NonNull
    protected TextView getIdTextView() {
        return binding.id;
    }

    @NonNull
    protected TextView getTermTextView() {
        return binding.term;
    }

    @NonNull
    protected TextView getDefinitionTextView() {
        return binding.definition;
    }

    private float getLastTouchX() {
        return lastTouchX;
    }

    private float getLastTouchY() {
        return lastTouchY;
    }

    @NonNull
    @Override
    protected ListCardsAdapter getAdapter() {
        return (ListCardsAdapter) super.getAdapter();
    }

    @NonNull
    @Override
    protected ListCardsActivity requireActivity() {
        return (ListCardsActivity) super.requireActivity();
    }
}
