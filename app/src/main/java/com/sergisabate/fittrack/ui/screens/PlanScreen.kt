package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.calculateRecommendations
import com.sergisabate.fittrack.data.helpers.getProfileViewModel
import com.sergisabate.fittrack.data.helpers.getWeightsViewModel
import com.sergisabate.fittrack.data.viewmodels.ActivityLevel
import com.sergisabate.fittrack.data.viewmodels.CalorieGoal
import com.sergisabate.fittrack.data.viewmodels.MacroDistribution
import com.sergisabate.fittrack.data.viewmodels.ProfileViewModel
import com.sergisabate.fittrack.data.viewmodels.WeightsViewModel
import com.sergisabate.fittrack.ui.theme.FitTrackTheme

@Composable
fun PlanScreen() {
    val profileViewModel: ProfileViewModel = getProfileViewModel()
    val weightsViewModel: WeightsViewModel = getWeightsViewModel()

    val currentWeight by weightsViewModel.getCurrentWeight().collectAsState()
    val currentProfileData by profileViewModel.getProfileData().collectAsState()
    val currentLifestyleData by profileViewModel.getLifestyleData().collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier)

            var selectedActivity by remember { mutableStateOf(currentLifestyleData.activityLevel) }
            var selectedGoal by remember { mutableStateOf(currentLifestyleData.calorieGoal) }
            var selectedMacro by remember { mutableStateOf(currentLifestyleData.macroDistribution) }

            val recommendations = calculateRecommendations(
                birthDateEpochDay = currentProfileData.birthDate,
                gender = currentProfileData.gender,
                weightKg = currentWeight,
                heightCm = currentProfileData.height,
                activityLevel = selectedActivity,
                calorieGoal = selectedGoal,
                macroDistribution = selectedMacro
            )

            Column {
                DropdownSelector(
                    label = stringResource(R.string.activity_level),
                    selected = selectedActivity.displayName,
                    options = ActivityLevel.entries.map { it.displayName },
                    onSelected = { resId ->
                        selectedActivity = ActivityLevel.entries.first { it.displayName == resId }
                        profileViewModel.setActivityLevel(selectedActivity)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownSelector(
                    label = stringResource(R.string.calorie_goal),
                    selected = selectedGoal.displayName,
                    options = CalorieGoal.entries.map { it.displayName },
                    onSelected = { resId ->
                        selectedGoal = CalorieGoal.entries.first { it.displayName == resId }
                        profileViewModel.setCalorieGoal(selectedGoal)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownSelector(
                    label = stringResource(R.string.macro_distribution),
                    selected = selectedMacro.displayName,
                    options = MacroDistribution.entries.map { it.displayName },
                    onSelected = { resId ->
                        selectedMacro = MacroDistribution.entries.first { it.displayName == resId }
                        profileViewModel.setMacroDistribution(selectedMacro)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            MacroIntakeCard(
                protein = recommendations.proteinGrams,
                carb = recommendations.carbGrams,
                fat = recommendations.fatGrams
            )

            WaterIntakeCard(
                minimumWaterIntake = recommendations.minimumWaterIntakeMl,
                recommendedWaterIntake = recommendations.recommendedWaterIntakeMl
            )

            SleepHoursCard(
                minimumSleepHours = recommendations.minimumSleepHours,
                maximumSleepHours = recommendations.maximumSleepHours
            )

            Spacer(modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selected: Int,
    options: List<Int>,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = stringResource(selected),
            onValueChange = {},
            label = {
                Text(text = label, style = MaterialTheme.typography.titleMedium)
            },
            readOnly = true,
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(option),
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MacroIntakeCard(protein: Int, carb: Int, fat: Int) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.macro_intake),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(stringResource(R.string.protein), style = MaterialTheme.typography.titleMedium)
                    Text("$protein g", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(stringResource(R.string.carbs), style = MaterialTheme.typography.titleMedium)
                    Text("$carb g", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(stringResource(R.string.fats), style = MaterialTheme.typography.titleMedium)
                    Text("$fat g", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WaterIntakeCard(minimumWaterIntake: Int, recommendedWaterIntake: Int) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.water_intake), style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(stringResource(R.string.minimum), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(
                            R.string.liters_amount,
                            minimumWaterIntake / 1000.0
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column {
                    Text(stringResource(R.string.recommended), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(
                            R.string.liters_amount,
                            recommendedWaterIntake / 1000.0
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepHoursCard(minimumSleepHours: Int, maximumSleepHours: Int) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.sleep_hours), style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(stringResource(R.string.minimum), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(
                            R.string.hours_and_minutes_amount,
                            minimumSleepHours / 60,
                            minimumSleepHours % 60
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Column {
                    Text(stringResource(R.string.maximum), style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = stringResource(
                            R.string.hours_and_minutes_amount,
                            maximumSleepHours / 60,
                            maximumSleepHours % 60
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ProgressScreenPreview() {
    FitTrackTheme {
        PlanScreen()
    }
}

