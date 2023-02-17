package pl.softfly.flashcards.ui.decks.standard;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.ExportImportDbUtil;
import pl.softfly.flashcards.ui.FileSyncUtil;
import pl.softfly.flashcards.ui.base.recyclerview.BaseViewAdapter;
import pl.softfly.flashcards.ui.card.NewCardActivity;
import pl.softfly.flashcards.ui.card.study.display_ratio.DisplayRatioStudyCardActivity;
import pl.softfly.flashcards.ui.card.study.exception.ExceptionStudyCardActivity;
import pl.softfly.flashcards.ui.cards.exception.ExceptionListCardsActivity;
import pl.softfly.flashcards.ui.decks.standard.dialog.RemoveDeckDialog;
import pl.softfly.flashcards.ui.decks.standard.dialog.RenameDeckDialog;
import pl.softfly.flashcards.ui.main.MainActivity;

/**
 * @author Grzegorz Ziemski
 */
public class DeckViewAdapter extends BaseViewAdapter<RecyclerView.ViewHolder> {

    protected static final int VIEW_TYPE_DECK = 1;

    private ListDecksFragment listDecksFragment;

    protected final ArrayList<Path> paths = new ArrayList<>();

    public DeckViewAdapter(
            @NonNull MainActivity activity,
            ListDecksFragment listDecksFragment
    ) {
        super(activity);
        this.listDecksFragment = listDecksFragment;
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DECK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_DECK == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deck, parent, false);
            return onCreateDeckViewHolder(view);
        } else {
            throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    protected RecyclerView.ViewHolder onCreateDeckViewHolder(View view) {
        return new DeckViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int itemPosition) {
        DeckViewHolder deckViewHolder = (DeckViewHolder) holder;

        TextView nameTextView = deckViewHolder.getNameTextView();
        nameTextView.setText(getDeckName(itemPosition));
        nameTextView.setSelected(true);

        try {
            DeckDatabase deckDb = getDeckDatabase(getFullDeckPath(itemPosition));
            calcTotal(deckViewHolder, deckDb);
            calcNew(deckViewHolder, deckDb);
            calcRev(deckViewHolder, deckDb);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    protected void calcTotal(DeckViewHolder deckViewHolder, DeckDatabase deckDb) {
        deckDb.cardDaoAsync().countByNotDeleted()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        count -> runOnUiThread(
                                () -> deckViewHolder.getTotalTextView().setText("Total: " + count),
                                this::onErrorBindView
                        ), this::onErrorBindView);
    }

    protected void calcNew(DeckViewHolder deckViewHolder, DeckDatabase deckDb) {
        deckDb.cardLearningProgressAsyncDao().countByNew()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        count -> runOnUiThread(
                                () -> deckViewHolder.getNewTextView().setText("New: " + count),
                                this::onErrorBindView
                        ), this::onErrorBindView);
    }

    protected void calcRev(DeckViewHolder deckViewHolder, DeckDatabase deckDb) {
        deckDb.cardLearningProgressAsyncDao().countByForgotten()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        count -> runOnUiThread(
                                () -> deckViewHolder.getRevTextView().setText("Rev: " + count),
                                this::onErrorBindView
                        ), this::onErrorBindView);
    }

    public String getDeckName(int itemPosition) {
        return paths.get(itemPosition)
                .getFileName()
                .toString()
                .replace(".db", "");
    }

    protected void onErrorBindView(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while loading item deck."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * New Activities
     * ----------------------------------------------------------------------------------------- */

    protected void newStudyCardActivity(int itemPosition) {
        Intent intent = new Intent(getActivity(), DisplayRatioStudyCardActivity.class);
        intent.putExtra(
                ExceptionStudyCardActivity.DECK_DB_PATH,
                getFullDeckPath(itemPosition)
        );
        getActivity().startActivity(intent);
    }

    public void newListCardsActivity(int itemPosition) {
        Intent intent = new Intent(getActivity(), ExceptionListCardsActivity.class);
        intent.putExtra(
                ExceptionListCardsActivity.DECK_DB_PATH,
                getFullDeckPath(itemPosition)
        );
        getActivity().startActivity(intent);
    }

    public void newNewCardActivity(int itemPosition) {
        Intent intent = new Intent(getActivity(), NewCardActivity.class);
        intent.putExtra(
                ExceptionListCardsActivity.DECK_DB_PATH,
                getFullDeckPath(itemPosition)
        );
        getActivity().startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void onItemClick(int position) {
        newStudyCardActivity(position);
    }

    public void showDeleteDeckDialog(int itemPosition) {
        RemoveDeckDialog dialog = new RemoveDeckDialog(getFullDeckPath(itemPosition), this);
        dialog.show(getActivity().getSupportFragmentManager(), "RemoveDeck");
    }

    public void showRenameDeckDialog(int itemPosition) {
        RenameDeckDialog dialog = new RenameDeckDialog(Paths.get(getFullDeckPath(itemPosition)));
        dialog.show(getActivity().getSupportFragmentManager(), "RemoveDeck");
    }

    public void launchExportDb(int itemPosition) {
        getExportImportDbUtil().launchExportDb(getFullDeckPath(itemPosition));
    }

    public void launchExportToFile(int itemPosition) {
        getFileSyncUtil().launchExportToFile(getFullDeckPath(itemPosition));
    }

    public void launchSyncFile(int itemPosition) {
        getFileSyncUtil().launchSyncFile(getFullDeckPath(itemPosition));
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    public void refreshItems() {
        loadItems(getCurrentFolder());
    }

    public void loadItems(@NonNull Path folder) {
        if (countSeparators(getRootFolder()) > countSeparators(folder)) {
            throw new RuntimeException("Folders lower than the root folder for the storage databases cannot be opened.");
        } else {
            paths.clear();
            paths.addAll(getStorageDb().listDatabases(folder));
            runOnUiThread(() -> notifyDataSetChanged(), this::onErrorBindView);
        }
    }

    @Deprecated
    protected long countSeparators(File file) {
        return file.getPath().chars().filter(ch -> ch == File.separatorChar).count();
    }

    protected long countSeparators(Path path) {
        return path.toString().chars().filter(ch -> ch == File.separatorChar).count();
    }

    protected String getFullDeckPath(int itemPosition) {
        return paths.get(itemPosition).toString();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    protected Path getRootFolder() {
        return getStorageDb().getDbFolderPath();
    }

    protected FileSyncUtil getFileSyncUtil() {
        return getActivity().getFileSyncUtil();
    }

    protected ExportImportDbUtil getExportImportDbUtil() {
        return getActivity().getAllDecksFragment().getExportImportDbUtil();
    }

    protected Path getPath(int position) {
        return paths.get(position);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public Path getCurrentFolder() {
        return getRootFolder();
    }

    public MainActivity getActivity() {
        return (MainActivity) super.getActivity();
    }

    protected ListDecksFragment getFragment() {
        return listDecksFragment;
    }
}
