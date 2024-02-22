package pl.gocards.ui.decks.folder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import pl.gocards.R;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.decks.folder.dialog.DeleteFolderDialog;
import pl.gocards.ui.decks.folder.dialog.RenameFolderDialog;
import pl.gocards.ui.decks.standard.DeckViewAdapter;
import pl.gocards.ui.decks.standard.DeckViewHolder;

/**
 * F_R_01 Show folders
 * @author Grzegorz Ziemski
 */
public class FolderDeckViewAdapter extends DeckViewAdapter {

    private static final int VIEW_TYPE_FOLDER = 2;

    @NonNull
    private Path currentFolder = getRootFolder();

    @NonNull
    private final MutableLiveData<String> cutPathLiveData = new MutableLiveData<>();

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    public FolderDeckViewAdapter(@NonNull ListFoldersDecksFragment listDecksFragment) {
        super(listDecksFragment);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_FOLDER == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_folder, parent, false);
            return onCreateFolderViewHolder(view);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @NonNull
    protected FolderViewHolder onCreateFolderViewHolder(@NonNull View view) {
        return new FolderViewHolder(view, this);
    }

    @NonNull
    @Override
    protected DeckViewHolder onCreateDeckViewHolder(@NonNull View view) {
        return new FolderDeckViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (VIEW_TYPE_FOLDER == getItemViewType(position)) {
            FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
            folderViewHolder.getNameTextView().setText(paths.get(position).getFileName().toString());
            folderViewHolder.getNameTextView().setSelected(true); // fixes marquee
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
            throw new UnsupportedOperationException("Folders lower than the root folder for the storage databases cannot be opened.");
        } else {
            currentFolder = folder;
            paths.clear();
            try {
                paths.addAll(getDeckDatabaseUtil().listFolders(folder));
                paths.addAll(getDeckDatabaseUtil().listDatabases(folder));
            } catch (IOException e) {
                this.onErrorItemLoading(e);
            }
            runOnUiThread(this::notifyDataSetChanged, this::onErrorItemLoading);
            updatePathTextView();
        }
    }

    @SuppressLint("SetTextI18n")
    protected void updatePathTextView() {
        runOnUiThread(() -> {
            String newPath = getCurrentFolder().toString()
                    .replace(getRootFolder().toString(), "")
                    .replace("/", " › ");
            getFragment().getPathTextView().setText(getString(R.string.decks_list_root_path) + newPath);
        }, this::onErrorItemLoading);
    }

    public boolean isRootFolder() {
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

    /**
     * F_R_02 Open folder
     */
    protected void openFolder(int position) {
        loadItems(paths.get(position));
        refreshTopBar();
    }

    /**
     * D_R_09 Cut the deck
     * F_C_06 Cut the folder
     */
    public void cut(int itemPosition) {
        cutPathLiveData.postValue(getFullDeckPath(itemPosition));
    }

    /**
     * F_D_05 Delete the folder
     */
    public void showDeleteFolderDialog(int position) {
        DeleteFolderDialog dialog = new DeleteFolderDialog(requireActivity(), paths.get(position));
        dialog.show(requireActivity().getSupportFragmentManager(), DeleteFolderDialog.class.getSimpleName());
    }

    /**
     * F_U_04 Rename the folder
     */
    public void showRenameFolderDialog(int position) {
        RenameFolderDialog dialog = new RenameFolderDialog(requireActivity(), paths.get(position));
        dialog.show(requireActivity().getSupportFragmentManager(), RenameFolderDialog.class.getSimpleName());
    }

    public void goFolderUp() {
        loadItems(currentFolder.getParent());
        refreshTopBar();
    }

    public void refreshTopBar() {
        if (isRootFolder()) {
            requireActivity().hideBackArrow();
            getSupportActionBar().setTitle(getString(R.string.app_name));
        } else {
            requireActivity().showBackArrow();
            getSupportActionBar().setTitle(getCurrentFolder().getFileName().toString());
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public Path getCurrentFolder() {
        return currentFolder;
    }

    @NonNull
    public MutableLiveData<String> getCutPathLiveData() {
        return cutPathLiveData;
    }

    @Override
    protected ListFoldersDecksFragment getFragment() {
        return (ListFoldersDecksFragment) super.getFragment();
    }

    @NonNull
    protected ActionBar getSupportActionBar() {
        return Objects.requireNonNull(requireActivity().getSupportActionBar());
    }
}
