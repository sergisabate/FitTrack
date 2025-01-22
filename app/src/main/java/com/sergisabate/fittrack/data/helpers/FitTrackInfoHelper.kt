package com.sergisabate.fittrack.data.helpers

import android.content.pm.PackageInfo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.ui.theme.ThemeOption

@Composable
fun getCurrentTheme(): ThemeOption {
    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val themeSetting by preferencesViewModel.getDarkTheme().collectAsState()
    val selectedTheme = enumValues<ThemeOption>().getOrElse(themeSetting) { ThemeOption.SystemDefault }

    return when (selectedTheme) {
        ThemeOption.DarkMode, ThemeOption.LightMode -> selectedTheme
        ThemeOption.SystemDefault -> if (isSystemInDarkTheme()) ThemeOption.DarkMode else ThemeOption.LightMode
    }
}

@Composable
fun getCurrentVersion(): String {
    val context = LocalContext.current
    val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val version = pInfo.versionName
    return version ?: "en desarrollo"
}
