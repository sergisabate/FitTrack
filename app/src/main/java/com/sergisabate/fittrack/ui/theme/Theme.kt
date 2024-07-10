package com.sergisabate.fittrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sergisabate.fittrack.ui.screens.ThemeViewModel

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

val LocalThemeViewModel = compositionLocalOf<ThemeViewModel> { error("No ThemeViewModel provided") }

@Composable
fun FitTrackTheme(
    themeViewModel: ThemeViewModel,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme by themeViewModel.isDarkTheme.collectAsState()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme == true) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme == false -> LightColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = darkTheme == false
            insetsController.isAppearanceLightNavigationBars = darkTheme == false
        }
    }

    CompositionLocalProvider(LocalThemeViewModel provides themeViewModel) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
