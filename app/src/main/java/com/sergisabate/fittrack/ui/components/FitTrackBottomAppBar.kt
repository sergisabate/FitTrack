package com.sergisabate.fittrack.ui.components

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.ui.navigation.currentRoute
import com.sergisabate.fittrack.ui.navigation.getBottomItems
import com.sergisabate.fittrack.ui.navigation.isCurrentRouteBottom
import com.sergisabate.fittrack.ui.theme.LightColorScheme

@Composable
fun FitTrackBottomAppBar(navController: NavHostController) {
    val isCurrentRouteBottom = isCurrentRouteBottom(navController)

    if (isCurrentRouteBottom) {
        BottomAppBar(
            modifier = Modifier
                .height(65.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            getBottomItems().forEach { item ->
                val isSelected = currentRoute(navController) == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) navController.navigate(item.route) },
                    icon = { BottomNavItemContent(item.icon, item.title) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        indicatorColor = MaterialTheme.colorScheme.background,
                    )
                )
            }
        }
    }

    AdjustNavigationBarColor(isCurrentRouteBottom)
}

@Composable
private fun BottomNavItemContent(icon: ImageVector, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun AdjustNavigationBarColor(
    isCurrentRouteBottom: Boolean
) {
    val view = LocalView.current
    val context = LocalContext.current as Activity
    val window = context.window
    val insetsController = WindowCompat.getInsetsController(window, view)

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isOldVersion = Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM
    val isVanillaIceCream = Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM

    if (isCurrentRouteBottom) {
        if (isOldVersion) {
            window.navigationBarColor = if (isPortrait) {
                MaterialTheme.colorScheme.secondary.toArgb()
            } else {
                MaterialTheme.colorScheme.background.toArgb()
            }
        }

        insetsController.isAppearanceLightNavigationBars = if (isVanillaIceCream && isLandscape) {
            MaterialTheme.colorScheme == LightColorScheme
        } else {
            false
        }
    } else if (!view.isInEditMode) {
        if (isOldVersion) {
            window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
        }

        insetsController.isAppearanceLightNavigationBars = MaterialTheme.colorScheme == LightColorScheme
    }
}
