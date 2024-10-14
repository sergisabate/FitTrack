package com.sergisabate.fittrack.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergisabate.fittrack.data.repositories.WeightsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class WeightRecord(
    val weight: Float = 0.0f,
    val date: Long = 0L
) {
    companion object {
        val Default = WeightRecord(0.0f, 0L)
    }
}

class WeightsViewModel(private val weightsRepository: WeightsRepository) : ViewModel() {

    fun getCurrentWeight(): StateFlow<Float> {
        return weightsRepository.currentWeightFlow
    }

    fun getWeightHistory(): StateFlow<MutableList<WeightRecord>> {
        val currentWeightHistoryJson = weightsRepository.weightHistoryFlow.value
        val weightHistoryList: MutableList<WeightRecord> = if (currentWeightHistoryJson.isNotEmpty()) {
            Gson().fromJson(currentWeightHistoryJson, object : TypeToken<List<WeightRecord>>() {}.type)
        } else {
            mutableListOf()
        }
        return MutableStateFlow(weightHistoryList)
    }

    fun addOrUpdateWeightRecord(
        weight: Float,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now().toEpochDay()
                val lastDateEpoch = weightsRepository.lastDateFlow.value
                val isTodayRecorded = lastDateEpoch == today

                val currentWeightHistoryJson = weightsRepository.weightHistoryFlow.value
                val weightHistoryList: MutableList<WeightRecord> = if (currentWeightHistoryJson.isNotEmpty()) {
                    Gson().fromJson(currentWeightHistoryJson, object : TypeToken<List<WeightRecord>>() {}.type)
                } else {
                    mutableListOf()
                }

                val newRecord = WeightRecord(weight, today)
                if (isTodayRecorded && weightHistoryList.isNotEmpty()) {
                    weightHistoryList[weightHistoryList.lastIndex] = newRecord
                } else {
                    /*
                    weightHistoryList.add(WeightRecord(weight-1.5f, today-10))
                    weightHistoryList.add(WeightRecord(weight-2f, today-9))
                    weightHistoryList.add(WeightRecord(weight-1f, today-8))
                    weightHistoryList.add(WeightRecord(weight, today-7))
                    weightHistoryList.add(WeightRecord(weight+2f, today-6))
                    weightHistoryList.add(WeightRecord(weight+3f, today-5))
                    weightHistoryList.add(WeightRecord(weight+4f, today-4))
                    weightHistoryList.add(WeightRecord(weight+3.5f, today-3))
                    weightHistoryList.add(WeightRecord(weight+2f, today-2))
                    weightHistoryList.add(WeightRecord(weight+1f, today-1))
                    */
                    weightHistoryList.add(newRecord)
                }

                val updatedWeightHistoryJson = Gson().toJson(weightHistoryList)
                weightsRepository.setWeightHistory(updatedWeightHistoryJson)

                weightsRepository.setCurrentWeight(weight)
                weightsRepository.setLastDate(today)

                onSuccess?.invoke()
            } catch (e: Exception) {
                onError?.invoke(e)
            }
        }
    }
}

class WeightsViewModelFactory(
    private val weightsRepository: WeightsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(WeightsViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return WeightsViewModel(weightsRepository) as T
    }
}