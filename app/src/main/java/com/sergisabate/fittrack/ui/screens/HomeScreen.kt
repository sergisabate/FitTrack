package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.ui.components.FitTrackTopAppBar

@Composable
fun HomeScreen() {
    Scaffold(
        topBar = { FitTrackTopAppBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary,
                text =
                """
                   Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque condimentum aliquam neque id laoreet. Mauris ac fermentum urna. Phasellus non tortor tempor, mattis erat vel, interdum augue. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi eu faucibus massa. Ut ultricies, erat at consequat lacinia, turpis lectus lacinia erat, vel blandit lectus tellus nec lacus. Morbi at consectetur eros, a dapibus eros. Nunc congue gravida massa, a tincidunt enim lobortis ac. Donec viverra lectus in eros ornare accumsan.

                   Ut dapibus mauris aliquet libero porta semper. Aenean faucibus fermentum ligula, mollis luctus tortor ultricies et. Integer accumsan dictum sagittis. Pellentesque non mattis libero, et condimentum lorem. Maecenas vitae diam maximus, gravida orci eu, varius metus. Etiam sit amet turpis elit. Sed nec finibus turpis. Donec non ligula sit amet elit cursus ultrices. Aliquam non maximus mi, sit amet molestie odio. Nulla at facilisis neque. Quisque in consequat arcu.
                   
                   Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque condimentum aliquam neque id laoreet. Mauris ac fermentum urna. Phasellus non tortor tempor, mattis erat vel, interdum augue. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi eu faucibus massa. Ut ultricies, erat at consequat lacinia, turpis lectus lacinia erat, vel blandit lectus tellus nec lacus. Morbi at consectetur eros, a dapibus eros. Nunc congue gravida massa, a tincidunt enim lobortis ac. Donec viverra lectus in eros ornare accumsan.

                   Ut dapibus mauris aliquet libero porta semper. Aenean faucibus fermentum ligula, mollis luctus tortor ultricies et. Integer accumsan dictum sagittis. Pellentesque non mattis libero, et condimentum lorem. Maecenas vitae diam maximus, gravida orci eu, varius metus. Etiam sit amet turpis elit. Sed nec finibus turpis. Donec non ligula sit amet elit cursus ultrices. Aliquam non maximus mi, sit amet molestie odio. Nulla at facilisis neque. Quisque in consequat arcu.
                """.trimIndent(),
            )
        }
    }
}
