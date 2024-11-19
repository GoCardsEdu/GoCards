package pl.gocards.ui.home.view

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import pl.gocards.App
import pl.gocards.ui.decks.all.view.AllDecksInput
import pl.gocards.ui.decks.all.view.AllDecksInputFactory
import pl.gocards.ui.decks.decks.service.ExportImportDbKtxUtil
import pl.gocards.ui.decks.decks.view.DeckBottomMenuInput
import pl.gocards.ui.decks.decks.view.actions.ExportDeckActions
import pl.gocards.ui.decks.decks.view.actions.SyncDeckAction
import pl.gocards.ui.decks.recent.ListRecentDecksAdapter
import pl.gocards.ui.decks.recent.view.RecentDecksInput
import pl.gocards.ui.decks.recent.view.RecentDecksInputFactory
import pl.gocards.ui.decks.search.SearchFoldersDecksAdapter
import pl.gocards.ui.discover.DiscoverInput
import pl.gocards.ui.discover.DiscoverInputFactory
import pl.gocards.ui.discover.premium.BillingClient
import pl.gocards.ui.discover.premium.PremiumViewModel
import pl.gocards.ui.discover.review.InAppReviewClient
import pl.gocards.ui.discover.review.ReviewViewModel
import pl.gocards.ui.explore.underconstruction.PollViewModel
import pl.gocards.ui.explore.underconstruction.UnderConstructionInput
import pl.gocards.ui.explore.underconstruction.UnderConstructionInputFactory
import pl.gocards.ui.filesync.FileSyncViewModel
import pl.gocards.ui.home.HomeActivity
import pl.gocards.util.FirebaseAnalyticsHelper
import java.nio.file.Path

@Immutable
data class HomeInput(
    val isDarkTheme: Boolean,
    val pagerState: PagerState,
    val recentDecks: RecentDecksInput,
    val allDecks: AllDecksInput,
    val explore: UnderConstructionInput,
    val discover: DiscoverInput,
    val deckBottomMenu: DeckBottomMenuInput,
)

/**
 * @author Grzegorz Ziemski
 */
class HomeInputFactory {

    private lateinit var recentAdapter: ListRecentDecksAdapter
    private lateinit var allAdapter: SearchFoldersDecksAdapter


    @Composable
    fun create(
        activity: HomeActivity
    ): HomeInput {
        return create(
            activity.recentAdapter!!,
            activity.allAdapter!!,

            activity.allAdapter!!.isShownMoreDeckMenu,
            activity.fileSyncViewModel,
            activity.premiumViewModel,
            activity.billingClient,
            activity.reviewViewModel,
            activity.inAppReviewClient,
            activity.exploreViewModel,

            { activity.handleOnBackPressed() },

            activity,
            activity
        )
    }

    @Composable
    private fun create(
        recentAdapter: ListRecentDecksAdapter,
        allAdapter: SearchFoldersDecksAdapter,

        isShownMoreDeckMenu: MutableState<Path?>,
        fileSyncViewModel: FileSyncViewModel?,
        premiumViewModel: PremiumViewModel,
        billingClient: BillingClient,

        reviewViewModel: ReviewViewModel,
        inAppReviewClient: InAppReviewClient,

        exploreViewModel: PollViewModel,

        onBack: () -> Unit,

        activity: Activity,
        owner: LifecycleOwner
    ): HomeInput {
        this.recentAdapter = recentAdapter
        this.allAdapter = allAdapter
        val scope = owner.lifecycleScope

        val application = activity.applicationContext as App

        val analytics = FirebaseAnalyticsHelper.getInstance(application)
        val exportImportDbUtil = ExportImportDbKtxUtil(scope, activity).getInstance { onRefreshItems() }
        val exportImport = ExportDeckActions(fileSyncViewModel, activity, owner)
        val sync = SyncDeckAction({onRefreshItems()}, activity, owner)
        val startActivity = StartActivityActions(activity, analytics, scope)

        return HomeInput(
            isDarkTheme = application.getDarkMode() ?: isSystemInDarkTheme(),
            pagerState = rememberPagerState(pageCount = { HomePage.entries.size }),
            recentDecks = RecentDecksInputFactory(
                recentAdapter,
                fileSyncViewModel,
                startActivity,
                onBack,
                { onRefreshItems() },
                activity,
                owner,
                application
            ).create(),
            allDecks = AllDecksInputFactory(
                allAdapter,
                fileSyncViewModel,
                premiumViewModel,
                startActivity,
                onBack,
                { onRefreshItems() },
                activity,
                owner,
                application
            ).create(),
            explore = UnderConstructionInputFactory().create(
                exploreViewModel,
                analytics,
                scope
            ),
            discover = DiscoverInputFactory().create(
                premiumViewModel,
                billingClient,
                reviewViewModel,
                inAppReviewClient,
                analytics,
                activity,
                scope
            ),
            deckBottomMenu = DeckBottomMenuInput(
                isShown = isShownMoreDeckMenu,
                onSync = sync.onClickSync(),
                onClickExportExcel = exportImport.onExportExcel(),
                onExportCsv = exportImport.onExportCsv(),
                onExportDb = { exportImportDbUtil.launchExportDb(it.toString()) },
                onDeckSettings = { startActivity.startDeckSettingsActivity(it.toString()) },
            ),
        )
    }

    private fun onRefreshItems() {
        recentAdapter.loadItems()
        allAdapter.loadItems()
    }
}