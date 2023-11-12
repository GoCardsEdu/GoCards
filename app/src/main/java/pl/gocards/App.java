package pl.gocards;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * @author Grzegorz Ziemski
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks {

    /** @noinspection DataFlowIssue*/
    @NonNull // Null only when the app is not active.
    private Activity activeActivity = null;

    @Nullable
    private Throwable exceptionToDisplay = null;

    @NonNull
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        registerActivityLifecycleCallbacks(this);
    }

    /* -----------------------------------------------------------------------------------------
     * ActivityLifecycleCallbacks
     * ----------------------------------------------------------------------------------------- */

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        App.this.activeActivity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        App.this.activeActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        App.this.activeActivity = activity;
    }

    /** @noinspection DataFlowIssue*/
    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        App.this.activeActivity = null;
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // don't clear current activity because activity may get stopped after
        // the new activity is resumed
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // don't clear current activity because activity may get destroyed after
        // the new activity is resumed
    }

    @NonNull
    public AppCompatActivity getActiveActivity() {
        return (AppCompatActivity) activeActivity;
    }

    @Nullable
    public Throwable getExceptionToDisplay() {
        return exceptionToDisplay;
    }

    public void setExceptionToDisplay(@Nullable Throwable exceptionToDisplay) {
        this.exceptionToDisplay = exceptionToDisplay;
    }

    @NonNull
    public CompositeDisposable getDisposable() {
        return disposable;
    }
}
