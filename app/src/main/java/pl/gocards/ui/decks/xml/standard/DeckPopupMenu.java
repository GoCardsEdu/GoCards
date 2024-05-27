package pl.gocards.ui.decks.xml.standard;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.gocards.R;

/**
 * @author Grzegorz Ziemski
 */
public class DeckPopupMenu {

    private final DeckViewAdapter adapter;

    private final int position;

    private final View view;

    public DeckPopupMenu(
            DeckViewAdapter adapter,
            int position,
            View view
    ) {
        this.adapter = adapter;
        this.position = position;
        this.view = view;
    }

    protected PopupMenu createPopupMenu() {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.decks_list_popup_deck, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuMoreClick);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
        return popup;
    }

    public void showPopupMenu() {
        createPopupMenu().show();
    }

    @SuppressLint("NonConstantResourceId")
    protected boolean onMenuMoreClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_cards -> {
                getAdapter().newListCardsActivity(position);
                return true;
            }
            case R.id.add_card -> {
                getAdapter().newNewCardActivity(position);
                return true;
            }
            case R.id.delete_deck -> {
                getAdapter().showDeleteDeckDialog(position);
                return true;
            }
            case R.id.rename_deck -> {
                getAdapter().showRenameDeckDialog(position);
                return true;
            }
            case R.id.more -> {
                DeckBottomMenu deckBottomMenu = new DeckBottomMenu(getAdapter(), position);
                deckBottomMenu.show(requireActivity().getSupportFragmentManager(), "DeckBottomMenu");
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

    @NonNull
    protected DeckViewAdapter getAdapter() {
        return adapter;
    }

    @NonNull
    private AppCompatActivity requireActivity() {
        return getAdapter().requireActivity();
    }

    protected int getPosition() {
        return position;
    }
}
