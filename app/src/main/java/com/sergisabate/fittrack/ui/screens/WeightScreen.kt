package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getPreferencesViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.PreferencesViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.charts.FitTrackWeightsChart
import com.sergisabate.fittrack.ui.components.FitTrackTitleDescriptionCard
import kotlin.math.absoluteValue

@Composable
fun WeightScreen(navController: NavController) {
    val preferencesViewModel: PreferencesViewModel = getPreferencesViewModel()
    val currentLinkedDevice by preferencesViewModel.getBluetoothDevice().collectAsState()

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

            WeightControlCard()

            FitTrackTitleDescriptionCard(
                title = stringResource(R.string.manage_weights),
                description = stringResource(R.string.manage_weights_description),
                isClickable = true,
                modifier = Modifier.clickable { navController.navigate("ManageWeights") }
            )

            if (currentLinkedDevice.isNotEmpty()) {
                FitTrackTitleDescriptionCard(
                    title = stringResource(R.string.weigh),
                    description = stringResource(R.string.weigh_description),
                    isClickable = true,
                    modifier = Modifier.clickable { navController.navigate("WeighScreen") }
                )
            }

            Spacer(modifier = Modifier)
        }
    }
}

@Composable
private fun WeightControlCard() {
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()
    val weightRecords by weightsViewModel.getWeightRecords().collectAsState()

    if (weightRecords.isNotEmpty()) {
        Card(
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.weight_control),
                    style = MaterialTheme.typography.titleLarge
                )

                if (weightRecords.size > 1) {
                    FitTrackWeightsChart(modifier = Modifier, weightRecords = weightRecords)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val currentWeight = weightRecords.last().weight

                val weightVariation = if (weightRecords.size > 1) {
                    currentWeight - weightRecords[weightRecords.size - 2].weight
                } else {
                    0f
                }

                val trendWeight = if (weightRecords.size >= 2) {
                    val recentWeights = weightRecords.takeLast(5).map { it.weight }
                    val differences = recentWeights.zipWithNext { a, b -> b - a }
                    val averageDifference = differences.average().toFloat()
                    recentWeights.last() + averageDifference
                } else {
                    currentWeight
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = stringResource(R.string.current), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "%.2f kg".format(currentWeight),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Column {
                        Text(text = stringResource(R.string.variation), style = MaterialTheme.typography.titleMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "%.2f kg".format(weightVariation.absoluteValue),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            if (weightVariation != 0f) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.rotate(if (weightVariation > 0) 180f else 0f),
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }
                    Column {
                        Text(text = stringResource(R.string.trend), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "%.2f kg".format(trendWeight),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
