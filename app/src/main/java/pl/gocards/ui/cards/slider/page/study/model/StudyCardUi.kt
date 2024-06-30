package pl.gocards.ui.cards.slider.page.study.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.TextUnit
import pl.gocards.room.entity.deck.CardLearningProgressAndHistory

/**
 * The card used in the UI slider.
 *
 * @author Grzegorz Ziemski
 */
data class StudyCardUi(
    var id: Int,

    /**
     * Synonyms: Front
     *
     * To simplify SQL queries, NULL values are not used.
     * It would have to be used "IS NULL" instead of "= NULL".
     */
    var term: String = "",

    /**
     * Synonyms: Back
     *
     * To simplify SQL queries, NULL values are not used.
     * It would have to be used "IS NULL" instead of "= NULL".
     */
    var definition: String = "",

    val isTermSimpleHtml: Boolean = false,
    val isTermFullHtml: Boolean = false,
    val isDefinitionSimpleHtml: Boolean = false,
    val isDefinitionFullHtml: Boolean = false,

    /**
     * Used when card does not have display settings yet.
     * The card will be opened for the first time ever.
     *
     * If true, it means that the card can be setup with the last default settings.
     * This makes the cards look nice when placed behind the main card when swiping the cards on the pager.
     *
     * If false, it means the card already has display settings defined.
     */
    var canPreSetup: Boolean = true,

    val termFontSize: MutableState<TextUnit?> = mutableStateOf(null), //24.sp

    val defFontSize: MutableState<TextUnit?> = mutableStateOf(null),  //24.sp

    val displayRatio: Float = 0.5f,

    val termHeightPx: MutableState<Float?> = mutableStateOf(null),

    val showDefinition: MutableState<Boolean> = mutableStateOf(false),

    val current: CardLearningProgressAndHistory?,

    val nextAfterAgain: CardLearningProgressAndHistory,

    val nextAfterQuick: CardLearningProgressAndHistory?,

    val nextAfterHard: CardLearningProgressAndHistory,

    val nextAfterEasy: CardLearningProgressAndHistory
)