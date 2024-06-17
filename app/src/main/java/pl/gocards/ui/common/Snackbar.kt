package pl.gocards.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun showSnackbar(
    message: String,
    actionLabel: String,
    @SuppressWarnings("unused")
    onAction: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    scope.launch {
        showSnackbar(
            message,
            actionLabel,
            onAction,
            snackbarHostState,
        )
    }
}

suspend fun showSnackbar(
    message: String,
    actionLabel: String,
    @SuppressWarnings("unused")
    onAction: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val result = snackbarHostState.showSnackbar(
        message,
        actionLabel,
        duration = SnackbarDuration.Long
    )

    when (result) {
        SnackbarResult.ActionPerformed -> {
            onAction()
        }

        SnackbarResult.Dismissed -> {
            /* Handle snackbar dismissed */
        }
    }
}