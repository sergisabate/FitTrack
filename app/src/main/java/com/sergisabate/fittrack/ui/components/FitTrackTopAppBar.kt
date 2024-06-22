package com.sergisabate.fittrack.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sergisabate.fittrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTrackTopAppBar(title: String = stringResource(R.string.app_name)){
    Column {
        TopAppBar(
            title = { Text(title) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            actions = {
                IconButton(onClick = {/* Do Something*/ }) {
                    Icon(Icons.Filled.Settings, null)
                }
            }
        )
    }
}
