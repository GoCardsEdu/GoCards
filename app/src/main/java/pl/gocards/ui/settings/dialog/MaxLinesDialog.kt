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
import pl.gocards.ui.theme.AppTheme
import pl.gocards.R

@Preview(showBackground = true)
@Composable
fun PreviewMaxLinesDialog() {
    AppTheme(isDarkTheme = true, preview = true) {
        MaxLinesDialog(
            MaxLinesDialogEntity(
                maxLines = remember { mutableStateOf("10") }
            )
        )
    }
}

data class MaxLinesDialogEntity(
    val maxLines: State<String>,
    val onValueChange: (Any) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)

/**
 * S_U_04 This deck: Max lines
 * S_U_06 All decks: Max lines
 * @author Grzegorz Ziemski
 */
@Composable
fun MaxLinesDialog(entity: MaxLinesDialogEntity) {
    SettingsAlertDialog(
        title = stringResource(R.string.settings_max_lines_title),
        body = {
            Column {
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text(text = stringResource(R.string.settings_max_lines_desc))
                }
                Row {
                    Column (modifier = Modifier.weight(1f)) {
                        Slider(
                            value = if (entity.maxLines.value.isEmpty()) 0f else entity.maxLines.value.toFloat(),
                            onValueChange = entity.onValueChange,
                            valueRange = 1f..50f
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(6.dp, 7.dp, 0.dp, 0.dp)
                            .width(50.dp),
                    ) {
                        BasicTextField(
                            value = entity.maxLines.value,
                            onValueChange = entity.onValueChange,
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
        onSave = entity.onSave,
        onDismiss = entity.onDismiss,
    )
}
