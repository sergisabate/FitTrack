package com.sergisabate.fittrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sergisabate.fittrack.R

@Composable
fun FitTrackNavigation(navController: NavHostController) {
    val items = getMenuItems()
    NavHost(
        navController = navController,
        startDestination = stringResource(R.string.home),
    ) {
        items.forEach { item ->
            composable(item.name) { item.screen() }
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? =
    navController.currentBackStackEntryAsState().value?.destination?.route
