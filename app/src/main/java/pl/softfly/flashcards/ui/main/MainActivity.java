package pl.softfly.flashcards.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.MaterialColors;

import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.softfly.flashcards.FlashCardsApp;
import pl.softfly.flashcards.R;
import pl.softfly.flashcards.databinding.ActivityMainBinding;
import pl.softfly.flashcards.entity.app.AppConfig;
import pl.softfly.flashcards.ui.FileSyncUtil;
import pl.softfly.flashcards.ui.base.BaseActivity;
import pl.softfly.flashcards.ui.decks.all_decks_exception.ExceptionListDecksFragment;
import pl.softfly.flashcards.ui.decks.recent.ListRecentDecksFragment;
import pl.softfly.flashcards.ui.decks.recent_exception.ExceptionListRecentDecksFragment;

/**
 * @author Grzegorz Ziemski
 */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private Fragment currentFragment;

    private ExceptionListDecksFragment allDecksFragment;

    private ListRecentDecksFragment recentDecksFragment;

    private Integer position;

    private FileSyncUtil fileSyncUtil = new FileSyncUtil(this);

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initDarkMode();
        super.onCreate(savedInstanceState);

        // Remove shadow under bar
        getSupportActionBar().setElevation(0);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = getNavView();
        navView.setOnItemSelectedListener(this::onNavigationItemSelected);

        loadFragment();
    }

    protected void loadFragment() {
        if (position != null) {
            if (position == 1) {
                loadRecentDecksFragment();
            } else {
                loadAllDecksFragment();
            }
        } else {
            loadDefaultFragment();
        }
    }

    protected void loadDefaultFragment() {
        getAppDatabase().deckDaoAsync().findByLastUpdatedAt(1)
                .doOnSuccess(deck -> {
                    if (deck.size() > 0) {
                        loadRecentDecksFragment();
                    } else {
                        loadAllDecksFragment();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(decks -> {}, this::onErrorLoadFragment);
    }

    protected void loadRecentDecksFragment() {
        currentFragment = getRecentDecksFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, currentFragment);
        transaction.commit();
        runOnUiThread(() -> getNavView().setSelectedItemId(R.id.recent_decks));
        position = 1;
    }

    protected void loadAllDecksFragment() {
        currentFragment = getAllDecksFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, currentFragment);
        transaction.commit();
        runOnUiThread(() -> getNavView().setSelectedItemId(R.id.list_decks));
        position = 2;
    }

    protected void onErrorLoadFragment(Throwable e) {
        getExceptionHandler().handleException(
                e, getSupportFragmentManager(),
                this.getClass().getSimpleName(),
                "Error while loading first screen."
        );
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        int newPosition = 0;
        switch (menuItem.getItemId()) {
            case R.id.recent_decks:
                fragment = getRecentDecksFragment();
                newPosition = 1;
                break;
            case R.id.list_decks:
                fragment = getAllDecksFragment();
                newPosition = 2;
                break;
        }
        loadFragment(fragment, newPosition);
        return true;
    }

    /**
     * Adds a slide animation when switching between tabs.
     */
    protected void loadFragment(Fragment fragment, int newPosition) {
        if(position > newPosition) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        if(position < newPosition) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
        position = newPosition;
        currentFragment = fragment;
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        initDarkMode();
        super.onResume();
        showExceptionCrashedActivities();
    }

    protected void showExceptionCrashedActivities() {
        FlashCardsApp app = (FlashCardsApp) getApplicationContext();
        if (app.getExceptionToDisplay() != null) {
            getExceptionHandler().handleException(
                    app.getExceptionToDisplay(),
                    getSupportFragmentManager(),
                    this.getClass().getSimpleName()
            );
            app.setExceptionToDisplay(null);
        }
    }

    /**
     * It is not needed elsewhere, because
     * 1) this is the first activity
     * 2) after changing the app settings, it comes back to this activity
     * @return
     */
    protected void initDarkMode() {
        String darkMode = getAppDatabase().appConfigDao().getStringByKey(AppConfig.DARK_MODE);
        if (darkMode == null) {
            setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            return;
        }

        switch (darkMode) {
            case AppConfig.DARK_MODE_ON:
                setDarkMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case AppConfig.DARK_MODE_OFF:
                setDarkMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
    protected void setDarkMode(@AppCompatDelegate.NightMode int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        setBarSameColours();
    }


    @Override
    public boolean onSupportNavigateUp() {
        if (isAllDecksFragment()) {
            getAllDecksFragment().onSupportNavigateUp();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("position", position);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
    }

    /* -----------------------------------------------------------------------------------------
     * Actions (public)
     * ----------------------------------------------------------------------------------------- */

    public void showBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void hideBackArrow() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void refreshItems() {
        if (isRecentDecksFragment()) {
            getRecentDecksFragment().getAdapter().refreshItems();
        }
        if (isAllDecksFragment()) {
            getAllDecksFragment().getAdapter().refreshItems();
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public ExceptionListDecksFragment getAllDecksFragment() {
        if (allDecksFragment == null) {
            allDecksFragment = new ExceptionListDecksFragment();
        }
        return allDecksFragment;
    }

    public ListRecentDecksFragment getRecentDecksFragment() {
        if (recentDecksFragment == null) {
            recentDecksFragment = new ExceptionListRecentDecksFragment();
        }
        return recentDecksFragment;
    }

    protected boolean isAllDecksFragment() {
        return currentFragment instanceof ExceptionListDecksFragment;
    }

    protected boolean isRecentDecksFragment() {
        return currentFragment instanceof ExceptionListRecentDecksFragment;
    }

    public FileSyncUtil getFileSyncUtil() {
        return fileSyncUtil;
    }

    protected BottomNavigationView getNavView() {
        return binding.navView;
    }
}