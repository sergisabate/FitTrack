package com.sergisabate.fittrack.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import java.io.IOException

private const val CURRENT_WEIGHT_KEY = "current_weight"
private const val LAST_DATE_KEY = "last_date"
private const val WEIGHT_HISTORY_KEY = "weight_history"

private const val DEFAULT_STRING = ""
private const val DEFAULT_FLOAT = 0.0f
private const val DEFAULT_LONG = 0L

class WeightsRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val CURRENT_WEIGHT = floatPreferencesKey(CURRENT_WEIGHT_KEY)
        val LAST_DATE = longPreferencesKey(LAST_DATE_KEY)
        val WEIGHT_HISTORY = stringPreferencesKey(WEIGHT_HISTORY_KEY)
    }

    private val Context.dataStore by preferencesDataStore(name = "weights_datastore")
    private val dataStore = context.applicationContext.dataStore

    private val _currentWeightFlow =
        MutableStateFlow(runBlocking { getCurrentWeight() })
    val currentWeightFlow: StateFlow<Float> = _currentWeightFlow

    private val _lastDateFlow =
        MutableStateFlow(runBlocking { getLastDate() })
    val lastDateFlow: StateFlow<Long> = _lastDateFlow

    private val _weightHistoryFlow =
        MutableStateFlow(runBlocking { getWeightHistory() })
    val weightHistoryFlow: StateFlow<String> = _weightHistoryFlow

    init {
        initParameter(PreferencesKeys.CURRENT_WEIGHT, DEFAULT_FLOAT, _currentWeightFlow)
        initParameter(PreferencesKeys.LAST_DATE, DEFAULT_LONG, _lastDateFlow)
        initParameter(PreferencesKeys.WEIGHT_HISTORY, DEFAULT_STRING, _weightHistoryFlow)
    }

    private fun <T> initParameter(key: Preferences.Key<T>, default: T, mutable: MutableStateFlow<T>) {
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: default
            }
            .onEach { value ->
                mutable.value = value
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    suspend fun setCurrentWeight(currentWeight: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_WEIGHT] = currentWeight
        }
    }

    suspend fun setLastDate(lastDate: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_DATE] = lastDate
        }
    }

    suspend fun setWeightHistory(weightHistory: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_HISTORY] = weightHistory
        }
    }

    private suspend fun getCurrentWeight(): Float {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.CURRENT_WEIGHT] ?: DEFAULT_FLOAT
            }
            .first()
    }

    private suspend fun getLastDate(): Long {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.LAST_DATE] ?: DEFAULT_LONG
            }
            .first()
    }

    private suspend fun getWeightHistory(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.WEIGHT_HISTORY] ?: DEFAULT_STRING
            }
            .first()
    }

    companion object {
        @Volatile
        private var INSTANCE: WeightsRepository? = null

        fun getInstance(context: Context): WeightsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WeightsRepository(context).also { INSTANCE = it }
            }
        }
    }
}