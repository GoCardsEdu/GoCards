package pl.softfly.flashcards.ui.decks.folder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pl.softfly.flashcards.R;
import pl.softfly.flashcards.ui.decks.folder.dialog.DeleteFolderDialog;
import pl.softfly.flashcards.ui.decks.folder.dialog.RenameFolderDialog;
import pl.softfly.flashcards.ui.decks.standard.DeckViewAdapter;
import pl.softfly.flashcards.ui.decks.standard.dialog.RenameDeckDialog;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class FolderDeckViewAdapter extends DeckViewAdapter {

    private static final int VIEW_TYPE_FOLDER = 2;

    private Path currentFolder = getRootFolder();

    private final MutableLiveData<String> cutPathLiveData = new MutableLiveData<>();

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public FolderDeckViewAdapter(
            @NonNull MainActivity activity,
            ListFoldersDecksFragment listDecksFragment
    ) {
        super(activity, listDecksFragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_FOLDER == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_folder, parent, false);
            return onCreateFolderViewHolder(view);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    protected RecyclerView.ViewHolder onCreateFolderViewHolder(View view) {
        return new FolderDeckViewHolder(view, this);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDeckViewHolder(View view) {
        return new FolderViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (VIEW_TYPE_FOLDER == getItemViewType(position)) {
            FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
            folderViewHolder.nameTextView.setText(paths.get(position).getFileName().toString());
            folderViewHolder.nameTextView.setSelected(true); // fixes marquee
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public int getItemViewType(int position) {
        if (isFolder(position)) {
            return VIEW_TYPE_FOLDER;
        } else {
            return super.getItemViewType(position);
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void loadItems(@NonNull Path folder) {
        if (countSeparators(getRootFolder()) > countSeparators(folder)) {
            throw new RuntimeException("Folders lower than the root folder for the storage databases cannot be opened.");
        } else {
            currentFolder = folder;
            paths.clear();
            paths.addAll(getStorageDb().listFolders(folder));
            paths.addAll(getStorageDb().listDatabases(folder));
            runOnUiThread(() -> notifyDataSetChanged(), this::onErrorBindView);
            updatePathTextView();
        }
    }

    protected void updatePathTextView() {
        runOnUiThread(() -> {
            String newPath = getCurrentFolder().toString()
                    .replace(getRootFolder().toString(), "")
                    .replace("/", " › ");
            getFragment().getPathTextView().setText("This Device" + newPath);
        }, this::onErrorBindView);
    }

    protected boolean isRootFolder() {
        return getRootFolder().equals(currentFolder);
    }

    protected boolean isFolder(int position) {
        Path path = paths.get(position);
        return Files.isDirectory(path);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onItemClick(int position) {
        if (isFolder(position)) {
            openFolder(position);
        } else {
            super.onItemClick(position);
        }
    }

    protected void openFolder(int position) {
        loadItems(paths.get(position));
        if (isRootFolder()) {
            getActivity().hideBackArrow();
        } else {
            getActivity().showBackArrow();
        }
    }

    public void showDeleteFolderDialog(int position) {
        DeleteFolderDialog dialog = new DeleteFolderDialog(paths.get(position), this);
        dialog.show(getActivity().getSupportFragmentManager(), DeleteFolderDialog.class.getSimpleName());
    }

    public void showRenameFolderDialog(int position) {
        RenameFolderDialog dialog = new RenameFolderDialog(paths.get(position));
        dialog.show(getActivity().getSupportFragmentManager(),  RenameFolderDialog.class.getSimpleName());
    }

    public void cut(int itemPosition) {
        cutPathLiveData.postValue(getFullDeckPath(itemPosition));
    }

    public void goFolderUp() {
        loadItems(currentFolder.getParent());
        if (isRootFolder()) {
            getActivity().hideBackArrow();
        } else {
            getActivity().showBackArrow();
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @Override
    public Path getCurrentFolder() {
        return currentFolder;
    }

    public MutableLiveData<String> getCutPathLiveData() {
        return cutPathLiveData;
    }

    protected ListFoldersDecksFragment getFragment() {
        return (ListFoldersDecksFragment) super.getFragment();
    }

}
