package pl.gocards.ui.discover.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import pl.gocards.R
import pl.gocards.ui.discover.DiscoverCard

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun CommunityCard(
    onDiscordClick: () -> Unit,
    onFanpageClick: () -> Unit
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
            DiscordButton()
            FanpageButton(onFanpageClick)
        },
        onClickBody = onDiscordClick
    )
}


@Composable
private fun DiscordButton() {
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
}

@Composable
private fun FanpageButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClick = onClick
            )
    ) {
        Row(modifier = Modifier.padding(15.dp, 10.dp)) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.facebook),
                stringResource(R.string.facebook)
            )
            Text(
                modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                text = stringResource(R.string.discover_community_open_fanpage)
            )
        }
    }
}