package pl.gocards.ui.cards.kt.slider.page.study.model

import androidx.compose.ui.unit.sp
import pl.gocards.db.room.AppDatabase
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.CardConfig
import pl.gocards.room.entity.deck.DeckConfig

/**
 * @author Grzegorz Ziemski
 */
open class DisplaySettingsStudyCardsModel(
    val deckDb: DeckDatabase,
    val appDb: AppDatabase,
    val deckDbPath: String,
) {

    private var windowHeightPx: Int? = null

    /* -----------------------------------------------------------------------------------------
     * Set current page
     * ----------------------------------------------------------------------------------------- */

    fun copyDisplaySettings(card: StudyCardUi, previousCard: StudyCardUi?) {
        if (!card.canPreSetup) return

        if (previousCard != null) {
            if (card.termFontSize.value == null) {
                card.termFontSize.value = previousCard.termFontSize.value
            }
            if (card.defFontSize.value == null) {
                card.defFontSize.value = previousCard.defFontSize.value
            }
        } else {
            if (card.termFontSize.value == null) {
                card.termFontSize.value = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT.sp
            }
            if (card.defFontSize.value == null) {
                card.defFontSize.value = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT.sp
            }
        }
        setTermHeightPx(card, previousCard)
    }

    fun setTermHeightPx(
        card: StudyCardUi,
        previousCard: StudyCardUi?
    ) {
        val lastTermHeightPx = previousCard?.termHeightPx?.value
        setTermHeightPx(card, windowHeightPx, lastTermHeightPx)
    }

    private fun setTermHeightPx(
        card: StudyCardUi,
        windowHeightPx: Int?,
        lastTermHeightPx: Float?
    ) {
        if (windowHeightPx == null && lastTermHeightPx == null) return
        card.termHeightPx.value = getTermHeightPx(
            card.displayRatio,
            windowHeightPx,
            lastTermHeightPx
        )
    }

    private fun getTermHeightPx(
        displayRatio: Float,
        windowHeightPx: Int?,
        lastTermHeightPx: Float?
    ): Float {
        if (displayRatio == 0f) {
            if (lastTermHeightPx != null) {
                return lastTermHeightPx
            } else if (windowHeightPx != null) {
                return CardConfig.STUDY_CARD_TD_DISPLAY_RATIO_DEFAULT * windowHeightPx
            }
        } else if (windowHeightPx != null) {
            return displayRatio * windowHeightPx
        }
        return 0f
    }

    fun setWindowHeightPx(windowHeightPx: Int) {
        if (windowHeightPx <= 0) throw IllegalArgumentException()
        this.windowHeightPx = windowHeightPx
    }

    /* -----------------------------------------------------------------------------------------
     * Save
     * ----------------------------------------------------------------------------------------- */

    suspend fun saveDisplaySettings(card: StudyCardUi) {
        saveTermFontSize(card)
        saveDefinitionFontSize(card)
        saveDisplayRatio(card)
    }

    private suspend fun saveTermFontSize(card: StudyCardUi) {
        val termFontSize = card.termFontSize.value?.value?.toInt() ?: return

        deckDb.cardConfigKtxDao().updateStudyCardTermFontSize(
            card.id,
            termFontSize
        )
    }

    private suspend fun saveDefinitionFontSize(card: StudyCardUi) {
        val defFontSize = card.defFontSize.value?.value?.toInt() ?: return

        deckDb.cardConfigKtxDao().updateStudyCardDefinitionFontSize(
            card.id,
            defFontSize
        )
    }

    private suspend fun saveDisplayRatio(card: StudyCardUi) {
        val windowHeightPx = this.windowHeightPx ?: return
        val termHeightPx = card.termHeightPx.value ?: return
        val displayRatio = termHeightPx / windowHeightPx
        deckDb.cardConfigKtxDao().updateStudyCardTdDisplayRatio(card.id, displayRatio)
    }

    /* -----------------------------------------------------------------------------------------
     * Others
     * ----------------------------------------------------------------------------------------- */

    /**
     * C_U_38 Reset view settings
     */
    fun resetView(card: StudyCardUi) {
        card.termFontSize.value = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT.sp
        card.defFontSize.value = DeckConfig.STUDY_CARD_FONT_SIZE_DEFAULT.sp
        card.termHeightPx.value = getTermHeightPx(0.5f, windowHeightPx, null)
    }
}