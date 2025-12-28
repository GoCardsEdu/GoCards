package pl.gocards.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun GoCardsButton(
    icon: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
) {
    GoCardsButton(icon, stringResource(text), onClick, Modifier, fontSize)
}

@Composable
fun GoCardsButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    width: Dp = 180.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(width)
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                icon,
                modifier = Modifier.padding(end = 5.dp),
                contentDescription = text
            )
            Text(
                text = text,
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                textAlign = TextAlign.Start,
                fontSize = fontSize
            )
        }
    }
}