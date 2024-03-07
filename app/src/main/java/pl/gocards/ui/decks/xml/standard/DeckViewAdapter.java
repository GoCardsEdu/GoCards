package pl.gocards.ui.decks.xml.standard;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.color.MaterialColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.filesync.FileSyncLauncher;
import pl.gocards.filesync.FileSyncProLauncher;
import pl.gocards.ui.ExportImportDbUtil;
import pl.gocards.ui.base.recyclerview.BaseViewAdapter;
import pl.gocards.ui.base.recyclerview.BaseViewHolder;
import pl.gocards.ui.cards.xml.list.exception.ExceptionListCardsActivity;
import pl.gocards.ui.cards.xml.list.standard.ListCardsActivity;
import pl.gocards.ui.cards.xml.slider.add.AddCardSliderActivity;
import pl.gocards.ui.cards.xml.slider.delete.DeleteCardSliderActivity;
import pl.gocards.ui.cards.xml.slider.slider.CardSliderActivity;
import pl.gocards.ui.cards.xml.study.exception.ExceptionStudyCardActivity;
import pl.gocards.ui.decks.xml.standard.dialog.DeleteDeckDialog;
import pl.gocards.ui.decks.xml.standard.dialog.RenameDeckDialog;
import pl.gocards.ui.main.xml.MainActivity;
import pl.gocards.ui.settings.SettingsActivity;

/**
 * D_R_02 Show all decks
 * @author Grzegorz Ziemski
 */
public class DeckViewAdapter extends BaseViewAdapter<BaseViewHolder> {

    private static final String TAG = "DeckViewAdapter";

    protected static final int VIEW_TYPE_DECK = 1;

    @NonNull
    private final ListDecksFragment listDecksFragment;

    @NonNull
    protected final List<Path> paths = new ArrayList<>();

    public DeckViewAdapter(@NonNull ListDecksFragment listDecksFragment) {
        super(listDecksFragment.requireMainActivity());
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
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_DECK == viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_deck, parent, false);
            return onCreateDeckViewHolder(view);
        } else {
            throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @NonNull
    protected DeckViewHolder onCreateDeckViewHolder(@NonNull View view) {
        return new DeckViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int itemPosition) {
        DeckViewHolder deckViewHolder = (DeckViewHolder) holder;

        TextView nameTextView = deckViewHolder.getNameTextView();
        nameTextView.setText(getDeckName(itemPosition));
        nameTextView.setSelected(true);

        deckViewHolder.setNoCardsToRepeat(true);
        try {
            DeckDatabase deckDb = getDeckDb(getFullDeckPath(itemPosition));
            calcTotal(deckViewHolder, deckDb);
            calcNew(deckViewHolder, deckDb);
            calcRev(deckViewHolder, deckDb);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    protected void calcTotal(@NonNull DeckViewHolder deckViewHolder, @NonNull DeckDatabase deckDb) {
        Disposable disposable = deckDb.cardRxDao().countByNotDeleted()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(total -> runOnUiThread(
                        () -> deckViewHolder.getTotalTextView().setText(
                                String.format(Locale.getDefault(), getString(R.string.decks_list_total), total)
                        ),
                        this::onErrorItemLoading
                ))
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorItemLoadingNoDialog);
        addToDisposable(disposable);
    }

    @SuppressLint("CheckResult")
    private void calcNew(@NonNull DeckViewHolder deckViewHolder, @NonNull DeckDatabase deckDb) {
        Disposable disposable = deckDb.cardLearningProgressRxDao().countByNew()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(
                        countByNew -> runOnUiThread(
                                () -> calcNewDoOnSuccess(deckViewHolder, countByNew),
                                this::onErrorItemLoading
                        )
                ).ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorItemLoadingNoDialog);
        addToDisposable(disposable);
    }

    private void calcNewDoOnSuccess(@NonNull DeckViewHolder deckViewHolder, int countByNew) {
        if (countByNew > 0) {
            deckViewHolder.setNoCardsToRepeat(false);

            int intColor = MaterialColors.getColor(deckViewHolder.itemView, R.attr.colorItemRememberedCards);
            String hexColor = String.format("#%06X", (0xFFFFFF & intColor));

            String text = String.format(
                    Locale.getDefault(),
                    "<font color=\"%s\">" + getString(R.string.decks_list_new) + "</font>",
                    hexColor,
                    countByNew
            );
            Spanned html = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
            deckViewHolder.getNewTextView().setText(html);
        } else {
            String text = String.format(Locale.getDefault(), getString(R.string.decks_list_new_zero));
            deckViewHolder.getNewTextView().setText(text);
        }
    }

    @SuppressLint("CheckResult")
    private void calcRev(@NonNull DeckViewHolder deckViewHolder, @NonNull DeckDatabase deckDb) {
        Disposable disposable = deckDb.cardLearningProgressRxDao().countByForgotten()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(
                        countByForgotten -> runOnUiThread(
                                () -> calcRevDoOnSuccess(deckViewHolder, countByForgotten),
                                this::onErrorItemLoading
                        )
                )
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorItemLoadingNoDialog);
        addToDisposable(disposable);
    }

    private void calcRevDoOnSuccess(@NonNull DeckViewHolder deckViewHolder, int countByForgotten) {
        if (countByForgotten > 0) {
            deckViewHolder.setNoCardsToRepeat(false);

            int intColor = MaterialColors.getColor(deckViewHolder.itemView, R.attr.colorItemForgottenCard);
            String hexColor = String.format("#%06X", (0xFFFFFF & intColor));

            String text = String.format(
                    Locale.getDefault(),
                    "<font color=\"%s\">" + getString(R.string.decks_list_review) + "</font>",
                    hexColor,
                    countByForgotten
            );
            Spanned html = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
            deckViewHolder.getRevTextView().setText(html);
        } else {
            String text = String.format(Locale.getDefault(), getString(R.string.decks_list_review_zero));
            deckViewHolder.getRevTextView().setText(text);
        }
    }

    @NonNull
    public String getDeckName(int itemPosition) {
        return paths.get(itemPosition)
                .getFileName()
                .toString()
                .replace(".db", "");
    }

    protected void onErrorItemLoading(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(), TAG,
                "Error while loading item deck."
        );
    }

    /**
     * Without GUI, because generates too many errors on the GUI.
     */
    protected void onErrorItemLoadingNoDialog(@NonNull Throwable e) {
        getExceptionHandler().saveException(
                requireActivity(),
                e, TAG,
                "Error while loading item deck."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * New Activities
     * ----------------------------------------------------------------------------------------- */

    protected void newStudyCardActivity(int itemPosition) {
        Intent intent = new Intent(requireActivity(), ExceptionStudyCardActivity.class);
        intent.putExtra(
                ExceptionStudyCardActivity.DECK_DB_PATH,
                getFullDeckPath(itemPosition)
        );
        requireActivity().startActivity(intent);
    }

    public void newListCardsActivity(int itemPosition) {
        Intent intent = new Intent(requireActivity(), ExceptionListCardsActivity.class);
        intent.putExtra(
                ExceptionListCardsActivity.DECK_DB_PATH,
                getFullDeckPath(itemPosition)
        );
        requireActivity().startActivity(intent);
    }

    public void newNewCardActivity(int itemPosition) {
        Intent intent = new Intent(requireActivity(), DeleteCardSliderActivity.class);
        intent.putExtra(CardSliderActivity.DECK_DB_PATH, getFullDeckPath(itemPosition));
        intent.putExtra(AddCardSliderActivity.ADD_NEW_CARD, true);
        requireActivity().startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Actions
     * ----------------------------------------------------------------------------------------- */

    public void onItemClick(int itemPosition) {
        newStudyCardActivity(itemPosition);
    }

    /**
     * D_U_07 Rename the deck
     */
    public void showRenameDeckDialog(int itemPosition) {
        RenameDeckDialog dialog = new RenameDeckDialog(requireActivity(), paths.get(itemPosition));
        dialog.show(requireActivity().getSupportFragmentManager(), "RenameDeckDialog");
    }

    /**
     * D_R_08 Delete the deck
     */
    public void showDeleteDeckDialog(int itemPosition) {
        DeleteDeckDialog dialog = new DeleteDeckDialog(requireActivity(), paths.get(itemPosition));
        dialog.show(requireActivity().getSupportFragmentManager(), "DeleteDeckDialog");
    }

    /**
     * FS_PRO_S Synchronize the deck with a file.
     */
    public void launchSyncFile(int itemPosition) {
        Objects.requireNonNull(getFileSyncProLauncher()).launchSyncFile(getFullDeckPath(itemPosition));
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    public void launchExportToExcel(int itemPosition) {
        Objects.requireNonNull(getFileSyncLauncher()).launchExportToExcel(getFullDeckPath(itemPosition));
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    public void launchExportToCsv(int itemPosition) {
        Objects.requireNonNull(getFileSyncLauncher()).launchExportToCsv(getFullDeckPath(itemPosition));
    }

    /**
     * D_R_12 Export database
     */
    public void launchExportDb(int itemPosition) {
        getExportImportDbUtil().launchExportDb(getFullDeckPath(itemPosition));
    }

    public void startDeckSettingsActivity(int itemPosition) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra(ListCardsActivity.DECK_DB_PATH, getFullDeckPath(itemPosition));
        getActivity().startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Items actions
     * ----------------------------------------------------------------------------------------- */

    public void loadItems() {
        loadItems(getCurrentFolder());
    }

    public void loadItems(@NonNull Path folder) {
        if (countSeparators(getRootFolder()) > countSeparators(folder)) {
            throw new UnsupportedOperationException("Folders lower than the root folder for the storage databases cannot be opened.");
        } else {
            paths.clear();
            try {
                paths.addAll(getDeckDatabaseUtil().listDatabases(folder));
            } catch (IOException e) {
                this.onErrorItemLoading(e);
            }
            runOnUiThread(this::notifyDataSetChanged, this::onErrorItemLoading);
        }
    }

    @Deprecated
    protected long countSeparators(@NonNull File file) {
        return file.getPath().chars().filter(ch -> ch == File.separatorChar).count();
    }

    protected long countSeparators(@NonNull Path path) {
        return path.toString().chars().filter(ch -> ch == File.separatorChar).count();
    }

    @NonNull
    protected String getFullDeckPath(int itemPosition) {
        return paths.get(itemPosition).toString();
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected Path getRootFolder() {
        return getDeckDatabaseUtil().getDbFolder(getApplicationContext());
    }

    @Nullable
    protected FileSyncLauncher getFileSyncLauncher() {
        return requireActivity().getFileSyncLauncher();
    }

    @Nullable
    protected FileSyncProLauncher getFileSyncProLauncher() {
        return requireActivity().getFileSyncProLauncher();
    }

    @NonNull
    protected ExportImportDbUtil getExportImportDbUtil() {
        return requireActivity().getExportImportDbUtil();
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    @NonNull
    public Path getCurrentFolder() {
        return getRootFolder();
    }

    @NonNull
    public MainActivity requireActivity() {
        return (MainActivity) super.requireActivity();
    }

    @NonNull
    public MainActivity getActivity() {
        return (MainActivity) super.getActivity();
    }

    protected ListDecksFragment getFragment() {
        return listDecksFragment;
    }

    protected void addToDisposable(@NonNull Disposable disposable) {
        getFragment().getDisposable().add(disposable);
    }
}
