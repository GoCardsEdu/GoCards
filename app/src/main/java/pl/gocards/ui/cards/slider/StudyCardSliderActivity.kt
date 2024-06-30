package pl.gocards.ui.cards.slider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import pl.gocards.App
import pl.gocards.db.deck.DeckDbUtil
import pl.gocards.ui.cards.slider.model.SliderCardsViewModel
import pl.gocards.ui.cards.slider.slider.CardSliderScaffold
import pl.gocards.ui.cards.slider.slider.CardSliderScaffoldInputFactory
import pl.gocards.ui.cards.slider.slider.model.Mode
import pl.gocards.ui.filesync_pro.AutoSyncViewModel

/**
 * C_R_30 Study the cards
 * @author Grzegorz Ziemski
 */
@Immutable
class StudyCardSliderActivity : ComponentActivity() {

    companion object {
        const val DECK_DB_PATH = "DECK_DB_PATH"
    }

    private var autoSyncCardsModel: AutoSyncViewModel? = null

    private lateinit var viewModel: SliderCardsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deckDbPath = intent.getStringExtra(DECK_DB_PATH) ?: return
        val context = this
        val owner = this
        val application = application as App

        viewModel = SliderCardsViewModel.getInstance(
            context,
            deckDbPath,
            Mode.STUDY,
            this
        )
        autoSyncCardsModel = AutoSyncViewModel.getInstance(deckDbPath, owner, application)


        autoSyncCardsModel?.autoSync {
            viewModel.loadForgottenCards()
        }

        setContent {
            LaunchedEffect(true) {
                if (!viewModel.isLoaded()) {
                    viewModel.loadForgottenCards()
                } else {
                    viewModel.refreshSettledPage()
                }
            }

            CardSliderScaffold(
                CardSliderScaffoldInputFactory().getInstance(
                    onBack = { super.finish() },
                    deckName = DeckDbUtil.getDeckName(deckDbPath),
                    viewModel = viewModel,
                    autoSyncCardsModel = autoSyncCardsModel,
                    showRateButtons = true,
                    application = application
                )
            )
        }

        this.onBackPressedDispatcher.addCallback(this) {
            if (!viewModel.handleOnBackPressed()) super.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCard()
        autoSyncCardsModel?.autoSync { }
    }
}
