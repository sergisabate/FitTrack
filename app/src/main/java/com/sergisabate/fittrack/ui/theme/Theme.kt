package com.sergisabate.fittrack.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel

val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = Pink,
    onSecondary = White,
    background = Black,
    surface = Gray
)

val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = Pink,
    onSecondary = White,
    background = White,
    surface = Gray
)

enum class ThemeOption(val value: Int) {
    DarkMode(0),
    LightMode(1),
    SystemDefault(2)
}

@Composable
fun FitTrackTheme(
    content: @Composable () -> Unit
) {
    val themeOption = getThemeOption()
    val colorScheme = getColorScheme(themeOption, isSystemInDarkTheme())

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = colorScheme == LightColorScheme
            insetsController.isAppearanceLightNavigationBars = colorScheme == LightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun getThemeOption(): Int {
    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val themeOption by preferencesViewModel.getDarkTheme().collectAsState()
    return themeOption
}

private fun getColorScheme(themeOption: Int, isSystemInDarkTheme: Boolean): ColorScheme {
    return when (ThemeOption.entries.firstOrNull { it.value == themeOption } ?: ThemeOption.SystemDefault) {
        ThemeOption.SystemDefault -> {
            if (isSystemInDarkTheme) DarkColorScheme
            else LightColorScheme
        }
        ThemeOption.LightMode -> LightColorScheme
        ThemeOption.DarkMode -> DarkColorScheme
    }
}

