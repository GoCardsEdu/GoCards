package pl.gocards.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Composable
fun BarColorsTheme(
    isDarkTheme: Boolean,
    colorScheme: ColorScheme? = null,
    color: Color = colorScheme?.surfaceColorAtElevation(2.dp) ?: Color.Unspecified,
    color2: Color = colorScheme?.surfaceColorAtElevation(3.dp) ?: Color.Unspecified
) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color2.toArgb()

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
    }
}