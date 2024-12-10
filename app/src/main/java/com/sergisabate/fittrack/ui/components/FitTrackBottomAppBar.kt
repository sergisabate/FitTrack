package com.sergisabate.fittrack.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.ui.navigation.currentRoute
import com.sergisabate.fittrack.ui.navigation.getBottomItems
import com.sergisabate.fittrack.ui.navigation.isCurrentRouteBottom

@Composable
fun FitTrackBottomAppBar(navController: NavHostController) {
    val view = LocalView.current
    val context = LocalContext.current as Activity
    val window = context.window

    if (isCurrentRouteBottom(navController)) {
        window.navigationBarColor = MaterialTheme.colorScheme.secondary.toArgb()
        BottomAppBar(
            modifier = Modifier.height(65.dp),
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
    } else if (!view.isInEditMode) {
        window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
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
