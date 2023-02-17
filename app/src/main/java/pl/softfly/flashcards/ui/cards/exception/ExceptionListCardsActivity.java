package pl.softfly.flashcards.ui.cards.exception;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import pl.softfly.flashcards.db.storage.DatabaseException;
import pl.softfly.flashcards.ui.cards.file_sync.FileSyncListCardsActivity;

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
                getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                (dialog, which) -> onBackPressed() //TODO show error
        );
    }

    @Override
    protected ExceptionCardBaseViewAdapter onCreateRecyclerViewAdapter()
            throws DatabaseException {
        return new ExceptionCardBaseViewAdapter(this, getDeckDbPath());
    }

    @NonNull
    @Override
    protected ExceptionCardTouchHelper onCreateTouchHelper() {
        return new ExceptionCardTouchHelper(getAdapter());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        AtomicBoolean display = new AtomicBoolean(false);
        getExceptionHandler().tryRun(
                () -> display.set(super.onPrepareOptionsMenu(menu)),
                getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                (dialog, which) -> onBackPressed() //TODO show error
        );
        return display.get();
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        getExceptionHandler().tryRun(
                () -> super.onResume(),
                getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while resuming activity."
        );
    }

    @Override
    public void onRestart() {
        getExceptionHandler().tryRun(
                () -> super.onRestart(),
                getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while restarting activity."
        );
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        try {
            return super.onCreateOptionsMenu(menu);
        } catch (Exception e) {
            getExceptionHandler().handleException(
                    e, getSupportFragmentManager(),
                    this.getClass().getSimpleName(),
                    "Error while creating toolbar menu."
            );
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            getExceptionHandler().handleException(
                    e, getSupportFragmentManager(),
                    this.getClass().getSimpleName(),
                    "Error while selecting item in the toolbar menu."
            );
            return false;
        }
    }

    @Override
    public ExceptionCardBaseViewAdapter getAdapter() {
        return (ExceptionCardBaseViewAdapter) super.getAdapter();
    }
}