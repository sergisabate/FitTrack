package com.sergisabate.fittrack.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.ui.navigation.currentRoute
import com.sergisabate.fittrack.ui.navigation.getBottomItems
import com.sergisabate.fittrack.ui.navigation.isCurrentRouteBottom

@Composable
fun FitTrackBottomAppBar(navController: NavHostController) {
    val isCurrentRouteBottom = isCurrentRouteBottom(navController)
    val currentRoute = currentRoute(navController)

    if (isCurrentRouteBottom) {
        BottomAppBar(
            modifier = Modifier
                .height(65.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()),
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            getBottomItems().forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo("Weight")
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { BottomNavItemContent(item.icon, item.title) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        indicatorColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    }
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
