package com.sergisabate.fittrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
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
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.repositories.UserPreferencesRepository
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModelFactory
import com.sergisabate.fittrack.ui.screens.AIToolsScreen
import com.sergisabate.fittrack.ui.screens.BluetoothScreen
import com.sergisabate.fittrack.ui.screens.EditProfileScreen
import com.sergisabate.fittrack.ui.screens.GenerateExerciseRoutineScreen
import com.sergisabate.fittrack.ui.screens.GenerateMenuScreen
import com.sergisabate.fittrack.ui.screens.HomeScreen
import com.sergisabate.fittrack.ui.screens.LanguageOption
import com.sergisabate.fittrack.ui.screens.LanguageScreen
import com.sergisabate.fittrack.ui.screens.ProfileScreen
import com.sergisabate.fittrack.ui.screens.ProgressScreen
import com.sergisabate.fittrack.ui.screens.SettingsScreen
import com.sergisabate.fittrack.ui.screens.TestScreen
import com.sergisabate.fittrack.ui.screens.ThemeScreen
import com.sergisabate.fittrack.ui.screens.ViewExerciseRoutineScreen
import com.sergisabate.fittrack.ui.screens.ViewMenuScreen
import com.sergisabate.fittrack.ui.screens.WeighScreen
import com.sergisabate.fittrack.ui.theme.ThemeOption

data class NavItem(
    val icon: ImageVector,
    val title: String,
    val description: String? = null,
    val route: String,
    val screen: @Composable (NavHostController?) -> Unit
)

@Composable
fun getTopItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Settings,
            title = stringResource(R.string.settings),
            route = "Settings",
            screen = { navController -> navController?.let { SettingsScreen(it) } }
        )
    )
}

@Composable
fun getBottomItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Home,
            title = stringResource(R.string.home),
            route = "Home",
            screen = { HomeScreen() }
        ),
        NavItem(
            icon = Icons.Default.Menu,
            title = stringResource(R.string.progress),
            route = "Progress",
            screen = { ProgressScreen() }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.ai_tools),
            route = "AITools",
            screen =  { navController -> navController?.let { AIToolsScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Face,
            title = stringResource(R.string.profile),
            route = "Profile",
            screen = { navController -> navController?.let { ProfileScreen(it) } }
        )
    )
}
@Composable
fun getSettingsItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Star,
            title = stringResource(R.string.theme),
            description = getThemeOptionName(),
            route = "Theme",
            screen = { ThemeScreen() }
        ),
        NavItem(
            icon = Icons.Default.Place,
            title = stringResource(R.string.language),
            description = getLanguageOptionName(),
            route = "Language",
            screen = { LanguageScreen() }
        ),
        NavItem(
            icon = Icons.Default.Create,
            title = stringResource(R.string.edit_profile),
            route = "EditProfile",
            screen = { EditProfileScreen() }
        ),
        NavItem(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.notifications),
            route = "Notifications",
            screen = { TestScreen() }
        ),
        NavItem(
            icon = Icons.Default.Info,
            title = stringResource(R.string.about),
            route = "About",
            screen = { TestScreen() }
        )
    )
}

@Composable
fun getMenuItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.menu_generator_title),
            route = "GenerateMenu",
            screen = { navController -> navController?.let { GenerateMenuScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.view_menu),
            route = "ViewMenu",
            screen = { ViewMenuScreen() }
        )
    )
}

@Composable
fun getExerciseRoutineItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.routine_generator_title),
            route = "GenerateExerciseRoutine",
            screen =  { navController -> navController?.let { GenerateExerciseRoutineScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.view_routine),
            route = "ViewExerciseRoutine",
            screen = { ViewExerciseRoutineScreen() }
        )
    )
}

@Composable
fun getBluetoothItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = "Bluetooth",
            route = "Bluetooth",
            screen = { navController -> navController?.let { BluetoothScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = "Pesar",
            route = "WeighScreen",
            screen = { navController -> navController?.let { WeighScreen(it) } }
        )
    )
}

@Composable
fun getThemeOptionName(): String {
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository.getInstance(context) }
    val preferencesViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(userPreferencesRepository)
    )
    val themeSetting by preferencesViewModel.getDarkTheme().collectAsState()
    val themeOption = enumValues<ThemeOption>().getOrElse(themeSetting) { ThemeOption.SystemDefault }
    return when (themeOption) {
        ThemeOption.DarkMode -> stringResource(R.string.dark_mode)
        ThemeOption.LightMode -> stringResource(R.string.light_mode)
        ThemeOption.SystemDefault -> stringResource(R.string.system_default)
    }
}

@Composable
fun getLanguageOptionName(): String {
    val context = LocalContext.current
    val test = context.resources.configuration.getLocales().get(0).language
    val option = LanguageOption.entries.firstOrNull { it.value == test } ?: LanguageOption.Spanish
    return option.displayName
}

