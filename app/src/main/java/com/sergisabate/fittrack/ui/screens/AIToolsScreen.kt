package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionCard
import com.sergisabate.fittrack.ui.theme.FitTrackTheme

@Composable
fun AIToolsScreen(navController: NavController) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier)

            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.menu_generator_title),
                description = stringResource(R.string.menu_generator_description),
                modifier = Modifier.clickable { navController.navigate("GenerateMenu") },
                isClickable = true
            )
            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.exercise_generator_title),
                description = stringResource(R.string.exercise_generator_description),
                modifier = Modifier.clickable { navController.navigate("GenerateExerciseRoutine") },
                isClickable = true
            )
            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.internet_required),
                    style = MaterialTheme.typography.titleSmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier)
        }
    }
}

@Preview
@Composable
fun AIToolsScreenPreview() {
    FitTrackTheme {
        AIToolsScreen(rememberNavController())
    }
}
