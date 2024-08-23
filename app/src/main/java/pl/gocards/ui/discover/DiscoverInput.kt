package pl.gocards.ui.discover

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.ui.common.OpenUrl
import pl.gocards.ui.discover.premium.BillingClient
import pl.gocards.ui.discover.premium.PremiumInput
import pl.gocards.ui.discover.premium.PremiumViewModel
import pl.gocards.ui.discover.review.InAppReviewClient
import pl.gocards.ui.discover.review.ReviewInput
import pl.gocards.ui.discover.review.ReviewViewModel
import pl.gocards.util.Config
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * @author Grzegorz Ziemski
 */
data class DiscoverInput(
    val premium: PremiumInput,
    val review: ReviewInput,
    val onClickDiscord: () -> Unit,
    val onFanpageClick: () -> Unit,
    val onYoutubeClick: () -> Unit
)

class DiscoverInputFactory {

    private lateinit var analytics: FirebaseAnalyticsHelper
    private lateinit var activity: Activity
    private lateinit var context: Context
    private lateinit var scope: LifecycleCoroutineScope

    fun create(
        premiumViewModel: PremiumViewModel,
        billingClient: BillingClient,
        reviewViewModel: ReviewViewModel,
        inAppReviewClient: InAppReviewClient,
        analytics: FirebaseAnalyticsHelper,
        activity: Activity,
        scope: LifecycleCoroutineScope
    ): DiscoverInput {
        this.analytics = analytics
        this.activity = activity
        this.context = activity
        this.scope = scope

        return DiscoverInput(
            onClickDiscord = { openDiscord() },
            onFanpageClick = { openFanpage() },
            onYoutubeClick = { openYoutube() },
            premium = PremiumInput(
                isPremium = premiumViewModel.isPremium(),
                isPremiumSwitch = premiumViewModel.isPremiumSwitch,
                setPremium = { premiumViewModel.isPremiumSwitch.value = true },
                onClickBuyPremium = {
                    scope.launch {
                        if (isPremiumMockEnabled()) {
                            premiumViewModel.enablePremium()
                        } else {
                            billingClient.launch(activity)
                        }
                    }
                },
                onDisableSubscription = {
                    if (isPremiumMockEnabled()) {
                        scope.launch {
                            premiumViewModel.disablePremium()
                        }
                    } else {
                        openSubscriptions()
                    }
                },
                onOpenSubscriptions = {
                    openSubscriptions()
                }
            ),
            review = ReviewInput(
                canReview = reviewViewModel.canReview,
                onClickReview = { inAppReviewClient.launch() }
            )
        )
    }

    private fun openDiscord() {
        analytics.discoverOpenDiscord()
        openUrl(Config.getInstance(this.context).discordUrl(this.context))
    }

    private fun openFanpage() {
        analytics.discoverOpenFanpage()
        openUrl(Config.getInstance(this.context).fanpageUrl(this.context))
    }

    private fun openYoutube() {
        analytics.discoverOpenYoutube()
        openUrl(Config.getInstance(this.context).youtubeUrl(this.context))
    }

    private fun openSubscriptions() {
        openUrl(Config.getInstance(this.context).subscriptionsUrl(this.context))
    }

    private fun isPremiumMockEnabled(): Boolean {
        return Config.getInstance(context)
            .isPremiumMockEnabled(context)
    }

    private fun openUrl(link: String) {
        OpenUrl.openUrl(activity, scope, link)
    }
}