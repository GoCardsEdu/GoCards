package pl.gocards.ui.cards.xml.slider.slider;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import androidx.annotation.NonNull;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import pl.gocards.ui.base.BaseFragmentStateAdapter;

/**
 * C_R_22 Swipe the cards left and right
 * @author Grzegorz Ziemski
 */
public abstract class CardSliderAdapter extends BaseFragmentStateAdapter {

    @NonNull
    private final CardsSliderViewModel activityModel;

    @NonNull
    private final String deckDbPath;

    @NonNull
    private final CompositeDisposable activityDisposable;

    public CardSliderAdapter(@NonNull CardSliderActivity activity) {
        super(activity);
        this.deckDbPath = activity.getDeckDbPath();
        this.activityModel = activity.getActivityModel();
        this.activityDisposable = activity.getDisposable();
        loadCards();
    }

    public void loadCards() {
        Disposable disposable = getActivityModel()
                .loadCards(this::onErrorLoadCards)
                .doOnEvent((cards, throwable) -> requireActivity().onSuccessLoadCards())
                .doOnSuccess(cardOrdinals -> requireActivity().onSuccessLoadCards())
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorLoadCards);
        activityDisposable.add(disposable);
    }

    private void onErrorLoadCards(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireActivity(),
                "Error while loading cards."
        );
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets Items
     * ----------------------------------------------------------------------------------------- */

    @Override
    public int getItemCount() {
        return activityModel.getCards().size();
    }

    @Override
    public long getItemId(int position) {
        return getCardId(position);
    }

    @Override
    public boolean containsItem(long itemId) {
        return getActivityModel().getCardIds().contains((int) (itemId / 10));
    }

    protected int getCardId(int position) {
        return Objects.requireNonNull(activityModel.getCard(position).getId());
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    protected String getDeckDbPath() {
        return deckDbPath;
    }

    protected CardSliderActivity requireActivity() {
        return (CardSliderActivity) super.requireActivity();
    }

    @NonNull
    protected CardsSliderViewModel getActivityModel() {
        return activityModel;
    }
}
