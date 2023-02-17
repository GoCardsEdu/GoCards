package pl.softfly.flashcards.ui.deck.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.softfly.flashcards.ui.kt.theme.FlashCardsTheme
import pl.softfly.flashcards.ui.kt.theme.Grey600
import pl.softfly.flashcards.ui.kt.theme.Grey800

@Preview(showBackground = true)
@Composable
fun PreviewLimitForgottenCardsDialog() {
    FlashCardsTheme(isDarkTheme = true, preview = true) {
        LimitForgottenCardsDialog(
            maxForgottenCards = remember { mutableStateOf("200") },
        )
    }
}

@Composable
fun LimitForgottenCardsDialog(
    maxForgottenCards: State<String>,

    //Actions
    onValueChange: (Any) -> Unit = {},
    onSave: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    SettingsAlertDialog(
        title = "Limit forgotten cards",
        body = {
            Column {
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(text = "If the limit of clicks on the \"Again\" button is reached, the study session will return to the first unmemorized card.")
                }
                Row {
                    Column (modifier = Modifier.weight(1f)) {
                        Slider(
                            value = if (maxForgottenCards.value.isNotEmpty()) maxForgottenCards.value.toFloat() else 0f,
                            onValueChange = onValueChange,
                            valueRange = 0f..100f
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(6.dp, 7.dp, 0.dp, 0.dp)
                            .width(50.dp),
                    ) {
                        BasicTextField(
                            value = maxForgottenCards.value,
                            onValueChange = onValueChange,
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            decorationBox = {
                                Box(
                                    modifier = Modifier.padding(8.dp),
                                    contentAlignment = Alignment.BottomEnd
                                ) { it() }
                            }
                        )
                    }
                }
            }
        },
        onSave = onSave,
        onDismiss = onDismiss,
    )
}
