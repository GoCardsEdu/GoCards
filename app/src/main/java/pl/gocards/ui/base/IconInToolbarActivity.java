package pl.gocards.ui.base;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.color.MaterialColors;

import java.util.Objects;

import pl.gocards.R;

/**
 * @author Grzegorz Ziemski
 */
public abstract class IconInToolbarActivity extends BaseActivity {

    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    protected MenuItem menuIconWithText(@NonNull MenuItem item, int icon) {
        return menuIconWithText(item, icon, Objects.requireNonNull(item.getTitle()).toString());
    }

    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    protected MenuItem menuIconWithText(@NonNull MenuItem item, int icon, String title) {
        Drawable drawable = Objects.requireNonNull(getDrawableHelper(icon));
        item.setTitle(menuIconWithText(drawable, title))
                .setTitleCondensed(menuIconWithText(drawable, ""));
        return item;
    }

    @NonNull
    @SuppressLint("ResourceAsColor")
    protected CharSequence menuIconWithText(@NonNull Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        r.setColorFilter(
                MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorIcon),
                PorterDuff.Mode.MULTIPLY
        );
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Nullable
    protected Drawable getDrawableHelper(int id) {
        return AppCompatResources.getDrawable(getBaseContext(), id);
    }
}
