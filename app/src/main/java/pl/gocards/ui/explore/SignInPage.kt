package pl.gocards.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.gocards.R
import pl.gocards.ui.common.GoCardsButton

/**
 * @author Grzegorz Ziemski
 */
@Composable
fun SignInPage(
    innerPadding: PaddingValues,
    token: String?,
    onClickLogin: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Icon(
                    Icons.Default.Public,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(top = 20.dp, bottom = 20.dp),
                    contentDescription = stringResource(R.string.login),
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.explore_sing_in_desc),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .align(alignment = Alignment.CenterVertically),
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.explore_sing_in_login_required),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 80.dp)
                        .align(alignment = Alignment.CenterVertically),
                )
            }
            GoCardsButton(
                Icons.AutoMirrored.Filled.Login,
                stringResource(R.string.login),
                onClickLogin,
                14.sp,
                Modifier.padding(bottom = 20.dp),
                120.dp
            )
            if (token != null) {
                Row {
                    SelectionContainer {
                        Text(
                            text = token,
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        )
                    }
                }
            }
        }
    }
}
