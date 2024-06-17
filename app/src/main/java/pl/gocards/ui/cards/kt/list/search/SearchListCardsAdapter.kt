package pl.gocards.ui.cards.kt.list.search

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.ui.cards.kt.list.ListCardsActivity
import pl.gocards.ui.cards.kt.list.select.SelectCardsViewModel
import pl.gocards.ui.cards.kt.list.select.SelectListCardsAdapter
import pl.gocards.ui.kt.theme.ExtendedColors

/**
 * C_R_02 Search cards
 * @author Grzegorz Ziemski
 */
open class SearchListCardsAdapter(
    override val viewModel: SearchListCardsViewModel,
    selectViewModel: SelectCardsViewModel,
    snackbarHostState: SnackbarHostState,
    colors: ExtendedColors,
    activity: ListCardsActivity,
    scope: CoroutineScope
): SelectListCardsAdapter(
    viewModel,
    selectViewModel,
    snackbarHostState,
    colors,
    activity,
    scope
) {

    @SuppressLint("NotifyDataSetChanged")
    fun search(query: String) {
        viewModel.search(query) {
            scope.launch {
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearSearch() {
        viewModel.disableSearch {
            scope.launch {
                notifyDataSetChanged()
            }
        }
    }

    override fun setText(textView: TextView, value: String) {
        if (viewModel.getSearchQuery().value.isNullOrEmpty()) {
            super.setText(textView, value)
        } else {

            val bg = Integer.toHexString(
                0xFFFFFF and colors.colorItemSearch.toArgb()
            )
            val searchedMarked = value
                .replace("{search}", "<span style='background-color:#$bg;'>")
                .replace("{esearch}", "</span>")
            textView.text = htmlUtil.fromHtml(searchedMarked)
        }
    }
}