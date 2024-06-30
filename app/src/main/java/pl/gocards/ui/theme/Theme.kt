package pl.gocards.ui.theme

import android.R
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource

private val DarkColorScheme = darkColorScheme(
    //primary = Color(0xff76d1ff),
    //surface = Color(0xff191c1e),
    //secondaryContainer = Color(0xff374955),
    //background = Color(0xff191c1e)

)
private val LightColorScheme = lightColorScheme(
    //primary = Color(0xff00668b),
    //surface = Color(0xfffcfcff),DarkColorScheme
)

@Immutable
data class ExtendedColors(
    val behindWindowBackground: Color,
    val cardBorder: Color,

    // Card list
    val colorListDivider: Color,
    val colorItemTextColor: Color,
    val colorItemSearch: Color,
    val colorItemDisabledCard: Color,
    val colorItemForgottenCard: Color,
    val colorItemRememberedCards: Color,
    val colorItemSelected: Color,
    val colorItemActive: Color,

    //Show last synced
    val colorItemRecentlyAddedDeckCard: Color,
    val colorItemRecentlyAddedFileCard: Color,
    val colorItemRecentlyUpdatedDeckCard: Color,
    val colorItemRecentlyUpdatedFileCard: Color,
)

private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        behindWindowBackground = Color.Unspecified,
        cardBorder = Color.Unspecified,

        // Card list
        colorListDivider = Color.Unspecified,
        colorItemTextColor = Color.Unspecified,
        colorItemSearch = Color.Unspecified,
        colorItemDisabledCard = Color.Unspecified,
        colorItemForgottenCard = Color.Unspecified,
        colorItemRememberedCards = Color.Unspecified,
        colorItemSelected = Color.Unspecified,
        colorItemActive = Color.Unspecified,

        //Show last synced
        colorItemRecentlyAddedDeckCard = Color.Unspecified,
        colorItemRecentlyAddedFileCard = Color.Unspecified,
        colorItemRecentlyUpdatedDeckCard = Color.Unspecified,
        colorItemRecentlyUpdatedFileCard = Color.Unspecified
    )
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean? = isSystemInDarkTheme(),
    preview: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val isSdk31 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val isDarkTheme = isDarkTheme ?: isSystemInDarkTheme()

    val extendedColors = when {
        isSdk31 && isDarkTheme -> ExtendedColors(
            behindWindowBackground = colorResource(R.color.system_accent2_900),
            cardBorder = colorResource(R.color.system_accent2_800),

            // Card list
            colorListDivider = colorResource(R.color.system_neutral2_800),
            colorItemTextColor = colorResource(R.color.white),
            colorItemSearch = colorResource(R.color.system_accent3_500),
            colorItemDisabledCard = Grey800,
            colorItemForgottenCard = Orange900,
            colorItemRememberedCards = Green800,
            colorItemSelected = colorResource(R.color.system_accent2_700),
            colorItemActive = colorResource(R.color.system_accent3_800),

            //Show last synced
            colorItemRecentlyAddedDeckCard = Green700,
            colorItemRecentlyAddedFileCard = Green400,
            colorItemRecentlyUpdatedDeckCard = Orange900,
            colorItemRecentlyUpdatedFileCard = Orange500
        )
        isSdk31 && !isDarkTheme -> ExtendedColors(
            behindWindowBackground = colorResource(R.color.system_accent2_200),
            cardBorder = colorResource(R.color.system_accent2_300),

            // Card list
            colorListDivider = colorResource(R.color.system_neutral2_200),
            colorItemTextColor = colorResource(R.color.white),
            colorItemSearch = colorResource(R.color.system_accent3_500),
            colorItemDisabledCard = Grey800,
            colorItemForgottenCard = Orange900,
            colorItemRememberedCards = Green800,
            colorItemSelected = colorResource(R.color.system_accent2_200),
            colorItemActive = colorResource(R.color.system_accent3_200),

            //Show last synced
            colorItemRecentlyAddedDeckCard = Green700,
            colorItemRecentlyAddedFileCard = Green400,
            colorItemRecentlyUpdatedDeckCard = Orange900,
            colorItemRecentlyUpdatedFileCard = Orange500
        )
        isDarkTheme -> ExtendedColors(
            behindWindowBackground = Grey800,
            cardBorder = Grey800,

            // Card list
            colorListDivider = Grey800,
            colorItemTextColor = colorResource(R.color.white),
            colorItemSearch = Purple500,
            colorItemDisabledCard = Grey800,
            colorItemForgottenCard = Orange900,
            colorItemRememberedCards = Green800,
            colorItemSelected = Purple900,
            colorItemActive = Purple800,

            //Show last synced
            colorItemRecentlyAddedDeckCard = Green700,
            colorItemRecentlyAddedFileCard = Green400,
            colorItemRecentlyUpdatedDeckCard = Orange900,
            colorItemRecentlyUpdatedFileCard = Orange500
        )
        else -> ExtendedColors(
            behindWindowBackground = Color(0xffb5cad7),
            cardBorder = Color(0xff9aaebb),

            // Card list
            colorListDivider = Grey300,
            colorItemTextColor = colorResource(R.color.black),
            colorItemSearch = Indigo300,
            colorItemDisabledCard = Grey800,
            colorItemForgottenCard = Orange900,
            colorItemRememberedCards = Green800,
            colorItemSelected = Indigo100,
            colorItemActive = Indigo200,

            //Show last synced
            colorItemRecentlyAddedDeckCard = Green700,
            colorItemRecentlyAddedFileCard = Green400,
            colorItemRecentlyUpdatedDeckCard = Orange900,
            colorItemRecentlyUpdatedFileCard = Orange500
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        val colorScheme = when {
            dynamicColor && isSdk31 && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
            dynamicColor && isSdk31 && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
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
}

// Use with eg. ExtendedTheme.colors.tertiary
object ExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}