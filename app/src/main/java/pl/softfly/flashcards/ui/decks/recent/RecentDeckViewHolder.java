package pl.softfly.flashcards.ui.decks.recent;

import android.os.Build;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.decks.standard.DeckViewAdapter;
import pl.softfly.flashcards.ui.decks.standard.DeckViewHolder;

/**
 * @author Grzegorz Ziemski
 */
public class RecentDeckViewHolder extends DeckViewHolder {

    public RecentDeckViewHolder(@NonNull View itemView, DeckViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void initMoreImageView() {
        View moreImageView = itemView.findViewById(R.id.moreImageView);
        moreImageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), moreImageView);
            popup.getMenuInflater().inflate(R.menu.popup_menu_deck, popup.getMenu());
            popup.getMenu().removeItem(R.id.cut);
            popup.setOnMenuItemClickListener(this::onMenuMoreClick);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
            popup.show();
        });
    }
}