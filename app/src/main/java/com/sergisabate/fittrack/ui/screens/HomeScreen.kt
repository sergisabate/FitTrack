package com.sergisabate.fittrack.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.charts.FitTrackWeightsChart
import com.sergisabate.fittrack.ui.theme.FitTrackTheme
import kotlin.math.absoluteValue

@Composable
fun HomeScreen() {
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

            // Card 2: Workout of the Day
            Card(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Entrenamiento del Día",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Full Body Workout - 45 mins",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Card 3: Nutritional information
            Card(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Información Nutricional",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Proteínas", style = MaterialTheme.typography.titleMedium)
                            Text(text = "120 g", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text(text = "Carbohidratos", style = MaterialTheme.typography.titleMedium)
                            Text(text = "250 g", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text(text = "Grasas", style = MaterialTheme.typography.titleMedium)
                            Text(text = "70 g", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Card 4: Water intake
            Card(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Consumo de Agua",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Consumido", style = MaterialTheme.typography.titleMedium)
                            Text(text = "1.5 L", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text(text = "Meta", style = MaterialTheme.typography.titleMedium)
                            Text(text = "3.0 L", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Card 5: Sleep tracking
            Card(
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Seguimiento del Sueño",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Dormido", style = MaterialTheme.typography.titleMedium)
                            Text(text = "7 hrs 30 mins", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text(text = "Calidad", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Buena", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier)
        }
    }
}

@Composable
private fun WeightControlCard() {
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()
    val weightRecords by weightsViewModel.getWeightHistory().collectAsState()

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
                    text = "Control de Peso",
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
                        Text(text = "Actual", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "%.2f kg".format(currentWeight),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Column {
                        Text(text = "Variación", style = MaterialTheme.typography.titleMedium)
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
                        Text(text = "Tendencia", style = MaterialTheme.typography.titleMedium)
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

@Preview
@Composable
fun HomeScreenPreview() {
    FitTrackTheme {
        HomeScreen()
    }
}

