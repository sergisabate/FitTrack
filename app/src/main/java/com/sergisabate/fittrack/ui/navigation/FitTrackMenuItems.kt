package com.sergisabate.fittrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.UserPreferencesRepository
import com.sergisabate.fittrack.ui.screens.ApplicationThemeScreen
import com.sergisabate.fittrack.ui.screens.HomeScreen
import com.sergisabate.fittrack.ui.screens.SettingsScreen
import com.sergisabate.fittrack.ui.screens.StatsScreen
import com.sergisabate.fittrack.ui.screens.TestScreen
import com.sergisabate.fittrack.ui.theme.ThemeOption
import com.sergisabate.fittrack.ui.viewmodels.ThemeViewModel
import com.sergisabate.fittrack.ui.viewmodels.ThemeViewModelFactory

data class NavItem(
    val icon: ImageVector,
    val name: String,
    val description: String? = null,
    val screen: @Composable () -> Unit
)

@Composable
fun getTopItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Settings,
            name = stringResource(R.string.settings),
            screen = { SettingsScreen() }
        )
    )
}

@Composable
fun getBottomItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Home,
            name = stringResource(R.string.home),
            screen = { HomeScreen() }
        ),
        NavItem(
            icon = Icons.Default.Menu,
            name = stringResource(R.string.stats),
            screen = { StatsScreen() }
        ),
        NavItem(
            icon = Icons.Default.Face,
            name = stringResource(R.string.test),
            screen = { TestScreen() }
        )
    )
}
@Composable
fun getSettingsItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Star,
            name = "Application Theme",
            description = getThemeOptionName(),
            screen = { ApplicationThemeScreen() }
        ),
        NavItem(
            icon = Icons.Default.Info,
            name = "About",
            screen = { TestScreen() }
        )
    )
}

@Composable
fun getThemeOptionName(): String {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ThemeViewModelFactory(userPreferencesRepository)
    )
    val themeSetting by themeViewModel.getPreferences().collectAsState()
    val themeOption = enumValues<ThemeOption>().getOrElse(themeSetting) { ThemeOption.SystemDefault }
    return themeOption.name
}

