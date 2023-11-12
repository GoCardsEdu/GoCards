package pl.gocards.ui.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public abstract class BaseFragmentStateAdapter extends FragmentStateAdapter {

    @NonNull
    protected final FragmentActivity activity;

    public BaseFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        activity = fragmentActivity;
    }

    protected FragmentActivity requireActivity() {
        return activity;
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
