package com.sergisabate.fittrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.ui.screens.AIScreen
import com.sergisabate.fittrack.ui.screens.AboutScreen
import com.sergisabate.fittrack.ui.screens.DeleteDataScreen
import com.sergisabate.fittrack.ui.screens.EditProfileScreen
import com.sergisabate.fittrack.ui.screens.GenerateExerciseRoutineScreen
import com.sergisabate.fittrack.ui.screens.GenerateMenuScreen
import com.sergisabate.fittrack.ui.screens.LanguageOption
import com.sergisabate.fittrack.ui.screens.LanguageScreen
import com.sergisabate.fittrack.ui.screens.LifestyleScreen
import com.sergisabate.fittrack.ui.screens.PairScaleScreen
import com.sergisabate.fittrack.ui.screens.PlanScreen
import com.sergisabate.fittrack.ui.screens.ProfileScreen
import com.sergisabate.fittrack.ui.screens.SettingsScreen
import com.sergisabate.fittrack.ui.screens.ThemeScreen
import com.sergisabate.fittrack.ui.screens.ViewExerciseRoutineScreen
import com.sergisabate.fittrack.ui.screens.ViewMenuScreen
import com.sergisabate.fittrack.ui.screens.WeighInScreen
import com.sergisabate.fittrack.ui.screens.WeightManagerScreen
import com.sergisabate.fittrack.ui.screens.WeightScreen
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
            icon = Icons.AutoMirrored.Filled.ShowChart,
            title = stringResource(R.string.weight),
            route = "Weight",
            screen = { navController -> navController?.let { WeightScreen(it) } }
        ),
        NavItem(
            icon = Icons.AutoMirrored.Filled.List,
            title = stringResource(R.string.plan),
            route = "Plan",
            screen = { navController -> navController?.let { PlanScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.AutoAwesome,
            title = stringResource(R.string.ai),
            route = "AI",
            screen = { navController -> navController?.let { AIScreen(it) } }
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
            icon = Icons.Default.Info,
            title = stringResource(R.string.about),
            route = "About",
            screen = { AboutScreen() }
        ),
        NavItem(
            icon = Icons.Default.Delete,
            title = stringResource(R.string.delete_data),
            route = "DeleteData",
            screen = { DeleteDataScreen() }
        )
    )
}

@Composable
fun getAIItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.menu_generator_title),
            route = "GenerateMenu",
            screen = { navController -> navController?.let { GenerateMenuScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.routine_generator_title),
            route = "GenerateExerciseRoutine",
            screen =  { navController -> navController?.let { GenerateExerciseRoutineScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.view_menu),
            route = "ViewMenu",
            screen = { ViewMenuScreen() }
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
fun getWeightItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.pair_scale),
            route = "PairScale",
            screen = { navController -> navController?.let { PairScaleScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.weigh_in),
            route = "WeighIn",
            screen = { navController -> navController?.let { WeighInScreen(it) } }
        ),
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.weight_manager),
            route = "WeightManager",
            screen = { WeightManagerScreen() }
        ),
    )
}

@Composable
fun getPlanItems(): List<NavItem> {
    return listOf(
        NavItem(
            icon = Icons.Default.Build,
            title = stringResource(R.string.lifestyle),
            route = "Lifestyle",
            screen = { LifestyleScreen() }
        )
    )
}

@Composable
fun getThemeOptionName(): String {
    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
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
    val language = LocalConfiguration.current.locales.get(0).language
    val option = LanguageOption.entries.firstOrNull { it.value == language } ?: LanguageOption.Spanish
    return option.displayName
}

