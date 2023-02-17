package pl.softfly.flashcards.ui.cards.standard;

import static pl.softfly.flashcards.ui.card.NewCardAfterOrdinalActivity.AFTER_ORDINAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.ExceptionHandler;
import pl.softfly.flashcards.HtmlUtil;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.databinding.ItemCardBinding;
import pl.softfly.flashcards.db.room.AppDatabase;
import pl.softfly.flashcards.db.room.DeckDatabase;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.ui.base.recyclerview.BaseViewAdapter;
import pl.softfly.flashcards.ui.card.EditCardActivity;
import pl.softfly.flashcards.ui.card.NewCardActivity;
import pl.softfly.flashcards.ui.card.NewCardAfterOrdinalActivity;

public class CardBaseViewAdapter extends BaseViewAdapter<CardViewHolder> {

    private final List<Card> cards = new LinkedList<>();
    private final String deckDbPath;
    private final DeckDatabase deckDb;
    private final AppDatabase appDb;

    private final CalcCardIdWidth calcCardIdWidth = CalcCardIdWidth.getInstance();
    private final HtmlUtil htmlUtil = HtmlUtil.getInstance();

    public int idTextViewWidth = 0;

    public CardBaseViewAdapter(ListCardsActivity activity, String deckDbPath)
            throws DatabaseException {
        super(activity);
        this.deckDbPath = deckDbPath;
        this.deckDb = getDeckDatabase(deckDbPath);
        this.appDb = getAppDatabase();
        init();

    }

    public void init() {
        loadItems();
    }

    public void onRestart() {
        init();
    }

    /* -----------------------------------------------------------------------------------------
     * Adapter methods overridden
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardViewHolder(onCreateView(parent), this);
    }

    protected ItemCardBinding onCreateView(@NonNull ViewGroup parent) {
        return ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = getItem(position);
        holder.getIdTextView().setText(card.getOrdinal().toString());
        if (calcCardIdWidth.getLastNumChecked() < card.getOrdinal()) {
            //resetIdWidth(holder);
        } else {
            calcCardIdWidth.setIdWidth(holder);
        }
        setText(holder.getTermTextView(), card.getTerm());
        setText(holder.getDefinitionTextView(), card.getDefinition());
    }

    protected void setText(TextView textView, String value) {
        textView.setText(formatText(value));
    }

    /**
     * Needed to recalculate the width
     * @see CalcCardIdWidth
     */
    protected void resetIdWidth(@NonNull CardViewHolder holder) {
        holder.getIdTextView().setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    /**
     * Removes html tags
     */
    protected String formatText(String value) {
        if (htmlUtil.isHtml(value)) {
            return htmlUtil.fromHtml(value).toString();
        } else {
            return value;
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Cards
     * ----------------------------------------------------------------------------------------- */

    public void loadItems() {
        loadItems(-1, -1);
    }

    public void loadItems(int positionStart, int itemCount) {
        loadItems(Observable.empty())
                .doOnComplete(() -> refreshDataSet(positionStart, itemCount, this::onErrorLoadCards))
                .subscribe();
    }

    protected Observable loadItems(ObservableSource next) {
        return Completable
                .fromObservable(loadCardsToList())
                .andThen(next);
    }

    protected Observable<List<Card>> loadCardsToList() {
        return deckDb.cardDaoAsync().getAllCards()
                .subscribeOn(Schedulers.io())
                .doOnError(this::onErrorLoadCards)
                .doOnSuccess(cards -> {
                    getCurrentList().clear();
                    getCurrentList().addAll(cards);
                }).toObservable();
    }

    protected void refreshDataSet(int positionStart, int itemCount, Consumer<? super Throwable> onError) {
        getActivity().runOnUiThread(() -> {
            if (positionStart < 0)
                this.notifyDataSetChanged();
            else
                this.notifyItemRangeChanged(positionStart, itemCount);
        }, onError);
    }

    protected void onErrorLoadCards(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while loading cards."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Menu actions
     * ----------------------------------------------------------------------------------------- */

    public void onClickDeleteCard(int position) {
        Card card = removeItem(position);
        deckDb.cardDao().deleteAsync(card)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> getActivity().runOnUiThread(() -> {
                    notifyItemRemoved(position);
                    //Refresh ordinal numbers.
                    loadItems(position, getItemCount() - position);
                    Snackbar.make(getActivity().findViewById(R.id.listCards),
                                    "The card has been deleted.",
                                    Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> revertCard(card))
                            .show();
                }, this::onErrorClickDeleteCard))
                .subscribe(() -> {}, this::onErrorClickDeleteCard);
    }

    protected void onErrorClickDeleteCard(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName() + "_OnClickDeleteCard",
                "Error while removing the card."
        );
    }

    @SuppressLint("CheckResult")
    protected void revertCard(@NonNull Card card) {
        deckDb.cardDao().restoreAsync(card)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() ->
                        loadItems(Observable.empty())
                                .doOnComplete(() -> getActivity().runOnUiThread(() -> {
                                    notifyItemInserted(card.getOrdinal() - 1);

                                    int positionStart = card.getOrdinal() - 1;
                                    int itemCount = getItemCount() - card.getOrdinal() + 2;
                                    refreshDataSet(positionStart, itemCount, this::onErrorLoadCards);

                                    Toast.makeText(getActivity(), "The card has been restored.", Toast.LENGTH_SHORT).show();
                                }, this::onErrorRevertCard))
                                .ignoreElements()
                                .subscribe(() -> {}, this::onErrorRevertCard)
                )
                .subscribe(() -> {}, this::onErrorRevertCard);
    }

    protected void onErrorRevertCard(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while restoring the card."
        );
    }

    protected void startEditCardActivity(int position) {
        Intent intent = new Intent(getActivity(), EditCardActivity.class);
        intent.putExtra(EditCardActivity.DECK_DB_PATH, deckDbPath);
        intent.putExtra(EditCardActivity.CARD_ID, getItem(position).getId());
        getActivity().startActivity(intent);
    }

    protected void startNewCardActivity(int position) {
        Intent intent = new Intent(getActivity(), NewCardAfterOrdinalActivity.class);
        intent.putExtra(NewCardActivity.DECK_DB_PATH, deckDbPath);
        intent.putExtra(AFTER_ORDINAL, getItem(position).getOrdinal());
        getActivity().startActivity(intent);
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

    protected DeckDatabase getDeckDb() {
        return deckDb;
    }

    protected AppDatabase getAppDb() {
        return appDb;
    }

    public ListCardsActivity getActivity() {
        return (ListCardsActivity) super.getActivity();
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
