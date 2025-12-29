package pl.gocards.ui.decks.all.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.recyclerview.widget.RecyclerView
import pl.gocards.R
import pl.gocards.ui.ALL_DECKS
import pl.gocards.ui.decks.decks.view.EmptyFolder
import pl.gocards.ui.decks.decks.view.EmptyFolderData
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
data class ListAllDecksPageData(
    val isEmptyFolder: Boolean,
    val recyclerView: RecyclerView,
    val folderPath: String?,
    val emptyFolder: EmptyFolderData,
    val showDeckPasteBar: Boolean,
    val showFolderPasteBar: Boolean,
    val cutPath: Path?,
    val onPaste: () -> Unit,
    val onCancel: () -> Unit
)

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun ListAllDecksPage(
    innerPadding: PaddingValues,
    input: ListAllDecksPageData
) {
    Column(Modifier
        .padding(innerPadding)
        .testTag(ALL_DECKS)
    ) {
        PathBar(input.folderPath)
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (bodyRef, pasteBarRef) = createRefs()
            Body(
                modifier = Modifier.constrainAs(bodyRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(pasteBarRef.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                input = input
            )
            PasteBar(
                modifier = Modifier.constrainAs(pasteBarRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
                showDeckPasteBar = input.showDeckPasteBar,
                showFolderPasteBar = input.showFolderPasteBar,
                onPaste = input.onPaste,
                onCancel = input.onCancel
            )
        }
    }
}

@Composable
private fun PathBar(folderPath: String?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 3.dp)
            .zIndex(2f)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.decks_list_root_path) + (folderPath ?: ""),
                modifier = Modifier.padding(20.dp, 5.dp),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    input: ListAllDecksPageData
) {
    Row(modifier) {
        if (input.isEmptyFolder) {
            EmptyFolder(
                title = stringResource(R.string.no_decks_in_folder),
                input.emptyFolder
            )
        } else {
            AndroidView(
                factory = { input.recyclerView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun PasteBar(
    modifier: Modifier,
    showDeckPasteBar: Boolean,
    showFolderPasteBar: Boolean,
    onPaste: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(modifier) {
        if (showDeckPasteBar || showFolderPasteBar) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    .padding(top = 2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.padding(end = 10.dp),
                    icon = Icons.Filled.ContentPaste,
                    text = if (showFolderPasteBar) {
                        R.string.decks_list_paste_folder_here
                    } else {
                        R.string.decks_list_paste_deck_here
                    },
                    fontSize = 15.sp,
                    onClick = onPaste
                )
                IconButton(
                    icon = Icons.Outlined.Cancel,
                    text = R.string.cancel,
                    fontSize = 15.sp,
                    onClick = onCancel
                )
            }
        }
    }
}

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes text: Int,
    fontSize: TextUnit,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row {
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