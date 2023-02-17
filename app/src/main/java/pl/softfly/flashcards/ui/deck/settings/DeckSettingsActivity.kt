package pl.softfly.flashcards.ui.deck.settings


import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.softfly.flashcards.R
import pl.softfly.flashcards.db.AppDatabaseUtil
import pl.softfly.flashcards.db.DeckDatabaseUtil
import pl.softfly.flashcards.entity.app.AppConfig
import pl.softfly.flashcards.ui.card.NewCardActivity
import pl.softfly.flashcards.ui.kt.theme.FlashCardsAppBar
import pl.softfly.flashcards.ui.kt.theme.FlashCardsTheme
import pl.softfly.flashcards.ui.kt.theme.Grey900

@Preview(showBackground = true)
@Composable
fun PreviewDeckSettings() {
    DeckSettingsScaffold(isDarkTheme = false,
        autoSync = remember { mutableStateOf(true) },
        maxForgottenCards = remember { mutableStateOf("10") },
        leftEdgeBar = remember { mutableStateOf(AppConfig.LEFT_EDGE_BAR_DEFAULT) },
        rightEdgeBar = remember { mutableStateOf(AppConfig.RIGHT_EDGE_BAR_DEFAULT) },
        autoSyncDb = false,
        fileNameDb = "Example File Name",
        AutoSyncDialog_IsShown = remember { mutableStateOf(false) },
        LimitForgottenCardsDialog_IsShown = remember { mutableStateOf(false) },
        LeftEdgeBarDialog_IsShown = remember { mutableStateOf(false) },
        RightEdgeBarDialog_IsShown = remember { mutableStateOf(false) },
        preview = true
    )
}

class DeckSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            intent.getStringExtra(NewCardActivity.DECK_DB_PATH)?.let {
                InitViewModelDeckSettings(
                    onBack = { super.finish() }, dbPath = it
                )
            }
        }
    }
}

/**
 * It is separate to don't create and compose the ViewModel all the time.
 */
@Composable
fun InitViewModelDeckSettings(
    onBack: () -> Unit = {}, dbPath: String
) {
    InitDeckSettings(
        onBack = onBack,
        viewModel = DeckSettingsViewModel(
            AppDatabaseUtil.getInstance(LocalContext.current).database,
            DeckDatabaseUtil.getInstance(LocalContext.current).getDatabase(dbPath),
            LocalContext.current.applicationContext as Application
        )
    )
}

@Composable
fun InitDeckSettings(onBack: () -> Unit = {}, viewModel: DeckSettingsViewModel) {
    val isShownAutoSyncDialog = remember { mutableStateOf(false) }
    val isShownLimitLearningCardsDialog = remember { mutableStateOf(false) }
    val isShownLeftEdgeBarDialog = remember { mutableStateOf(false) }
    val isShownRightEdgeBarDialog = remember { mutableStateOf(false) }
    DeckSettingsScaffold(
        // Current values
        autoSync = viewModel.autoSync.observeAsState(initial = false),
        maxForgottenCards = viewModel.maxForgottenCards.observeAsState(initial = ""),
        leftEdgeBar = viewModel.leftEdgeBar.observeAsState(initial = ""),
        rightEdgeBar = viewModel.rightEdgeBar.observeAsState(initial = ""),
        // DB values
        autoSyncDb = viewModel.fileSyncedDb?.isAutoSync ?: false,
        fileNameDb = viewModel.fileSyncedDb?.displayName ?: "",
        // Fields
        AutoSyncField_OnClick = { isShownAutoSyncDialog.value = true },
        LimitForgottenCardsField_OnClick = { isShownLimitLearningCardsDialog.value = true },
        LeftEdgeBarField_OnClick = { isShownLeftEdgeBarDialog.value = true },
        RightEdgeBarField_OnClick = { isShownRightEdgeBarDialog.value = true },

        // Dialogs
        AutoSyncDialog_IsShown = isShownAutoSyncDialog,
        AutoSyncDialog_OnSelectOption = {
            viewModel.AutoSync().set(it as Boolean)
        },
        AutoSyncDialog_OnSave = {
            isShownAutoSyncDialog.value = false
            viewModel.AutoSync().commit()
        },
        AutoSyncDialog_OnDismiss = {
            isShownAutoSyncDialog.value = false
            viewModel.AutoSync().reset()
        },

        LimitForgottenCardsDialog_IsShown = isShownLimitLearningCardsDialog,
        LimitForgottenCardsDialog_OnValueChange = {
            viewModel.MaxForgottenCards().set(it)
        },
        LimitForgottenCardsDialog_OnSave = {
            isShownLimitLearningCardsDialog.value = false
            viewModel.MaxForgottenCards().commit()
        },
        LimitForgottenCardsDialog_OnDismiss = {
            isShownLimitLearningCardsDialog.value = false
            viewModel.MaxForgottenCards().reset()
        },
        LeftEdgeBarDialog_OnSelectRadio = {
            viewModel.ShowLeftEdgeBar().set(it as String)
        },
        LeftEdgeBarDialog_IsShown = isShownLeftEdgeBarDialog,
        LeftEdgeBarDialog_OnSave = {
            isShownLeftEdgeBarDialog.value = false
            viewModel.ShowLeftEdgeBar().commit()
        },
        LeftEdgeBarDialog_OnDismiss = {
            isShownLeftEdgeBarDialog.value = false
            viewModel.ShowLeftEdgeBar().reset()
        },

        RightEdgeBarDialog_OnSelectRadio = {
            viewModel.ShowRightEdgeBar().set(it as String)
        },
        RightEdgeBarDialog_IsShown = isShownRightEdgeBarDialog,
        RightEdgeBarDialog_OnSave = {
            isShownRightEdgeBarDialog.value = false
            viewModel.ShowRightEdgeBar().commit()
        },
        RightEdgeBarDialog_OnDismiss = {
            isShownRightEdgeBarDialog.value = false
            viewModel.ShowRightEdgeBar().reset()
        },


        onBack = onBack,
        isDarkTheme = isSystemInDarkTheme(),
        preview = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckSettingsScaffold(
    // Current values
    autoSync: State<Boolean>,
    maxForgottenCards: State<String>,
    leftEdgeBar: State<String>,
    rightEdgeBar: State<String>,

    // DB values
    autoSyncDb: Boolean,
    fileNameDb: String,

    // Fields
    AutoSyncField_OnClick: () -> Unit = {},
    LimitForgottenCardsField_OnClick: () -> Unit = {},
    LeftEdgeBarField_OnClick: () -> Unit = {},
    RightEdgeBarField_OnClick: () -> Unit = {},

    LimitForgottenCardsDialog_IsShown: State<Boolean>,
    LimitForgottenCardsDialog_OnValueChange: (limitLearningCards: Any) -> Unit = {},
    LimitForgottenCardsDialog_OnSave: () -> Unit = {},
    LimitForgottenCardsDialog_OnDismiss: () -> Unit = {},

    AutoSyncDialog_IsShown: State<Boolean>,
    AutoSyncDialog_OnSelectOption: (autoSync: Any) -> Unit = {},
    AutoSyncDialog_OnSave: () -> Unit = {},
    AutoSyncDialog_OnDismiss: () -> Unit = {},

    LeftEdgeBarDialog_IsShown: State<Boolean>,
    LeftEdgeBarDialog_OnSelectRadio: (v: Any) -> Unit = {},
    LeftEdgeBarDialog_OnSave: () -> Unit = {},
    LeftEdgeBarDialog_OnDismiss: () -> Unit = {},

    RightEdgeBarDialog_IsShown: State<Boolean>,
    RightEdgeBarDialog_OnSelectRadio: (v: Any) -> Unit = {},
    RightEdgeBarDialog_OnSave: () -> Unit = {},
    RightEdgeBarDialog_OnDismiss: () -> Unit = {},

    onBack: () -> Unit = {},
    isDarkTheme: Boolean,
    preview: Boolean
) {
    FlashCardsTheme(isDarkTheme = isDarkTheme, preview = preview) {
        if (AutoSyncDialog_IsShown.value) {
            FileAutoSyncAlertDialog(
                isDarkTheme = isDarkTheme,
                autoSync = autoSync,
                autoSyncDb = autoSyncDb,
                fileNameDb = fileNameDb,
                onSelectRadio = AutoSyncDialog_OnSelectOption,
                onSave = AutoSyncDialog_OnSave,
                onDismiss = AutoSyncDialog_OnDismiss,
            )
        }
        if (LimitForgottenCardsDialog_IsShown.value) {
            LimitForgottenCardsDialog(
                maxForgottenCards = maxForgottenCards,
                onValueChange = LimitForgottenCardsDialog_OnValueChange,
                onSave = LimitForgottenCardsDialog_OnSave,
                onDismiss = LimitForgottenCardsDialog_OnDismiss,
            )
        }
        if (LeftEdgeBarDialog_IsShown.value) {
            EdgeBarAlertDialog(
                isDarkTheme = isDarkTheme,
                title = "Show left edge bar",
                edgeBar = leftEdgeBar,
                onSelectRadio = LeftEdgeBarDialog_OnSelectRadio,
                onSave = LeftEdgeBarDialog_OnSave,
                onDismiss = LeftEdgeBarDialog_OnDismiss,
            )
        }
        if (RightEdgeBarDialog_IsShown.value) {
            EdgeBarAlertDialog(
                isDarkTheme = isDarkTheme,
                title = "Show right edge bar",
                edgeBar = rightEdgeBar,
                onSelectRadio = RightEdgeBarDialog_OnSelectRadio,
                onSave = RightEdgeBarDialog_OnSave,
                onDismiss = RightEdgeBarDialog_OnDismiss,
            )
        }
        Scaffold(
            topBar = {
                FlashCardsAppBar(
                    isDarkTheme = isDarkTheme,
                    title = { Text("Deck Settings") },
                    onBack = onBack
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    SettingField(
                        isDarkTheme = isDarkTheme,
                        title = "Auto-sync with the file",
                        icon = R.drawable.ic_sharp_sync_24,
                        value = if (autoSyncDb) fileNameDb else "Off",
                        onClick = AutoSyncField_OnClick
                    )
                    SettingField(
                        isDarkTheme = isDarkTheme,
                        title = "Limit forgotten cards",
                        icon = R.drawable.ic_round_123_24,
                        value = if (maxForgottenCards.value.isEmpty() || maxForgottenCards.value == "0") "Off" else maxForgottenCards.value,
                        onClick = LimitForgottenCardsField_OnClick
                    )
                    SettingField(
                        isDarkTheme = isDarkTheme,
                        title = "Show left edge bar",
                        icon = R.drawable.ic_round_align_horizontal_left_24,
                        value = getShowEdgeBarLabel(leftEdgeBar.value),
                        onClick = LeftEdgeBarField_OnClick
                    )
                    SettingField(
                        isDarkTheme = isDarkTheme,
                        title = "Show right edge bar",
                        icon = R.drawable.ic_round_align_horizontal_right_24,
                        value = getShowEdgeBarLabel(rightEdgeBar.value),
                        onClick = RightEdgeBarField_OnClick
                    )
                }
            }
        )
    }
}

@Composable
private fun SettingField(
    isDarkTheme: Boolean, title: String, icon: Int, value: String, onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = { onClick() })
            .fillMaxWidth(),
    ) {
        Row(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)) {
            Column {
                Box(Modifier.padding(5.dp)) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDarkTheme) Grey900
                                else MaterialTheme.colorScheme.primary
                            )
                    )
                    Image(
                        painter = painterResource(icon),
                        contentDescription = title,
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier
                            .size(35.dp)
                            .padding(7.dp)
                    )
                }
            }
            Column(Modifier.padding(15.dp, 3.dp, 0.dp, 0.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(text = value, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}