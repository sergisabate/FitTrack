package com.sergisabate.fittrack.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument

@Composable
fun FitTrackNavigation(navController: NavHostController) {
    val allItems = listOf(
        getTopItems(),
        getBottomItems(),
        getSettingsItems(),
        getAIItems(),
        getWeightItems()
    ).flatten()

    NavHost(
        navController = navController,
        startDestination = "Weight",
    ) {
        allItems.forEach { item -> buildComposable(item, navController) }
    }
}

private fun NavGraphBuilder.buildComposable(
    item: NavItem,
    navController: NavHostController
) {
    composable(
        route = item.route,
        arguments = listOf(navArgument("title") { defaultValue = item.title }),
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) }
    ) {
        item.screen(navController)
    }
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
