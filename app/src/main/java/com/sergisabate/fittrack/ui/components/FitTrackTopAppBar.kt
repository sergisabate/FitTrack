package com.sergisabate.fittrack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sergisabate.fittrack.ui.navigation.currentRoute
import com.sergisabate.fittrack.ui.navigation.getTopItems
import com.sergisabate.fittrack.ui.navigation.isCurrentRouteBottom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackTopAppBar(navController: NavHostController) {
    val name = currentRoute(navController)
    if (name != null) {
        TopAppBar(
            title = { Text(name) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            actions = { GetActions(navController) },
            navigationIcon = { GetNavs(navController) }
        )
    }
}

@Composable
private fun GetActions(navController: NavHostController) {
    if (isCurrentRouteBottom(navController)) {
        getTopItems().forEach { item ->
            IconButton(onClick = { navController.navigate(item.name) }) {
                Icon(item.icon, item.name)
            }
        }
    }
}

@Composable
private fun GetNavs(navController: NavHostController) {
    if (!isCurrentRouteBottom(navController)) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go Back")
        }
    }
}
