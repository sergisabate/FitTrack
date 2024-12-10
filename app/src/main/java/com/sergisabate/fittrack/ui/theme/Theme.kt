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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModelFactory

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = Pink,
    onSecondary = White,
    background = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Black,
    secondary = Pink,
    onSecondary = White,
    background = White
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
            window.statusBarColor = colorScheme.background.toArgb()

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
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val preferencesViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(userPreferencesRepository)
    )
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

