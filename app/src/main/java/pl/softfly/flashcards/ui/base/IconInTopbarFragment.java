package pl.softfly.flashcards.ui.base;

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
import androidx.viewbinding.ViewBinding;

import com.google.android.material.color.MaterialColors;

import pl.softfly.flashcards.R;

/**
 * @author Grzegorz Ziemski
 */
public abstract class IconInTopbarFragment extends BaseFragment {

    protected MenuItem menuIconWithText(MenuItem item, int icon, String title) {
        Drawable drawable = getDrawableHelper(icon);
        item.setTitle(menuIconWithText(drawable, title))
                .setTitleCondensed(menuIconWithText(drawable, ""));
        return item;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    public CharSequence menuIconWithText(@NonNull Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        r.setColorFilter(
                MaterialColors.getColor(getView(), R.attr.colorIcon),
                PorterDuff.Mode.MULTIPLY
        );
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Nullable
    protected Drawable getDrawableHelper(int id) {
        return AppCompatResources.getDrawable(getActivity().getBaseContext(), id);
    }

    protected abstract ViewBinding getBinding();
}
