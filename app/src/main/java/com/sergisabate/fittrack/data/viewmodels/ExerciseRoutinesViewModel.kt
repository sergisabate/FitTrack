package com.sergisabate.fittrack.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sergisabate.fittrack.data.repositories.ExerciseRoutinesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExerciseRoutine(
    val title: String,
    val date: String,
    val warmUp: Session,
    val mainExercises: Session,
    val coolDown: Session,
    val aditionalInfo: String
)

data class Session(
    val exercises: List<Exercise>
)

data class Exercise(
    val description: String,
    val objectives: List<String>,
    val equipment: List<String>,
    val instructions: List<String>,
)

class ExerciseRoutinesViewModel(private val exerciseRoutinesRepository: ExerciseRoutinesRepository) : ViewModel() {

    fun setExerciseRoutineJson(
        routineJson: String,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                exerciseRoutinesRepository.setExerciseRoutineJson(routineJson)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun setExerciseRoutineSelected(
        index: Int,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                exerciseRoutinesRepository.setExerciseRoutineSelected(index)
                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }

    fun getExerciseRoutineData(number: Int): StateFlow<ExerciseRoutine> {
        val routineJson = when (number) {
            1 -> exerciseRoutinesRepository.routine1JsonFlow
            2 -> exerciseRoutinesRepository.routine2JsonFlow
            3 -> exerciseRoutinesRepository.routine3JsonFlow
            4 -> exerciseRoutinesRepository.routine4JsonFlow
            5 -> exerciseRoutinesRepository.routine5JsonFlow
            else -> exerciseRoutinesRepository.routine1JsonFlow
        }
        val routineData: ExerciseRoutine = Gson().fromJson(routineJson.value, ExerciseRoutine::class.java)
        return MutableStateFlow(routineData)
    }

    fun getTotalRoutines(): StateFlow<Int> {
        return exerciseRoutinesRepository.totalRoutinesFlow
    }

    fun getExerciseRoutineSelected(): StateFlow<ExerciseRoutine> {
        val routineSelectedFlow = exerciseRoutinesRepository.routineSelectedFlow
        val routineData: ExerciseRoutine = Gson().fromJson(routineSelectedFlow.value, ExerciseRoutine::class.java)
        return MutableStateFlow(routineData)
    }
}

class ExerciseRoutinesViewModelFactory(
    private val exerciseRoutinesRepository: ExerciseRoutinesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(ExerciseRoutinesViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return ExerciseRoutinesViewModel(exerciseRoutinesRepository) as T
    }
}
