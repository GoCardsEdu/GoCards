package pl.gocards.ui.decks.kt.decks.dialogs

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.gocards.App
import pl.gocards.R
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.ui.common.addViewToRoot
import pl.gocards.ui.decks.kt.decks.model.EditDecksViewModel
import pl.gocards.ui.kt.theme.AppTheme
import pl.gocards.util.FirebaseAnalyticsHelper
import java.nio.file.Path

/**
 * @author Grzegorz Ziemski
 */
class DeckDialogs(
    private val decksViewModel: EditDecksViewModel,
    private val onSuccess: () -> Unit,
    private val activity: Activity,
    private val scope: CoroutineScope,
    val application: App
) {

    /**
     * D_C_06 Create a new deck
     */
    fun showCreateDeckDialog(currentFolder: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = CreateDeckDialogInput(
                onCreateDeck = { name ->
                    createDeck(currentFolder, name) {
                        onDismiss()
                        FirebaseAnalyticsHelper
                            .getInstance(application)
                            .createDeck()
                        onSuccess()
                    }
                },
                onDismiss = onDismiss
            )
            AppTheme(isDarkTheme = application.darkMode) {
                CreateDeckDialog(input)
            }
        }
    }

    /**
     * D_C_06 Create a new deck
     */
    private fun createDeck(
        currentFolder: Path,
        name: String,
        onSuccess: () -> Unit
    ) {
        decksViewModel.createDeck(
            currentFolder,
            name,
            onSuccess = {
                scope.launch {
                    showShortToastMessage(
                        String.format(
                            getString(R.string.decks_list_deck_create_dialog_toast_created),
                            name
                        )
                    )
                    onSuccess()
                }
            },
            onDeckExists = {
                scope.launch {
                    showShortToastMessage(
                        String.format(
                            getString(R.string.decks_list_deck_create_dialog_toast_exists),
                            name
                        )
                    )
                }
            }
        )
    }

    /**
     * D_U_07 Rename the deck
     */
    fun showRenameDeckDialog(deckDbPath: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = RenameDeckDialogInput(
                currentName = AppDeckDbUtil.getDeckName(deckDbPath),
                onRenameDeck = { newName ->
                    renameDeck(deckDbPath, newName) {
                        onDismiss()
                        onSuccess()
                    }
                },
                onDismiss = {
                    onDismiss()
                }
            )

            AppTheme {
                RenameDeckDialog(input)
            }
        }
    }

    /**
     * D_U_07 Rename the deck
     */
    private fun renameDeck(
        currentDeckPath: Path,
        name: String,
        onSuccess: () -> Unit
    ) {
        decksViewModel.renameDeck(
            currentDeckPath,
            name,
            onSuccess = {
                scope.launch {
                    showShortToastMessage(
                        String.format(
                            getString(R.string.decks_list_deck_rename_dialog_toast_renamed),
                            name
                        )
                    )
                    onSuccess()
                }
            },
            onDeckExists = {
                showShortToastMessage(
                    String.format(
                        getString(R.string.decks_list_deck_rename_dialog_toast_exists),
                        name
                    )
                )
            }
        )
    }

    /**
     * D_R_08 Delete the deck
     */
    fun showDeleteDeckDialog(deckDbPath: Path) {
        addViewToRoot(activity, scope) { onDismiss ->
            val input = DeleteDeckDialogInput(
                onDeleteDeck = {
                    deleteDeck(deckDbPath) {
                        onDismiss()
                        onSuccess()
                    }
                },
                onDismiss = {
                    onDismiss()
                }
            )
            AppTheme {
                DeleteDeckDialog(input)
            }
        }
    }

    /**
     * D_R_08 Delete the deck
     */
    private fun deleteDeck(
        deckDbPath: Path,
        onSuccess: () -> Unit
    ) {
        decksViewModel.deleteDeck(deckDbPath, onSuccess = {
            scope.launch {
                showShortToastMessage(R.string.decks_list_deck_delete_dialog_toast)
                onSuccess()
            }
        })
    }

    /* -----------------------------------------------------------------------------------------
     * Helpers
     * ----------------------------------------------------------------------------------------- */

    @Suppress("SameParameterValue", "SameParameterValue")
    @UiThread
    private fun showShortToastMessage(@StringRes resId: Int) {
        Toast.makeText(
            activity,
            activity.getString(resId),
            Toast.LENGTH_SHORT
        ).show()
    }

    @UiThread
    private fun showShortToastMessage(text: String) {
        Toast.makeText(
            activity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getString(@StringRes id: Int): String {
        return application.resources.getString(id)
    }
}