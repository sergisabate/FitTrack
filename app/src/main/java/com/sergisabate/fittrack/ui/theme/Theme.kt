package com.sergisabate.fittrack.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.data.UserPreferencesRepository
import com.sergisabate.fittrack.ui.viewmodels.ThemeViewModel
import com.sergisabate.fittrack.ui.viewmodels.ThemeViewModelFactory

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    secondary = Pink,
    onSecondary = Color.White,
    background = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = Pink,
    onSecondary = Color.White,
    background = Color.White
)

enum class ThemeOption(val value: Int) {
    DarkMode(0),
    LightMode(1),
    SystemDefault(2)
}

@Composable
fun FitTrackTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(userPreferencesRepository)
    )

    val themeOption by themeViewModel.getPreferences().collectAsState(initial = ThemeOption.SystemDefault.value)

    val colorScheme = getColorScheme(context, themeOption, dynamicColor)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = themeOption == ThemeOption.LightMode.value
            insetsController.isAppearanceLightNavigationBars = themeOption == ThemeOption.LightMode.value
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
private fun getColorScheme(context: Context, themeOption: Int, dynamicColor: Boolean): ColorScheme {
    return when (ThemeOption.entries.firstOrNull { it.value == themeOption } ?: ThemeOption.SystemDefault) {
        ThemeOption.SystemDefault -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (themeOption == ThemeOption.DarkMode.value) dynamicDarkColorScheme(context)
                else dynamicLightColorScheme(context)
            } else {
                DarkColorScheme
            }
        }
        ThemeOption.LightMode -> LightColorScheme
        ThemeOption.DarkMode -> DarkColorScheme
    }
}

