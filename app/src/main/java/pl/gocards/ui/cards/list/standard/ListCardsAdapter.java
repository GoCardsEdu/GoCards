package pl.gocards.ui.cards.list.standard;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.room.util.HtmlUtil;
import pl.gocards.databinding.ItemCardBinding;
import pl.gocards.db.room.AppDatabase;
import pl.gocards.db.room.DeckDatabase;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.room.entity.deck.DeckConfig;
import pl.gocards.ui.base.recyclerview.BaseViewAdapter;
import pl.gocards.ui.cards.slider.add.AddCardSliderActivity;
import pl.gocards.ui.cards.slider.edit.EditCardSliderActivity;
import pl.gocards.ui.cards.slider.slider.CardSliderActivity;
import pl.gocards.ui.cards.slider.delete.DeleteCardSliderActivity;

/**
 * C_R_01 Display all cards
 * @author Grzegorz Ziemski
 */
public class ListCardsAdapter extends BaseViewAdapter<CardViewHolder> {

    private static final String TAG = "ListCardsAdapter";

    private final List<Card> cards = Collections.synchronizedList(new LinkedList<>());
    @NonNull
    private final String deckDbPath;
    @NonNull
    private final DeckDatabase deckDb;
    @SuppressWarnings("unused")
    @NonNull
    private final AppDatabase appDb;

    @SuppressWarnings("unused")
    private final CalcCardIdWidth calcCardIdWidth = CalcCardIdWidth.getInstance();
    private final HtmlUtil htmlUtil = HtmlUtil.getInstance();

    private int maxLines = DeckConfig.MAX_LINES_DEFAULT;

    public ListCardsAdapter(@NonNull ListCardsActivity activity) throws DatabaseException {
        super(activity);
        this.deckDbPath = activity.getDeckDbPath();
        this.deckDb = getDeckDb(deckDbPath);
        this.appDb = getAppDatabase();
    }

    protected @NonNull Completable beforeOnStart() {
        return loadMaxLinesConfig();
    }

    @NonNull
    private Completable loadMaxLinesConfig() {
        return deckDb.deckConfigRxDao().load(
                DeckConfig.MAX_LINES,
                Integer.toString(DeckConfig.MAX_LINES_DEFAULT),
                it -> runOnUiThread(() -> maxLines = Integer.parseInt(it))
        ).ignoreElements();
    }

    @SuppressLint("CheckResult")
    private void onStart() {
        Disposable disposable = beforeOnStart()
                .andThen(loadItemsCompletable())
                .andThen(afterOnStart())
                .subscribe(EMPTY_ACTION, this::onErrorLoadCards);
        addToDisposable(disposable);
    }

    protected @NonNull Completable afterOnStart() { return Completable.complete(); }

    public void onRestart() { onStart(); }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(onCreateView(parent), this);
    }

    @NonNull
    protected ItemCardBinding onCreateView(@NonNull ViewGroup parent) {
        return ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = getItem(position);
        holder.getIdTextView().setText(String.format(Integer.toString(card.getOrdinal())));
        if (calcCardIdWidth.getLastNumChecked() < card.getOrdinal()) {
            calcCardIdWidth.resetIdWidth(holder);
        } else {
            calcCardIdWidth.setIdWidth(holder);
        }

        setText(holder.getTermTextView(), card.getTerm());
        setText(holder.getDefinitionTextView(), card.getDefinition());

        holder.getTermTextView().setMaxLines(maxLines);
        holder.getDefinitionTextView().setMaxLines(maxLines);
    }

    protected void setText(@NonNull TextView textView, @NotNull String value) {
        textView.setText(formatText(value));
    }

    /**
     * Removes html tags
     */
    @NotNull
    protected String formatText(@NotNull String value) {
        if (htmlUtil.isSimpleHtml(value)) {
            return htmlUtil.fromHtml(value).toString();
        } else {
            return value;
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Cards
     * ----------------------------------------------------------------------------------------- */

    protected @NonNull Completable beforeLoadItems() {
        return Completable.complete();
    }

    @SuppressLint("CheckResult")
    public final void loadItems() {
        Disposable disposable = loadItemsCompletable()
                .subscribeOn(Schedulers.io())
                .subscribe(EMPTY_ACTION, this::onErrorLoadCards);
        addToDisposable(disposable);
    }

    @NonNull
    private Completable loadItemsCompletable() {
        return beforeLoadItems()
                .andThen(loadCardsToList())
                .ignoreElements()
                .andThen(Completable.fromAction(this::refreshDataSetOnUI))
                .andThen(afterLoadItems());
    }
    
    @SuppressLint("CheckResult")
    public void loadItems(int positionStart, int itemCount) {
        Disposable disposable = loadItemsCompletable(positionStart, itemCount)
                .subscribeOn(Schedulers.io())
                .subscribe(EMPTY_ACTION, this::onErrorLoadCards);
        addToDisposable(disposable);
    }

    private @NonNull Completable loadItemsCompletable(int positionStart, int itemCount) {
        return beforeLoadItems()
                .andThen(loadCardsToList())
                .ignoreElements()
                .andThen(Completable.fromAction(() -> refreshDataSetOnUI(positionStart, itemCount)))
                .andThen(afterLoadItems());
    }

    protected @NonNull Completable afterLoadItems() {
        return Completable.complete();
    }

    protected Observable<List<Card>> loadCardsToList() {
        return deckDb.cardRxDao().getAllCards()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(cards -> runOnUiThread(() -> {
                    getCurrentList().clear();
                    getCurrentList().addAll(cards);
                }, this::onErrorLoadCards))
                .toObservable();
    }

    protected void refreshDataSetOnUI() {
        runOnUiThread(this::notifyDataSetChanged, this::onErrorLoadCards);
    }

    protected void refreshDataSetOnUI(int positionStart, int itemCount) {
        runOnUiThread(() -> this.notifyItemRangeChanged(positionStart, itemCount), this::onErrorLoadCards);
    }

    protected void onErrorLoadCards(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(), TAG,
                "Error while loading cards.",
                (dialog, which) -> requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Menu actions
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_D_25 Delete the card
     */
    @SuppressLint("CheckResult")
    public void onClickDeleteCard(int position) {
        Card card = removeItem(position);
        Disposable disposable = deckDb.cardRxDao().delete(card)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> runOnUiThread(
                        () -> doOnSuccessDeleteCard(position, card),
                        this::onErrorClickDeleteCard)
                )
                .subscribe(EMPTY_ACTION, this::onErrorClickDeleteCard);

        addToDisposable(disposable);
    }

    @UiThread
    protected void doOnSuccessDeleteCard(int position, Card card) {
        notifyItemRemoved(position);
        //Refresh ordinal numbers.
        loadItems(position, getItemCount() - position);
    }

    protected void showCardHasBeenDeleted(Card card, boolean wasSelected) {
        Snackbar.make(requireActivity().findViewById(R.id.list_cards),
                        R.string.cards_list_toast_deleted_card,
                        Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> revertCard(card, wasSelected))
                .show();
    }

    protected void onErrorClickDeleteCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(), TAG,
                "Error while removing the card."
        );
    }

    /**
     * C_U_26 Undo card deletion
     */
    @SuppressLint("CheckResult")
    protected void revertCard(@NonNull Card card, boolean wasSelected) {
        Disposable disposable = deckDb.cardRxDao().restore(card)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    Disposable disposable2 = loadItemsCompletable()
                            .doOnComplete(() -> runOnUiThread(
                                    () -> onCompleteRevertCard(card, wasSelected),
                                    this::onErrorRevertCard)
                            )
                            .subscribe(EMPTY_ACTION, this::onErrorRevertCard);
                    addToDisposable(disposable2);
                })
                .subscribe(EMPTY_ACTION, this::onErrorRevertCard);
        addToDisposable(disposable);
    }

    @SuppressLint("CheckResult")
    protected void onCompleteRevertCard(@NonNull Card card, boolean wasSelected) {
        notifyItemInserted(card.getOrdinal() - 1);

        int positionStart = card.getOrdinal() - 1;
        int itemCount = getItemCount() - card.getOrdinal() + 2;
        this.notifyItemRangeChanged(positionStart, itemCount);

        showShortToastMessage(R.string.cards_list_toast_restore_card);
    }

    protected void onErrorRevertCard(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity(), TAG,
                "Error while restoring the card."
        );
    }

    /**
     * C_C_24 Edit the card
     */
    protected void startEditCardActivity(int position) {
        Intent intent = new Intent(requireActivity(), DeleteCardSliderActivity.class);
        intent.putExtra(CardSliderActivity.DECK_DB_PATH, deckDbPath);
        intent.putExtra(EditCardSliderActivity.EDIT_CARD_ID, getItem(position).getId());
        requireActivity().startActivity(intent);
    }

    /**
     * C_R_07 Add a new card here
     */
    protected void startNewCardActivity(int position) {
        Intent intent = new Intent(requireActivity(), DeleteCardSliderActivity.class);
        intent.putExtra(CardSliderActivity.DECK_DB_PATH, deckDbPath);
        intent.putExtra(AddCardSliderActivity.NEW_CARD_AFTER_CARD_ID, getItem(position).getId());
        requireActivity().startActivity(intent);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets Items
     * ----------------------------------------------------------------------------------------- */

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @NonNull
    public List<Card> getCurrentList() {
        return cards;
    }

    public Card getItem(int position) {
        return cards.get(position);
    }

    protected Card removeItem(int position) {
        return cards.remove(position);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected DeckDatabase getDeckDb() {
        return deckDb;
    }

    @NonNull
    protected AppDatabase getAppDb() {
        return appDb;
    }

    @NonNull
    public ListCardsActivity requireActivity() {
        return (ListCardsActivity) super.requireActivity();
    }

    protected void addToDisposable(@NonNull Disposable disposable) {
        requireActivity().addToDisposable(disposable);
    }
}