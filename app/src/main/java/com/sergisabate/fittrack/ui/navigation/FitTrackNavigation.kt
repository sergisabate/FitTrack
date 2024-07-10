package com.sergisabate.fittrack.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.ui.screens.SettingsScreen

@Composable
fun FitTrackNavigation(navController: NavHostController) {
    val topItems = listOf(
        NavItem(
            icon = Icons.Default.Settings,
            name = stringResource(R.string.settings),
            screen = { SettingsScreen( navController ) }
        )
    )
    val bottomItems = getBottomItems()
    val settingsItems = getSettingsItems()
    NavHost(
        navController = navController,
        startDestination = stringResource(R.string.home),
    ) {
        topItems.forEach { item ->
            composable(item.name,
                enterTransition = { fadeInTransition() }) {
                item.screen()
            }
        }
        bottomItems.forEach { item ->
            composable(item.name) { item.screen() }
        }
        settingsItems.forEach { item ->
            composable(item.name,
                enterTransition = { fadeInTransition() }) {
                item.screen()
            }
        }
    }
}

private fun fadeInTransition(): EnterTransition {
    return fadeIn(tween(1000))
}

@Composable
fun currentRoute(navController: NavHostController): String? =
    navController.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun isCurrentRouteTop(navController: NavHostController): Boolean {
    val currentRoute = currentRoute(navController)
    val topItems = getTopItems()
    return topItems.any { it.name == currentRoute }
}

@Composable
fun isCurrentRouteBottom(navController: NavHostController): Boolean {
    val currentRoute = currentRoute(navController)
    val bottomItems = getBottomItems()
    return bottomItems.any { it.name == currentRoute }
}
