package pl.softfly.flashcards;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;

/**
 * @author Grzegorz Ziemski
 */
public class FlashCardsApp extends Application {

    @Nullable
    private Activity activeActivity = null;

    private Throwable exceptionToDisplay = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //DynamicColors.applyToActivitiesIfAvailable(this);
        registerActivityLifecycleCallbacks(setupActivityListener());
    }

    @NonNull
    protected ActivityLifecycleCallbacks setupActivityListener() {
        return new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                FlashCardsApp.this.activeActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                FlashCardsApp.this.activeActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                FlashCardsApp.this.activeActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                FlashCardsApp.this.activeActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // don't clear current activity because activity may get stopped after
                // the new activity is resumed
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // don't clear current activity because activity may get destroyed after
                // the new activity is resumed
            }
        };
    }

    @Nullable
    public AppCompatActivity getActiveActivity() {
        return (AppCompatActivity) activeActivity;
    }

    public Throwable getExceptionToDisplay() {
        return exceptionToDisplay;
    }

    public void setExceptionToDisplay(Throwable exceptionToDisplay) {
        this.exceptionToDisplay = exceptionToDisplay;
    }
}
