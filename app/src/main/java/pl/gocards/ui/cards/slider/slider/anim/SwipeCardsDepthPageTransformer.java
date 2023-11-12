package pl.gocards.ui.cards.slider.slider.anim;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.color.MaterialColors;

import java.util.Objects;

import pl.gocards.R;

/**
 * C_R_22 Swipe the cards left and right
 * <p>
 * Sets the card edge colors.
 * The animation is in {@link DepthPageTransformer}
 *
 * @author Grzegorz Ziemski
 */
public class SwipeCardsDepthPageTransformer extends DepthPageTransformer {

    private static final int MAX_ALPHA = 255;

    private static final int DP_2 = dpToPx(2);

    @NonNull
    private final Context context;

    private final int cardBorderColor;

    @NonNull
    private final GradientDrawable shapeDrawable;


    public SwipeCardsDepthPageTransformer(@NonNull Context context) {
        this.context = context;
        cardBorderColor = MaterialColors.getColor(context, R.attr.cardBorder, "");
        shapeDrawable = (GradientDrawable) Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ui_card_border_slide));
    }

    @Override
    protected void offToLeft(@NonNull View view, float position) {
        setDefaultBackground(view);
        super.offToLeft(view, position);
    }

    @Override
    protected void moveToLeft(@NonNull View view, float position) {
        int alpha = Math.min((int) (-position * MAX_ALPHA) + 50, MAX_ALPHA);
        shapeDrawable.setStroke(DP_2, ColorUtils.setAlphaComponent(cardBorderColor, alpha));
        view.setBackground(shapeDrawable);
        super.moveToLeft(view, position);
    }

    @Override
    protected void fullPage(@NonNull View view, float position) {
        setDefaultBackground(view);
        super.fullPage(view, position);
    }

    @Override
    protected void moveToRight(@NonNull View view, float position) {
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.ui_card_border_slide));
        super.moveToRight(view, position);
    }

    @Override
    protected void offToRight(@NonNull View view, float position) {
        setDefaultBackground(view);
        super.offToRight(view, position);
    }

    protected void setDefaultBackground(@NonNull View view) {
        view.setBackground(new ColorDrawable(MaterialColors.getColor(view, android.R.attr.windowBackground)));
    }

    @SuppressWarnings("SameParameterValue")
    protected static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
