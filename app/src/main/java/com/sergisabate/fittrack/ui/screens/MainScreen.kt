package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sergisabate.fittrack.ui.components.FitTrackBottomAppBar
import com.sergisabate.fittrack.ui.components.FitTrackTopAppBar
import com.sergisabate.fittrack.ui.navigation.FitTrackNavigation

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold (
        topBar = { FitTrackTopAppBar(navController)},
        bottomBar = { FitTrackBottomAppBar(navController) }
    ) { innerPadding ->
        Box (
            modifier = modifier.padding(innerPadding).fillMaxSize()
        ) {
            FitTrackNavigation(navController)
        }
    }
}