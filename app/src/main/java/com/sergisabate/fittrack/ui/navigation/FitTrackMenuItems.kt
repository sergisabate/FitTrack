package com.sergisabate.fittrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.ui.screens.HomeScreen
import com.sergisabate.fittrack.ui.screens.StatsScreen
import com.sergisabate.fittrack.ui.screens.TestScreen

data class BottomNavItem(
    val icon: ImageVector,
    val name: String,
    val screen: @Composable () -> Unit
)

@Composable
fun getMenuItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            icon = Icons.Default.Home,
            name = stringResource(R.string.home),
            screen = { HomeScreen() }
        ),
        BottomNavItem(
            icon = Icons.Default.Menu,
            name = stringResource(R.string.stats),
            screen = { StatsScreen() }
        ),
        BottomNavItem(
            icon = Icons.Default.Face,
            name = stringResource(R.string.test),
            screen = { TestScreen() }
        )
    )
}