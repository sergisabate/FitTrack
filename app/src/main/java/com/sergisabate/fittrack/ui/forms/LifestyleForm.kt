package com.sergisabate.fittrack.ui.forms

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.viewmodels.ActivityLevel
import com.sergisabate.fittrack.data.viewmodels.CalorieGoal
import com.sergisabate.fittrack.data.viewmodels.LifestyleData
import com.sergisabate.fittrack.data.viewmodels.MacroDistribution

@Composable
fun LifestyleForm(lifestyleData: LifestyleData, onSubmit: (ActivityLevel, CalorieGoal, MacroDistribution) -> Unit) {
    val currentActivityLevel = lifestyleData.activityLevel
    val currentCalorieGoal = lifestyleData.calorieGoal
    val currentMacroDistribution = lifestyleData.macroDistribution

    var activityLevel by remember { mutableStateOf(currentActivityLevel) }
    var calorieGoal by remember { mutableStateOf(currentCalorieGoal) }
    var macroDistribution by remember { mutableStateOf(currentMacroDistribution) }

    var isLoading by remember { mutableStateOf(false) }

    DropdownSelector(
        label = stringResource(R.string.activity_level),
        selected = activityLevel.displayName,
        options = ActivityLevel.entries.map { it.displayName },
        onSelected = { resId ->
            activityLevel = ActivityLevel.entries.first { it.displayName == resId }
        }
    )

    DropdownSelector(
        label = stringResource(R.string.calorie_goal),
        selected = calorieGoal.displayName,
        options = CalorieGoal.entries.map { it.displayName },
        onSelected = { resId ->
            calorieGoal = CalorieGoal.entries.first { it.displayName == resId }
        }
    )

    DropdownSelector(
        label = stringResource(R.string.macro_distribution),
        selected = macroDistribution.displayName,
        options = MacroDistribution.entries.map { it.displayName },
        onSelected = { resId ->
            macroDistribution = MacroDistribution.entries.first { it.displayName == resId }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = {
            isLoading = true
            onSubmit(activityLevel, calorieGoal, macroDistribution)
            isLoading = false
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(24.dp)
            )
        } else {
            Text(text = stringResource(R.string.submit))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelector(
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
