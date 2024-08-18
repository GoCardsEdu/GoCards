package pl.gocards.ui.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.discover.premium.PremiumCard
import pl.gocards.ui.discover.review.ReviewCard


/**
 * @author Grzegorz Ziemski
 */
@Composable
fun DiscoverPage(
    innerPadding: PaddingValues,
    discover: DiscoverInput
) {
    Column(
        Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Column(
            Modifier
                .padding(bottom = 15.dp)
                .fillMaxSize()
        ) {
            if (discover.review.canReview.value) {
                ReviewCard(discover.review.onClickReview)
            }
            DiscordCard(discover.onClickDiscord)
            PremiumCard(discover.premium)
        }

    }
}

@Composable
private fun DiscordCard(
    onClick: () -> Unit
) {
    DiscoverCard(
        title = {
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier.padding(0.dp, 10.dp),
                    text = stringResource(R.string.discover_community_title),
                )
            }
        },
        body = {
            Text(
                modifier = Modifier.padding(15.dp, 10.dp),
                text = stringResource(R.string.discover_community_description)
            )
            Row(modifier = Modifier.padding(15.dp, 10.dp)) {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.discord),
                    stringResource(R.string.discord)
                )
                Text(
                    modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                    text = stringResource(R.string.discover_community_open_discord)
                )
            }
        },
        onClickBody = onClick
    )
}

@Composable
fun DiscoverCard(
    title: @Composable RowScope.() -> Unit,
    body: @Composable ColumnScope.() -> Unit,
    onClickBody: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 15.dp, 15.dp, 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(5),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp, 0.dp)
        ) {
            title()
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(5),
            onClick = onClickBody
        ) {
            body()
        }
    }
}