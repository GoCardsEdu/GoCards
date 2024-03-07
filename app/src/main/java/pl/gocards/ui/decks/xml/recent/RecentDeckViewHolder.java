package pl.gocards.ui.decks.xml.recent;

import android.os.Build;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.decks.xml.standard.DeckViewAdapter;
import pl.gocards.ui.decks.xml.standard.DeckViewHolder;

/**
 * D_R_05 Show recent used decks
 * @author Grzegorz Ziemski
 */
public class RecentDeckViewHolder extends DeckViewHolder {

    public RecentDeckViewHolder(@NonNull View itemView, @NonNull DeckViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void initMoreImageView() {
        View moreImageView = itemView.findViewById(R.id.moreImageView);
        moreImageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), moreImageView);
            popup.getMenuInflater().inflate(R.menu.decks_list_popup_deck, popup.getMenu());
            popup.getMenu().removeItem(R.id.cut_card);
            popup.setOnMenuItemClickListener(this::onMenuMoreClick);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
            popup.show();
        });
    }
}