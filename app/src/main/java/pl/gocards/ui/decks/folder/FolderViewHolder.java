package pl.gocards.ui.decks.folder;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pl.gocards.R;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
public class FolderViewHolder extends BaseViewHolder implements View.OnClickListener {

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private View moreTextView;

    public FolderViewHolder(@NonNull View itemView, @NonNull FolderDeckViewAdapter adapter) {
        super(itemView, adapter);
        initMoreImageView();
        itemView.setOnClickListener(this);
    }

    protected void initMoreImageView() {
        moreTextView = getMoreImageView();
        moreTextView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), moreTextView);
            popup.getMenuInflater().inflate(R.menu.decks_list_popup_folder, popup.getMenu());
            popup.setOnMenuItemClickListener(this::onMenuPopupClick);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popup.setForceShowIcon(true);
            popup.show();
        });
    }

    @SuppressLint("NonConstantResourceId")
    protected boolean onMenuPopupClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_folder -> {
                getAdapter().showRenameFolderDialog(getBindingAdapterPosition());
                return true;
            }
            case R.id.delete_folder -> {
                getAdapter().showDeleteFolderDialog(getBindingAdapterPosition());
                return true;
            }
            case R.id.cut_folder -> {
                getAdapter().cut(getBindingAdapterPosition());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        getAdapter().onItemClick(getBindingAdapterPosition());
    }

    @NonNull
    public FolderDeckViewAdapter getAdapter() {
        return (FolderDeckViewAdapter) super.getAdapter();
    }

    protected TextView getNameTextView() {
        return itemView.findViewById(R.id.nameTextView);
    }
    protected View getMoreImageView() {
        return itemView.findViewById(R.id.moreImageView);
    }
}