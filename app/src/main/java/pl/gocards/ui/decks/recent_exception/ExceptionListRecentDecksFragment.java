package pl.gocards.ui.decks.recent_exception;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import pl.gocards.ui.decks.recent.ListRecentDecksFragment;

/**
 * @author Grzegorz Ziemski
 */
public class ExceptionListRecentDecksFragment extends ListRecentDecksFragment {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @NonNull
    @Override
    protected ExceptionRecentDeckViewAdapter onCreateAdapter() {
        return new ExceptionRecentDeckViewAdapter(requireMainActivity(), this);
    }

    /* -----------------------------------------------------------------------------------------
     * Fragment methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getExceptionHandler().tryRun(
                () -> super.onCreateOptionsMenu(menu, inflater),
                this::onErrorCreateMenu
        );
    }

    protected void onErrorCreateMenu(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireBackupActivity(),
                "Error while creating toolbar menu.",
                (dialog, which) -> System.exit(4)
        );
    }

    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            this.onErrorMenuItemSelected(e);
        }
        return false;
    }

    protected void onErrorMenuItemSelected(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, getActivity(),
                "Error while selecting item in the toolbar menu.",
                (dialog, which) -> System.exit(4)
        );
    }
}