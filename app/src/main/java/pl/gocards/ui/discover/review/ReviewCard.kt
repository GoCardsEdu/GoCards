package pl.gocards.ui.discover.review

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.discover.DiscoverCard

/**
 * @author Grzegorz Ziemski
 */
data class ReviewInput(
    val canReview: State<Boolean>,
    val onClickReview: () -> Unit
)

@Composable
fun ReviewCard(onClick: () -> Unit) {
    DiscoverCard(
        title = {
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier.padding(0.dp, 10.dp),
                    text = stringResource(R.string.discover_review_title)
                )
            }
        },
        body = {
            Text(
                modifier = Modifier.padding(15.dp),
                text = stringResource(R.string.discover_review_description)
            )
        },
        onClickBody = onClick
    )
}