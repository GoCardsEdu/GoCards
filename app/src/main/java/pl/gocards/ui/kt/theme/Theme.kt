package pl.gocards.ui.kt.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    //primary = Color(0xff76d1ff),
    //surface = Color(0xff191c1e),
    //secondaryContainer = Color(0xff374955),
    //background = Color(0xff191c1e)

)
val LightColorScheme = lightColorScheme(
    primary = Color(0xff00668b),
    surface = Color(0xfffcfcff),
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean,
    dynamicColor: Boolean = true,
    preview: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )

    if (!preview) BarColorsTheme(isDarkTheme, colorScheme)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    isDarkTheme: Boolean,
    title: @Composable () -> Unit,
    onBack: () -> Unit,
    navigationIcon: @Composable (() -> Unit) = {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, "Back")
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = title,
        //modifier = Modifier.shadow(elevation = 3.dp),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            titleContentColor = if (isDarkTheme)Color.White else Color.Black,
        )
    )
}

@Composable
fun BarColorsTheme(isDarkTheme: Boolean, colorScheme: ColorScheme) {
    val color = colorScheme.surfaceColorAtElevation(2.dp)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = color.toArgb()
        window.navigationBarColor = color.toArgb()

        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
    }
}