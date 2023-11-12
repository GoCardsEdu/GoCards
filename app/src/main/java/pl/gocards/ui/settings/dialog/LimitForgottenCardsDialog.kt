package pl.gocards.ui.settings.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.gocards.ui.kt.theme.AppTheme
import pl.gocards.R

@Preview(showBackground = true)
@Composable
fun PreviewLimitForgottenCardsDialog() {
    AppTheme(isDarkTheme = true, preview = true) {
        LimitForgottenCardsDialog(
            LimitForgottenCardsDialogEntity(
                maxForgottenCards = remember { mutableStateOf("200") },
            )
        )
    }
}

data class LimitForgottenCardsDialogEntity(
    val maxForgottenCards: State<String>,
    val onValueChange: (Any) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

/**
 * S_U_03 This deck: Limit forgotten cards
 * S_U_05 All decks: Limit forgotten cards
 *
 * @author Grzegorz Ziemski
 */
@Composable
fun LimitForgottenCardsDialog(entity: LimitForgottenCardsDialogEntity) {
    SettingsAlertDialog(
        title = stringResource(R.string.settings_limit_forgotten_cards_title),
        body = {
            Column {
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(text = stringResource(R.string.settings_limit_forgotten_cards_desc))
                }
                Row {
                    Column (modifier = Modifier.weight(1f)) {
                        Slider(
                            value = if (entity.maxForgottenCards.value.isNotEmpty()) entity.maxForgottenCards.value.toFloat() else 0f,
                            onValueChange = entity.onValueChange,
                            valueRange = 0f..100f
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(6.dp, 7.dp, 0.dp, 0.dp)
                            .width(50.dp),
                    ) {
                        BasicTextField(
                            value = entity.maxForgottenCards.value,
                            onValueChange = entity.onValueChange,
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        ) {
                            Box(
                                modifier = Modifier.padding(8.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) { it() }
                        }
                    }
                }
            }
        },
        onSave = entity.onSave,
        onDismiss = entity.onDismiss,
    )
}
