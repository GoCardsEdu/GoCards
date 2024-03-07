package pl.gocards.ui.cards.xml.list.exception;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import pl.gocards.App;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.cards.xml.list.file_sync.FileSyncListCardsActivity;

/**
 * @author Grzegorz Ziemski
 */
/*
 * I considered making classes for catching exceptions more general and making Composition,
 * but in the Manifest defined classes must inherit android.app.Activity.
 */
public class ExceptionListCardsActivity extends FileSyncListCardsActivity {

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getExceptionHandler().tryRun(
                () -> super.onCreate(savedInstanceState),
                this::onErrorOnCreate
        );
        // getExceptionHandler().setUncaughtExceptionHandler(this);
    }

    protected void onErrorOnCreate(@NonNull Throwable e) {
        App app = (App) getApplicationContext();
        app.setExceptionToDisplay(e);
        getOnBackPressedDispatcher().onBackPressed();
    }

    @NonNull
    @Override
    protected ExceptionListCardsAdapter onCreateRecyclerViewAdapter()
            throws DatabaseException {
        return new ExceptionListCardsAdapter(this);
    }

    @NonNull
    @Override
    protected ExceptionCardTouchHelper onCreateTouchHelper() {
        return new ExceptionCardTouchHelper(getAdapter());
    }


    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        getExceptionHandler().tryRun(super::onResume, this, "Error while resuming activity.");
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        try {
            return super.onCreateOptionsMenu(menu);
        } catch (Exception e) {
            getExceptionHandler().handleException(e, this, "Error while creating options menu.", (dialog, which) -> getOnBackPressedDispatcher().onBackPressed());
            return false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        try {
            return super.onPrepareOptionsMenu(menu);
        } catch (Exception e) {
            getExceptionHandler().handleException(e, this, "Error while preparing menu.", (dialog, which) -> getOnBackPressedDispatcher().onBackPressed());
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            getExceptionHandler().handleException(e, this, "Error while selecting item in the toolbar menu.");
            return false;
        }
    }

    @NonNull
    @Override
    public ExceptionListCardsAdapter getAdapter() {
        return (ExceptionListCardsAdapter) super.getAdapter();
    }
}