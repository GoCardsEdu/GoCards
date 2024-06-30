package pl.gocards.ui.cards.slider.slider

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import pl.gocards.App
import pl.gocards.R
import pl.gocards.ui.cards.slider.model.SliderCardsViewModel
import pl.gocards.ui.cards.slider.page.edit.model.EditCardUi
import pl.gocards.ui.cards.slider.page.study.model.StudyCardUi
import pl.gocards.ui.cards.slider.page.study.ui.definition.DefinitionButtonsActions
import pl.gocards.ui.cards.slider.slider.model.SliderCardUi
import pl.gocards.ui.common.pager.dynamic.DynamicPagerDto
import pl.gocards.ui.common.showSnackbar
import pl.gocards.ui.filesync_pro.AutoSyncViewModel
import pl.gocards.util.FirebaseAnalyticsHelper

/**
 * @author Grzegorz Ziemski
 */
data class CardSliderScaffoldInput(
    val onBack: () -> Unit = {},
    val isDarkTheme: Boolean,
    val preview: Boolean,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),

    val deckName: String,
    val sliderCards: List<SliderCardUi>,
    val loaded: Boolean = false,

    val dynamicPager: DynamicPagerDto<SliderCardUi>,

    val studyPage: StudyPage,
    val setWindowHeightPx: (Int) -> Unit = {},

    val editPage: EditPage,
    val newPage: NewPage
)

/**
 * @author Grzegorz Ziemski
 */
class CardSliderScaffoldInputFactory {

    private lateinit var snackbarHostState: SnackbarHostState
    private lateinit var scope: CoroutineScope

    private lateinit var cardDeletedMessage: String
    private lateinit var restoreLabel: String

    private lateinit var application: App
    private lateinit var viewModel: SliderCardsViewModel

    @Composable
    fun getInstance(
        onBack: () -> Unit = {},
        deckName: String,
        viewModel: SliderCardsViewModel,
        autoSyncCardsModel: AutoSyncViewModel?,
        showRateButtons: Boolean,
        application: App
    ): CardSliderScaffoldInput {
        this.viewModel = viewModel
        this.application = application

        snackbarHostState = remember { SnackbarHostState() }
        scope = rememberCoroutineScope()

        cardDeletedMessage = stringResource(R.string.cards_list_toast_deleted_card)
        restoreLabel = stringResource(R.string.restore)

        val analytics = FirebaseAnalyticsHelper.getInstance(application)

        return CardSliderScaffoldInput(onBack = onBack,
            isDarkTheme = application.darkMode ?: isSystemInDarkTheme(),
            preview = false,
            deckName = deckName,
            snackbarHostState = snackbarHostState,
            sliderCards = viewModel.sliderCardsModel.items.value,
            loaded = viewModel.sliderCardsModel.loaded.value,
            dynamicPager = DynamicPagerDto.create(viewModel.sliderCardsModel)
                .copy(
                    setSettledPage = { viewModel.setSettledPage(it) },
                ),
            studyPage = StudyPage(
                studyCards = viewModel.studyCardsModel.cards,
                editingLocked = autoSyncCardsModel?.getEditingLocked() ?: remember {
                    mutableStateOf(
                        false
                    )
                },
                buttons = DefinitionButtonsActions(
                    showRateButtons = showRateButtons,
                    onClickAgain = { page, sliderCard ->
                        viewModel.onAgainClick(page, sliderCard)
                        analytics.again()
                    },
                    onClickQuick = { page, sliderCard ->
                        viewModel.onQuickClick(page, sliderCard)
                        analytics.quick()
                    },
                    onClickEasy = { page, sliderCard ->
                        viewModel.onEasyClick(page, sliderCard)
                        analytics.easy()
                    },
                    onClickHard = { page, sliderCard ->
                        viewModel.onHardClick(page, sliderCard)
                        analytics.hard()
                    },
                ),
                onClickMenuEditCard = { page -> viewModel.editMode(page) },
                onClickMenuNewCard = { viewModel.addNewCard(it) },
                onClickMenuDeleteCard = { page, card ->
                    viewModel.deleteCard(page, card)
                    showSnackbarDeletedCard(card)
                },
                onClickMenuResetView = { card -> viewModel.studyCardsModel.resetView(card) },
            ),
            editPage = EditPage(editCards = viewModel.editCardsModel.getCardsState(),
                onClickMenuNewCard = { viewModel.addNewCard(it) },
                onClickMenuDeleteCard = { page, card ->
                    viewModel.deleteCard(page, card)
                    showSnackbarDeletedCard(card)
                },
                onClickMenuSaveEditCard = { page, card ->
                    viewModel.saveCard(page, card)
                    showShortToastMessage(R.string.card_edit_card_updated_toast)
                }),
            newPage = NewPage(newCards = viewModel.newCardsModel.getCardsState(),
                onClickMenuNewCard = { viewModel.addNewCard(it) },
                onClickMenuDeleteCard = { page, card -> viewModel.deleteCard(page, card) },
                onClickMenuSaveNewCard = { page, card ->
                    viewModel.saveNewCard(page, card)
                    showShortToastMessage(R.string.card_edit_card_added_toast)
                    analytics.createCard()
                }),
            setWindowHeightPx = { viewModel.setWindowHeightPx(it) })
    }

    private fun showSnackbarDeletedCard(card: SliderCardUi) {
        showSnackbar(
            cardDeletedMessage,
            restoreLabel,
            { onClickMenuRestoreCard(card) },
            snackbarHostState,
            scope
        )
    }

    private fun onClickMenuRestoreCard(card: SliderCardUi) {
        viewModel.restoreDeletedCard(card)
    }

    private fun showShortToastMessage(@StringRes resId: Int) {
        Toast.makeText(
            application, application.getString(resId), Toast.LENGTH_SHORT
        ).show()
    }
}

data class StudyPage(
    val studyCards: State<Map<Int, StudyCardUi>>,
    val editingLocked: State<Boolean> = mutableStateOf(false),
    val buttons: DefinitionButtonsActions = DefinitionButtonsActions(true),
    val onClickMenuEditCard: (page: Int) -> Unit = {},
    val onClickMenuNewCard: (page: Int) -> Unit = {},
    val onClickMenuDeleteCard: (page: Int, card: SliderCardUi) -> Unit = { _, _ -> },
    val onClickMenuResetView: (card: StudyCardUi) -> Unit = {},
)

data class EditPage(
    val editCards: State<Map<Int, EditCardUi>>,
    val onClickMenuNewCard: (page: Int) -> Unit = {},
    val onClickMenuDeleteCard: (page: Int, card: SliderCardUi) -> Unit = { _, _ -> },
    val onClickMenuSaveEditCard: (page: Int, card: EditCardUi) -> Unit = { _, _ -> },
)

data class NewPage(
    val newCards: State<Map<Int, EditCardUi>>,
    val onClickMenuNewCard: (page: Int) -> Unit = {},
    val onClickMenuDeleteCard: (page: Int, card: SliderCardUi) -> Unit = { _, _ -> },
    val onClickMenuSaveNewCard: (page: Int, card: EditCardUi) -> Unit = { _, _ -> }
)