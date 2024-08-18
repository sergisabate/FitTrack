package com.sergisabate.fittrack.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument

@Composable
fun FitTrackNavigation(navController: NavHostController) {
    val topItems = getTopItems()
    val bottomItems = getBottomItems()
    val settingsItems = getSettingsItems()
    val menuItems = getMenuItems()
    val exerciseRoutineItems = getExerciseRoutineItems()
    val bluetoothItems = getBluetoothItems()
    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        topItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title }),
                enterTransition = { fadeInTransition() }) {
                item.screen(navController)
            }
        }
        bottomItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title })) { item.screen(navController) }
        }
        settingsItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title }),
                enterTransition = { fadeInTransition() }) {
                item.screen(navController)
            }
        }
        menuItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title }),
                enterTransition = { fadeInTransition() }) {
                item.screen(navController)
            }
        }
        exerciseRoutineItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title }),
                enterTransition = { fadeInTransition() }) {
                item.screen(navController)
            }
        }
        bluetoothItems.forEach { item ->
            composable(item.route,
                listOf(navArgument("title") { defaultValue = item.title }),
                enterTransition = { fadeInTransition() }) {
                item.screen(navController)
            }
        }
    }
}

private fun fadeInTransition(): EnterTransition {
    return fadeIn(tween(1000))
}

@Composable
fun isCurrentRouteBottom(navController: NavHostController): Boolean {
    val currentRoute = currentRoute(navController)
    val bottomItems = getBottomItems()
    return bottomItems.any { it.route == currentRoute }
}

@Composable
fun currentRoute(navController: NavHostController): String? =
    navController.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun currentNav(navController: NavHostController): NavDestination? =
    navController.currentBackStackEntryAsState().value?.destination

@Composable
fun currentTitle(navDestination: NavDestination): String? =
    navDestination.arguments["title"]?.defaultValue?.toString()
