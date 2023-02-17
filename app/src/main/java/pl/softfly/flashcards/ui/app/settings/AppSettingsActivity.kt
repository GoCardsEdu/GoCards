package pl.softfly.flashcards.ui.app.settings


import android.app.Application
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.softfly.flashcards.R
import pl.softfly.flashcards.db.AppDatabaseUtil
import pl.softfly.flashcards.entity.app.AppConfig
import pl.softfly.flashcards.ui.kt.theme.FlashCardsAppBar
import pl.softfly.flashcards.ui.kt.theme.FlashCardsTheme
import pl.softfly.flashcards.ui.kt.theme.Grey900

@Preview(showBackground = true)
@Composable
fun PreviewAppSettings() {
    AppSettingsScaffold(
        isDarkTheme = false,
        darkModeDbS = remember { mutableStateOf(
            AppConfig(
                null,
                AppConfig.DARK_MODE_OPTIONS[1]
            )
        ) },
        darkModeOptionSelectedS = remember { mutableStateOf(AppConfig.DARK_MODE_OPTIONS[1]) },
        showDarkModeDialog = remember { mutableStateOf(false) },
        preview = true
    )
}

class AppSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitViewModelAppSettings(
                onBack = { super.finish() },
            )
        }
    }
}

/**
 * It is separate so as not to create and compose a ViewModel all the time.
 */
@Composable
fun InitViewModelAppSettings(onBack: () -> Unit = {}) {
    InitAppSettings(
        onBack = onBack,
        viewModel = AppSettingsViewModel(
            AppDatabaseUtil.getInstance(LocalContext.current).database,
            isSystemInDarkTheme(),
            LocalContext.current.applicationContext as Application
        )
    )
}

@Composable
fun InitAppSettings(onBack: () -> Unit = {}, viewModel: AppSettingsViewModel) {
    val showDarkModeDialog = remember { mutableStateOf(false) }
    AppSettingsScaffold(
        isDarkTheme = viewModel.isDarkTheme.observeAsState().value ?: isSystemInDarkTheme(),
        darkModeDbS = viewModel.darkModeDb.observeAsState(),
        darkModeOptionSelectedS = viewModel.darkModeOptionSelected.observeAsState(),
        showDarkModeDialog = showDarkModeDialog,
        onBack = onBack,
        onClickDarkModeField = { showDarkModeDialog.value = true },
        onSelectDarkMode = { viewModel.setDarkModeOption(it) },
        onSaveDarkMode = {
            showDarkModeDialog.value = false
            viewModel.darkModeOptionSelected.value?.let { viewModel.updateDarkModeDb(it) }
        },
        onDismissDarkMode = {
            showDarkModeDialog.value = false
            viewModel.darkModeDb.value?.let {
                viewModel.setDarkModeOption(it.value)
            }
        },
        preview = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScaffold(
    isDarkTheme: Boolean,
    darkModeDbS: State<AppConfig?>?,
    darkModeOptionSelectedS: State<String?>,
    showDarkModeDialog: MutableState<Boolean>,
    onBack: () -> Unit = {},
    onClickDarkModeField: () -> Unit = {},
    onSelectDarkMode: (selected: String) -> Unit = {},
    onSaveDarkMode: () -> Unit = {},
    onDismissDarkMode: () -> Unit = {},
    preview: Boolean
) {
    FlashCardsTheme(isDarkTheme = isDarkTheme, preview = preview) {
        if (showDarkModeDialog.value) {
            DarkModeAlertDialog(
                isDarkTheme = isDarkTheme,
                darkModeOptionSelectedS = darkModeOptionSelectedS,
                onSelectDarkMode = onSelectDarkMode,
                onSaveDarkMode = onSaveDarkMode,
                onDismissDarkMode = onDismissDarkMode
            )
        }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FlashCardsAppBar(
                    isDarkTheme = isDarkTheme,
                    title = { Text("Settings") },
                    onBack = onBack
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    DarkModeField(isDarkTheme, darkModeDbS, onClickDarkModeField)
                }
            }
        )
    }
}

@Composable
fun DarkModeField(
    isDarkTheme: Boolean,
    darkModeDb: State<AppConfig?>?,
    onClickDarkModeField: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = { onClickDarkModeField() })
            .fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp)) {
            Column {
                Box(Modifier.padding(5.dp)) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) Grey900 else Color.Black)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_round_dark_mode_24),
                        contentDescription = "Dark mode icon",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .size(35.dp)
                            .padding(7.dp)
                    )
                }
            }
            Column(Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)) {
                Text("Dark Mode", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    darkModeDb?.value?.value ?: AppConfig.DARK_MODE_DEFAULT,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}