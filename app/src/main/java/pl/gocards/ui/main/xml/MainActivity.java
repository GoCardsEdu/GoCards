package pl.gocards.ui.main.xml;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;
import static pl.gocards.filesync.FileSyncLauncher.FILESYNC_DECK_DB_PATH;
import static pl.gocards.filesync.FileSyncProLauncher.FILESYNC_PRO_DECK_DB_PATH;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.App;
import pl.gocards.R;
import pl.gocards.databinding.ActivityMainBinding;
import pl.gocards.room.entity.app.AppConfig;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.filesync.FileSyncLauncher;
import pl.gocards.filesync.FileSyncProLauncher;
import pl.gocards.ui.decks.xml.ExportImportDbRxUtil;
import pl.gocards.ui.base.BaseActivity;
import pl.gocards.ui.decks.xml.recent.ListRecentDecksFragment;
import pl.gocards.ui.decks.xml.search.SearchDecksFragment;

/**
 * @author Grzegorz Ziemski
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final String CURRENT_FRAGMENT_ID = "CURRENT_FRAGMENT_ID";

    private static final int FRAGMENT_RECENT_DECKS = 0;

    private static final int FRAGMENT_ALL_DECKS = 1;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ActivityMainBinding binding;

    private MainAdapter adapter;

    private int currentFragmentId = -1;

    @Nullable
    private final FileSyncLauncher fileSyncLauncher = FileSyncLauncher.getInstance(this, getDisposable());

    @Nullable
    private final FileSyncProLauncher fileSyncProLauncher = FileSyncProLauncher.getInstance(this, null, getDisposable());

    @NonNull
    private final ExportImportDbRxUtil exportImportDbUtil = new ExportImportDbRxUtil(this, () -> {
        refreshItems();
        return null;
    });

    /* -----------------------------------------------------------------------------------------
     * Constructor
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initDarkMode();
        super.onCreate(savedInstanceState);
        createDbRootFolder();
        removeShadowUnderBar();

        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT_ID);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = getNavView();
        navView.setOnItemSelectedListener(this::onNavigationItemSelected);

        slideToFragment();

        adapter = new MainAdapter(this);
        getViewPager().setAdapter(adapter);
        getNavView().addOnLayoutChangeListener(this::fixViewPagerHeight);
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isAllDecksFragment() && !getAllDecksFragment().isRootFolder()) {
                    getAllDecksFragment().onSupportNavigateUp();
                } else {
                    finishAffinity();
                }
            }
        });
    }

    /**
     * If the ViewPager has a relative height (0dp), touch does not work on children.
     */
    protected void fixViewPagerHeight(
            View ignoredV,
            int ignoredLeft,
            int ignoredTop,
            int ignoredRight,
            int ignoredBottom,
            int ignoredOldLeft,
            int ignoredOldTop,
            int ignoredOldRight,
            int ignoredOldBottom
    ) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getViewPager().getLayoutParams();
        if (lp.bottomMargin != getNavView().getHeight()) {
            lp.bottomMargin = getNavView().getHeight();
            getViewPager().setLayoutParams(lp);
        }
    }

    private void createDbRootFolder() {
        try {
            Files.createDirectories(AppDeckDbUtil.getInstance(getApplicationContext())
                    .getDbFolder(getApplicationContext()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeShadowUnderBar() {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    @UiThread
    private void slideToFragment() {
        if (currentFragmentId == -1) {
            loadDefaultFragment();
        } else if (currentFragmentId == FRAGMENT_RECENT_DECKS) {
            loadRecentDecksFragment();
        } else if (currentFragmentId == FRAGMENT_ALL_DECKS) {
            loadAllDecksFragment();
        } else {
            loadDefaultFragment();
        }
    }

    private void loadDefaultFragment() {
        Disposable disposable = getAppDb().deckRxDao().findByLastUpdatedAt(1)
                .doOnSuccess(deck -> runOnUiThread(() -> {
                    if (deck.size() > 0) {
                        loadRecentDecksFragment();
                    } else {
                        loadAllDecksFragment();
                    }
                }))
                .subscribeOn(Schedulers.io())
                .ignoreElement()
                .subscribe(EMPTY_ACTION, this::onErrorLoadFragment);
        addToDisposable(disposable);
    }

    @UiThread
    private void loadRecentDecksFragment() {
        getViewPager().setCurrentItem(0);
        currentFragmentId = 0;
    }

    @UiThread
    private void loadAllDecksFragment() {
        getViewPager().setCurrentItem(1);
        currentFragmentId = 1;
    }

    protected void onErrorLoadFragment(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, this, TAG,
                "Error while loading first screen."
        );
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            switch (menuItem.getItemId()) {
                case R.id.recent_decks -> loadRecentDecksFragment();
                case R.id.all_decks -> loadAllDecksFragment();
                default -> throw new UnsupportedOperationException("Are you hiding any other fragments from me?");
            }
            return true;
        }
        return false;
    }

    /* -----------------------------------------------------------------------------------------
     * Activity methods overridden
     * ----------------------------------------------------------------------------------------- */

    @Override
    protected void onResume() {
        super.onResume();
        initDarkMode();
        super.onResume();
        showExceptionCrashedActivities();
        getRecentDecksFragment().setRefreshCards(true);
        getAllDecksFragment().setRefreshCards(true);
    }

    protected void checkAppDbIntegrity(@NonNull Runnable r) {
        try {
            r.run();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            getAppDbMainThreadUtil().deleteDatabase(this.getApplicationContext());
            r.run();
        }
    }

    /**
     * It is not needed elsewhere, because
     * 1) this is the first activity
     * 2) after changing the app settings, it comes back to this activity
     */
    protected void initDarkMode() {
        checkAppDbIntegrity(() -> {
            String darkMode = getAppDbMainThread().appConfigDao()
                    .getStringByKey(AppConfig.DARK_MODE);

            if (darkMode == null) {
                setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                return;
            }

            switch (darkMode) {
                case AppConfig.DARK_MODE_ON -> setDarkMode(AppCompatDelegate.MODE_NIGHT_YES);
                case AppConfig.DARK_MODE_OFF -> setDarkMode(AppCompatDelegate.MODE_NIGHT_NO);
                default -> setDarkMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
    }

    protected void setDarkMode(@AppCompatDelegate.NightMode int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        setBarSameColoursAsToolbar();
    }

    protected void showExceptionCrashedActivities() {
        App app = (App) getApplicationContext();
        if (app.getExceptionToDisplay() != null) {
            getExceptionHandler().handleException(
                    app.getExceptionToDisplay(), this, TAG
            );
            app.setExceptionToDisplay(null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isAllDecksFragment()) {
            getAllDecksFragment().onSupportNavigateUp();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(CURRENT_FRAGMENT_ID, currentFragmentId);
        if (fileSyncLauncher != null) {
            savedInstanceState.putString(FILESYNC_DECK_DB_PATH, fileSyncLauncher.getDeckDbPath());
        }
        if (fileSyncProLauncher != null) {
            savedInstanceState.putString(FILESYNC_PRO_DECK_DB_PATH, fileSyncProLauncher.getDeckDbPath());
        }
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT_ID);
        if (fileSyncLauncher != null) {
            fileSyncLauncher.setDeckDbPath(savedInstanceState.getString(FILESYNC_DECK_DB_PATH));
        }
        if (fileSyncProLauncher != null) {
            fileSyncProLauncher.setDeckDbPath(savedInstanceState.getString(FILESYNC_PRO_DECK_DB_PATH));
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Actions (public)
     * ----------------------------------------------------------------------------------------- */

    public void showBackArrow() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void hideBackArrow() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void refreshItems() {
        getRecentDecksFragment().setRefreshCards(true);
        getAllDecksFragment().setRefreshCards(true);

        if (isRecentDecksFragment()) {
            getRecentDecksFragment().getAdapter().loadItems();
        }
        if (isAllDecksFragment()) {
            getAllDecksFragment().getAdapter().loadItems();
        }
    }

    /* -----------------------------------------------------------------------------------------
     * Get/Sets View
     * ----------------------------------------------------------------------------------------- */

    protected ViewPager2 getViewPager() {
        return findViewById(R.id.viewPager);
    }

    /* -----------------------------------------------------------------------------------------
     * Gets/Sets
     * ----------------------------------------------------------------------------------------- */

    public MainAdapter getAdapter() {
        return adapter;
    }

    @NonNull
    protected SearchDecksFragment getAllDecksFragment() {
        return adapter.getAllDecksFragment();
    }

    @NonNull
    protected ListRecentDecksFragment getRecentDecksFragment() {
        return adapter.getRecentDecksFragment();
    }

    protected boolean isRecentDecksFragment() {
        return getViewPager().getCurrentItem() == 0;
    }

    protected boolean isAllDecksFragment() {
        return getViewPager().getCurrentItem() == 1;
    }

    @Nullable
    public FileSyncLauncher getFileSyncLauncher() {
        return fileSyncLauncher;
    }

    @Nullable
    public FileSyncProLauncher getFileSyncProLauncher() {
        return fileSyncProLauncher;
    }

    @NonNull
    public ExportImportDbRxUtil getExportImportDbUtil() {
        return exportImportDbUtil;
    }

    @NonNull
    public BottomNavigationView getNavView() {
        return binding.navView;
    }
}