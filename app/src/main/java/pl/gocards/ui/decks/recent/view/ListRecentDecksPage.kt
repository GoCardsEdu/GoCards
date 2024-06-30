package pl.gocards.ui.decks.recent.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.R
import pl.gocards.ui.decks.decks.view.EmptyFolder
import pl.gocards.ui.decks.decks.view.EmptyFolderData

/**
 * @author Grzegorz Ziemski
 */
data class ListRecentDecksPageData(
    val isEmptyFolder: Boolean,
    val recyclerView: RecyclerView,
    val emptyFolder: EmptyFolderData
)

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun ListRecentDecksPage(
    innerPadding: PaddingValues,
    input: ListRecentDecksPageData
) {
    Column(Modifier.padding(innerPadding)) {
        if (input.isEmptyFolder) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {}
            EmptyFolder(
                title = stringResource(R.string.no_decks_recently_used),
                input.emptyFolder
            )
        } else {
            AndroidView(
                factory = { input.recyclerView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}