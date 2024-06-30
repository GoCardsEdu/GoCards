package pl.gocards.ui.decks.decks.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.gocards.R


@Preview(showBackground = true)
@Composable
fun EmptyFolderPreview() {
    EmptyFolder(
        stringResource(R.string.no_decks_in_folder),
        input = EmptyFolderData()
    )
}

data class EmptyFolderData(
    val onClickNewDeck: () -> Unit = {},
    val onClickNewFolder: (() -> Unit)? = null,
    val onClickCreateSampleDeck: () -> Unit = {},
    val onClickImport: (() -> Unit)? = {}
)

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun EmptyFolder(
    title: String,
    input: EmptyFolderData,
) {
    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .align(alignment = Alignment.CenterVertically),
                )
            }

            Row {
                Column {
                    Box(Modifier.padding(
                            bottom = 20.dp,
                            end = 10.dp
                        )
                    ) {
                        EmptyFolderButton(
                            Icons.Filled.Add,
                            R.string.decks_list_menu_new_deck,
                            input.onClickNewDeck
                        )
                    }

                    if (input.onClickNewFolder != null) {
                        EmptyFolderButton(
                            Icons.Filled.Folder,
                            R.string.decks_list_menu_new_folder,
                            input.onClickNewFolder
                        )
                    }
                }

                Column {
                    Box(Modifier.padding(bottom = 20.dp)) {
                        EmptyFolderButton(
                            Icons.Filled.Add,
                            R.string.no_decks_create_sample_deck_button,
                            input.onClickCreateSampleDeck
                        )
                    }
                    input.onClickImport?.let {
                        EmptyFolderButton(
                            Icons.Filled.Download,
                            R.string.no_decks_import_file,
                            it,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyFolderButton(
    icon: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(180.dp)
            .height(50.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                icon,
                modifier = Modifier.padding(end = 5.dp),
                contentDescription = stringResource(text)
            )
            Text(
                text = stringResource(text),
                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                textAlign = TextAlign.Start,
                fontSize = fontSize
            )
        }
    }
}