package com.sergisabate.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sergisabate.fittrack.R
import com.sergisabate.fittrack.data.helpers.getExerciseRoutinesViewModel
import com.sergisabate.fittrack.data.viewmodels.Exercise
import com.sergisabate.fittrack.data.viewmodels.ExerciseRoutinesViewModel
import com.sergisabate.fittrack.data.viewmodels.Session

@Composable
fun ViewExerciseRoutineScreen() {
    val exerciseRoutinesViewModel: ExerciseRoutinesViewModel = getExerciseRoutinesViewModel()
    val routineData by exerciseRoutinesViewModel.getExerciseRoutineSelected().collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            routineData.let { routine ->
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = routine.title,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        ExerciseRoutineSection(stringResource(R.string.warm_up), routine.warmUp)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        ExerciseRoutineSection(stringResource(R.string.main_exercises), routine.mainExercises)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        ExerciseRoutineSection(stringResource(R.string.cool_down), routine.coolDown)
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.additional_info),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = routine.aditionalInfo,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseRoutineSection(exerciseType: String, session: Session) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = exerciseType,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        session.exercises.forEach { exercise ->
            ExerciseCard(exercise)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = exercise.description,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 4.dp, end = 8.dp, bottom = 4.dp)
                    .weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.objectives),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            exercise.objectives.forEach { objective ->
                Text(
                    text = "- $objective",
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.equipment_needed),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            exercise.equipment.forEach { equipment ->
                Text(
                    text = "- $equipment",
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.instructions),
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            exercise.instructions.forEach { instruction ->
                Text(
                    text = "- $instruction",
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
