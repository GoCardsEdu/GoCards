package pl.gocards.ui.cards.xml.study.exception;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import pl.gocards.ui.cards.xml.study.file_sync.FileSyncStudyCardActivity;

/**
 * @author Grzegorz Ziemski
 */
/*
 * I considered making classes for catching exceptions more general and making Composition,
 * but in the Manifest defined classes must inherit android.app.Activity.
 */
public class ExceptionStudyCardActivity extends FileSyncStudyCardActivity {

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        getExceptionHandler().tryRun(
                super::onResume, this, "Error while resuming activity."
        );
    }

    @Override
    public void onRestart() {
        getExceptionHandler().tryRun(
                super::onRestart, this, "Error while restarting activity."
        );
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        try {
            return super.onCreateOptionsMenu(menu);
        } catch (Exception e) {
            getExceptionHandler().handleException(
                    e, this, "Error while creating toolbar menu."
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
                    e, this, "Error while selecting item in the toolbar menu."
            );
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        try {
            return super.onSupportNavigateUp();
        } catch (Exception e) {
            getExceptionHandler().handleException(
                    e, this, "Error when pressing back."
            );
            return false;
        }
    }
}
