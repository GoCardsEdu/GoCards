package pl.gocards.ui.explore

import androidx.compose.runtime.State
import pl.gocards.ui.explore.underconstruction.UnderConstructionInput

/**
 * @author Grzegorz Ziemski
 */
data class ExploreInput(
    val menu: ExploreMenuData,
    val token: State<String?>,
    val onClickLogin: () -> Unit,
    val underConstruction: UnderConstructionInput,
    val onBack: () -> Unit
)