package pl.gocards.ui.discover

import android.app.Activity
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
    val onFanpageClick: () -> Unit
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
        openUrl("https://discord.gg/jYyRnD27JP")
    }

    private fun openFanpage() {
        analytics.discoverOpenFanpage()
        openUrl("https://www.facebook.com/profile.php?id=100092886761908")
    }

    private fun openSubscriptions() {
        openUrl("https://play.google.com/store/account/subscriptions")
    }

    private fun isPremiumMockEnabled(): Boolean {
        return Config.getInstance(context)
            .isPremiumMockEnabled(context)
    }

    private fun openUrl(link: String) {
        OpenUrl.openUrl(activity, scope, link)
    }
}