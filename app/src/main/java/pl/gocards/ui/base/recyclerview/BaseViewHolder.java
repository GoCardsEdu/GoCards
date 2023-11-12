package pl.gocards.ui.base.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final BaseViewAdapter<? extends RecyclerView.ViewHolder> adapter;

    public BaseViewHolder(@NonNull View itemView, @NonNull BaseViewAdapter<? extends RecyclerView.ViewHolder> adapter) {
        super(itemView);
        this.adapter = adapter;
    }

    /** @noinspection SameParameterValue*/
    @NonNull
    protected String getString(@StringRes int resId) {
        return requireActivity().getResources().getString(resId);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }

    @NonNull
    protected BaseViewAdapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }

    @NonNull
    protected AppCompatActivity requireActivity() {
        return getAdapter().requireActivity();
    }

    @Nullable
    protected AppCompatActivity getActivity() {
        return getAdapter().getActivity();
    }
}
