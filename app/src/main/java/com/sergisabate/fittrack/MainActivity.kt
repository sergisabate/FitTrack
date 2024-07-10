package com.sergisabate.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sergisabate.fittrack.ui.screens.MainScreen
import com.sergisabate.fittrack.ui.screens.ThemeViewModel
import com.sergisabate.fittrack.ui.theme.FitTrackTheme

class MainActivity : ComponentActivity() {
    private val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FitTrackTheme(themeViewModel) {
                MainScreen()
            }
        }
    }
}
