package com.sergisabate.fittrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.ui.screens.ApplicationThemeScreen
import com.sergisabate.fittrack.ui.screens.HomeScreen
import com.sergisabate.fittrack.ui.screens.SettingsScreen
import com.sergisabate.fittrack.ui.screens.StatsScreen
import com.sergisabate.fittrack.ui.screens.TestScreen

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
            description = "Dark Mode",
            screen = { ApplicationThemeScreen() }
        ),
        NavItem(
            icon = Icons.Default.Info,
            name = "About",
            screen = { TestScreen() }
        )
    )
}
