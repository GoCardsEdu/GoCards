package pl.gocards.ui.cards.xml.list.standard;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This sets the width of the ID column and the width of the header columns.
 * <p>
 * What problem does this solve?
 * I was unable to use the SDK to set the relative width of the ID column
 * to match with the length of the text.
 *
 * @author Grzegorz Ziemski
 */
public class CalcCardIdWidth {

    private static CalcCardIdWidth INSTANCE;

    private int maxIdWidth = 0;

    private int lastNumChecked = 1;

    @NonNull
    public static synchronized CalcCardIdWidth getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CalcCardIdWidth();
        }
        return INSTANCE;
    }

    @NonNull
    public ViewTreeObserver.OnDrawListener calcIdWidth(
            @NonNull RecyclerView recyclerView,
            @NonNull TextView idHeader
    ) {
        return () -> {
            int width = findMaxIdsWidth(recyclerView);
            if (width != 0) {
                maxIdWidth = Math.max(width, maxIdWidth);
                setIdsWidth(recyclerView, maxIdWidth);
                if (idHeader.getWidth() != maxIdWidth) {
                    idHeader.setWidth(maxIdWidth);
                }
            }
        };
    }

    private int findMaxIdsWidth(@NonNull RecyclerView recyclerView) {
        int maxIdWidth = 0;

        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            CardViewHolder holder = (CardViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            if (isWidthWrapContent(holder.getIdTextView())) {
                int width = holder.getIdTextView().getWidth();
                if (width > maxIdWidth) {
                    maxIdWidth = holder.getIdTextView().getWidth();
                }
            }
        }
        return maxIdWidth;
    }

    private boolean isWidthWrapContent(@NonNull View view) {
        return view.getLayoutParams().width == TableRow.LayoutParams.WRAP_CONTENT;
    }

    private void setIdsWidth(@NonNull RecyclerView recyclerView, int width) {
        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
            CardViewHolder holder = (CardViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            setIdWidth(holder, width);
        }
    }

    public void setIdWidth(@NonNull CardViewHolder holder) {
        setIdWidth(holder, maxIdWidth);
    }

    private void setIdWidth(@NonNull CardViewHolder holder, int width) {
        int currentWidth = holder.getIdTextView().getWidth();
        if (currentWidth != width) {
            holder.getIdTextView().setLayoutParams(new TableRow.LayoutParams(
                    width,
                    TableRow.LayoutParams.MATCH_PARENT
            ));
        }
    }

    /**
     * Needed to recalculate the width
     * @see CalcCardIdWidth
     */
    protected void resetIdWidth(@NonNull CardViewHolder holder) {
        lastNumChecked = Integer.parseInt(holder.getIdTextView().getText().toString());
        holder.getIdTextView().setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    public int getLastNumChecked() {
        return lastNumChecked;
    }
}