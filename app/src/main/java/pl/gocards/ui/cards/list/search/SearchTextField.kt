package pl.gocards.ui.cards.list.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

/**
 * C_R_02 Search cards
 * @author Grzegorz Ziemski
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchTextField(
    searchQuery: State<String?>,
    onValueChange: (String) -> Unit,
) {
    val focusRequester = FocusRequester()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = searchQuery.value ?: "",
        onValueChange = {
            onValueChange(it)
        },
        textStyle = MaterialTheme.typography.titleLarge.copy(
            color = LocalContentColor.current
        ),
        cursorBrush = SolidColor(LocalContentColor.current),
        singleLine = true,
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = searchQuery.value ?: "",
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = { Text(text = "Search...", fontSize = 20.sp) },
                label = null,
                leadingIcon = null,
                trailingIcon = null,
                prefix = null,
                suffix = null,
                supportingText = null,
                shape = TextFieldDefaults.shape,
                singleLine = true,
                enabled = true,
                isError = false,
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                ),
                container = {}
            )
        }
    )
}