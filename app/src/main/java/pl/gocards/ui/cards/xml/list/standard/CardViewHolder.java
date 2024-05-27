package pl.gocards.ui.cards.xml.list.standard;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import pl.gocards.R;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.cards.xml.list.standard.popup.menu.CardPopupMenu;

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

    /**
     * C_02_01 When no card is selected and tap on the card, show the popup menu.
     */
    protected void showSingleTapMenu() {
        new CardPopupMenu(requireActivity(), this).showPopupMenu();
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
        showSingleTapMenu();
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

    public float getLastTouchX() {
        return lastTouchX;
    }

    public float getLastTouchY() {
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
