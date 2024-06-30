package pl.gocards.ui.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import pl.gocards.R
import pl.gocards.ui.theme.AppBar

/**
 * @author Grzegorz Ziemski
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun EmptyDecksTopBar(
    onBack: () -> Unit,
    isDarkTheme: Boolean
) {
    AppBar(
        isDarkTheme = isDarkTheme,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        },
        onBack = {
            onBack()
        },
        actions = {
        },
        modifier = Modifier
    )
}