package pl.gocards.ui.cards.list.search;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.color.MaterialColors;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.room.util.HtmlUtil;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.room.entity.deck.Card;
import pl.gocards.ui.cards.list.select.SelectListCardsAdapter;

/**
 * C_R_02 Search cards
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
@SuppressWarnings("JavadocLinkAsPlainText")
public class SearchListCardsAdapter extends SelectListCardsAdapter {

    private String searchQuery;

    private final HtmlUtil htmlUtil = HtmlUtil.getInstance();

    public SearchListCardsAdapter(@NonNull SearchListCardsActivity activity) throws DatabaseException {
        super(activity);
    }

    @SuppressLint("CheckResult")
    public void onClickPasteCards(int pasteAfterOrdinal) {
        if (this.requireActivity().isSearchMode()) {
            Disposable disposable = Completable.fromAction(() -> pasteCards(pasteAfterOrdinal))
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(this::loadItems)
                    .subscribe(EMPTY_ACTION, this::onErrorOnClickPasteCards);
            addToDisposable(disposable);
        } else {
            super.onClickPasteCards(pasteAfterOrdinal);
        }
    }

    @Override
    protected Observable<List<Card>> loadCardsToList() {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return super.loadCardsToList();
        } else {
            return searchCards(searchQuery);
        }
    }

    private Observable<List<Card>> searchCards(@NonNull String query) {
        return getDeckDb().cardRxDao().searchCards(query)
                .subscribeOn(Schedulers.io())
                .doOnError(this::onErrorSearchCards)
                .doOnSuccess(cards -> runOnUiThread(() -> {
                    getCurrentList().clear();
                    getCurrentList().addAll(cards);
                    refreshDataSetOnUI();
                }, this::onErrorSearchCards))
                .doOnComplete(this::refreshDataSetOnUI)
                .toObservable();
    }

    private void onErrorSearchCards(@NonNull Throwable e) {
        getExceptionHandler().handleException(e, requireActivity(), "Error while searching cards.");
    }

    @Override
    protected void setText(@NonNull TextView textView, @NotNull String value) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            super.setText(textView, value);
        } else {
            String bg = Integer.toHexString(0xFFFFFF & MaterialColors.getColor(textView, R.attr.colorItemSearch));
            String searchedMarked = value
                    .replace("{search}", "<span style='background-color:#" + bg + ";'>")
                    .replace("{esearch}", "</span>");
            textView.setText(htmlUtil.fromHtml(searchedMarked));
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @NonNull
    @Override
    public SearchListCardsActivity requireActivity() {
        return (SearchListCardsActivity) super.requireActivity();
    }
}
