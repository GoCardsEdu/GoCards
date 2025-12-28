package pl.gocards.ui.explore.underconstruction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import pl.gocards.ui.common.GoCardsButton

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun UnderConstructionPage(
    innerPadding: PaddingValues,
    input: UnderConstructionInput
) {
    if (input.showPoll.value) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val maxHeight = this.maxHeight
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .height(if (maxHeight < 400.dp) 400.dp else maxHeight)
            ) {
                val (infoBoxRef, pollingBoxRef) = createRefs()

                Box(modifier = Modifier
                    .constrainAs(infoBoxRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(pollingBoxRef.top)
                        width = Dimension.fillToConstraints
                    }) {
                    ComingSoonBox()
                }

                Box(modifier = Modifier
                    .constrainAs(pollingBoxRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(infoBoxRef.bottom)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }) {
                    PollBox(input.onClickYes, input.onClickNo)
                }
            }
        }
    } else {
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ComingSoonBox()
            }
        }
    }
}

@Composable
private fun ComingSoonBox() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Icon(
                Icons.Filled.Construction,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 20.dp),
                contentDescription = "Under Construction",
            )
        }
        Row {
            Text(
                text = "Coming soon...",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(bottom = 20.dp),
            )
        }
        Row {
            Text(
                text = "A new space to share and explore pre-made decks.",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .align(alignment = Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun PollBox(
    onClickYes: () -> Unit,
    onClickNo: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Text(
                text = "Would you like to explore this new functionality?",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, bottom = 20.dp)
                    .align(alignment = Alignment.CenterVertically),
            )
        }
        Row {
            GoCardsButton(
                Icons.Filled.Done,
                "Yes",
                onClickYes,
                Modifier.padding(end = 50.dp),
                14.sp,
                100.dp
            )
            GoCardsButton(
                Icons.Filled.Close,
                "No",
                onClickNo,
                Modifier,
                14.sp,
                width = 100.dp
            )
        }
    }
}

