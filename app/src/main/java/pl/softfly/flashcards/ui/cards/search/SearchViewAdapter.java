package pl.softfly.flashcards.ui.cards.search;

import android.widget.TextView;

import com.google.android.material.color.MaterialColors;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.HtmlUtil;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.ui.cards.standard.CardBaseViewAdapter;

/**
 * https://developer.android.com/develop/ui/views/search
 *
 * @author Grzegorz Ziemski
 */
public class SearchViewAdapter extends CardBaseViewAdapter {

    private String searchQuery;

    private final HtmlUtil htmlUtil = HtmlUtil.getInstance();

    public SearchViewAdapter(SearchListCardsActivity activity, String deckDbPath)
            throws DatabaseException {
        super(activity, deckDbPath);
    }

    @Override
    public SearchListCardsActivity getActivity() {
        return (SearchListCardsActivity) super.getActivity();
    }

    @Override
    protected Observable<List<Card>> loadCardsToList() {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return super.loadCardsToList();
        } else {
            return searchCards(searchQuery);
        }
    }

    protected Observable<List<Card>> searchCards(String query) {
        return getDeckDb().cardDaoAsync().searchCards(query)
                .subscribeOn(Schedulers.io())
                .doOnError(this::onErrorSearchCards)
                .doOnSuccess(cards -> {
                    getActivity().runOnUiThread(() -> {
                        getCurrentList().clear();
                        getCurrentList().addAll(cards);
                        refreshDataSet(-1, -1, this::onErrorSearchCards);
                    });
                })
                .doOnComplete(() -> refreshDataSet(-1, -1, this::onErrorSearchCards))
                .toObservable();
    }

    protected void onErrorSearchCards(Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity().getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while searching cards."
        );
    }

    @Override
    protected void setText(TextView textView, String value) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            super.setText(textView, value);
        } else {
            String bg = Integer.toHexString(0xFFFFFF & MaterialColors.getColor(textView, R.attr.colorItemSearch));

            String val = formatText(value)
                    .replace("{search}", "<span style='background-color:#"+bg+";'>")
                    .replace("{esearch}", "</span>");

            textView.setText(htmlUtil.fromHtml(val));
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}
