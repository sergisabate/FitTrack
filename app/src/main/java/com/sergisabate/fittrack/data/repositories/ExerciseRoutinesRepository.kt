package com.sergisabate.fittrack.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
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

private const val ROUTINE1_JSON_KEY = "routine1_json"
private const val ROUTINE2_JSON_KEY = "routine2_json"
private const val ROUTINE3_JSON_KEY = "routine3_json"
private const val ROUTINE4_JSON_KEY = "routine4_json"
private const val ROUTINE5_JSON_KEY = "routine5_json"
private const val TOTAL_ROUTINES_KEY = "total_routines"
private const val ROUTINE_SELECTED_KEY = "routine_selected"
private const val DEFAULT_STRING = ""
private const val DEFAULT_INT = 0

private val Context.dataStore by preferencesDataStore(name = "exercise_routines")

class ExerciseRoutinesRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val ROUTINE1_JSON = stringPreferencesKey(ROUTINE1_JSON_KEY)
        val ROUTINE2_JSON = stringPreferencesKey(ROUTINE2_JSON_KEY)
        val ROUTINE3_JSON = stringPreferencesKey(ROUTINE3_JSON_KEY)
        val ROUTINE4_JSON = stringPreferencesKey(ROUTINE4_JSON_KEY)
        val ROUTINE5_JSON = stringPreferencesKey(ROUTINE5_JSON_KEY)
        val TOTAL_ROUTINES = intPreferencesKey(TOTAL_ROUTINES_KEY)
        val ROUTINE_SELECTED = stringPreferencesKey(ROUTINE_SELECTED_KEY)
    }

    private val dataStore = context.applicationContext.dataStore

    private val _routine1JsonFlow = MutableStateFlow(runBlocking { getRoutineJson(PreferencesKeys.ROUTINE1_JSON) })
    val routine1JsonFlow: StateFlow<String> = _routine1JsonFlow

    private val _routine2JsonFlow = MutableStateFlow(runBlocking { getRoutineJson(PreferencesKeys.ROUTINE2_JSON) })
    val routine2JsonFlow: StateFlow<String> = _routine2JsonFlow

    private val _routine3JsonFlow = MutableStateFlow(runBlocking { getRoutineJson(PreferencesKeys.ROUTINE3_JSON) })
    val routine3JsonFlow: StateFlow<String> = _routine3JsonFlow

    private val _routine4JsonFlow = MutableStateFlow(runBlocking { getRoutineJson(PreferencesKeys.ROUTINE4_JSON) })
    val routine4JsonFlow: StateFlow<String> = _routine4JsonFlow

    private val _routine5JsonFlow = MutableStateFlow(runBlocking { getRoutineJson(PreferencesKeys.ROUTINE5_JSON) })
    val routine5JsonFlow: StateFlow<String> = _routine5JsonFlow

    private val _totalRoutinesFlow = MutableStateFlow(runBlocking { getTotalRoutines() })
    val totalRoutinesFlow: StateFlow<Int> = _totalRoutinesFlow

    private val _routineSelectedFlow = MutableStateFlow(runBlocking { getRoutineSelected() })
    val routineSelectedFlow: StateFlow<String> = _routineSelectedFlow

    init {
        initParameter(PreferencesKeys.ROUTINE1_JSON, DEFAULT_STRING, _routine1JsonFlow)
        initParameter(PreferencesKeys.ROUTINE2_JSON, DEFAULT_STRING, _routine2JsonFlow)
        initParameter(PreferencesKeys.ROUTINE3_JSON, DEFAULT_STRING, _routine3JsonFlow)
        initParameter(PreferencesKeys.ROUTINE4_JSON, DEFAULT_STRING, _routine4JsonFlow)
        initParameter(PreferencesKeys.ROUTINE5_JSON, DEFAULT_STRING, _routine5JsonFlow)
        initParameter(PreferencesKeys.TOTAL_ROUTINES, DEFAULT_INT, _totalRoutinesFlow)
        initParameter(PreferencesKeys.ROUTINE_SELECTED, DEFAULT_STRING, _routineSelectedFlow)
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

    suspend fun setExerciseRoutineJson(routineJson: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ROUTINE5_JSON] = preferences[PreferencesKeys.ROUTINE4_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.ROUTINE4_JSON] = preferences[PreferencesKeys.ROUTINE3_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.ROUTINE3_JSON] = preferences[PreferencesKeys.ROUTINE2_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.ROUTINE2_JSON] = preferences[PreferencesKeys.ROUTINE1_JSON] ?: DEFAULT_STRING
            preferences[PreferencesKeys.ROUTINE1_JSON] = routineJson
        }
        updateTotalRoutines()
    }

    suspend fun setExerciseRoutineSelected(index: Int) {
        val indexModule = if (index % 5 == 0) 5 else index % 5

        val selectedKey = stringPreferencesKey("routine${indexModule}_json")

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ROUTINE_SELECTED] = preferences[selectedKey] ?: DEFAULT_STRING
        }
    }

    private suspend fun getRoutineJson(preferencesKey: Preferences.Key<String>): String {
        return dataStore.data
            .map { preferences ->
                preferences[preferencesKey] ?: DEFAULT_STRING
            }
            .first()
    }

    private suspend fun getTotalRoutines(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.TOTAL_ROUTINES] ?: DEFAULT_INT
            }
            .first()
    }

    private suspend fun getRoutineSelected(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.ROUTINE_SELECTED] ?: DEFAULT_STRING
            }
            .first()
    }

    private suspend fun updateTotalRoutines() {
        val keys = listOf(
            PreferencesKeys.ROUTINE1_JSON,
            PreferencesKeys.ROUTINE2_JSON,
            PreferencesKeys.ROUTINE3_JSON,
            PreferencesKeys.ROUTINE4_JSON,
            PreferencesKeys.ROUTINE5_JSON
        )

        val filledRoutines = keys.count { key ->
            runBlocking { getRoutineJson(key) != DEFAULT_STRING }
        }

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_ROUTINES] = filledRoutines
        }
        _totalRoutinesFlow.value = filledRoutines
    }

    companion object {
        @Volatile
        private var INSTANCE: ExerciseRoutinesRepository? = null

        fun getInstance(context: Context): ExerciseRoutinesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExerciseRoutinesRepository(context).also { INSTANCE = it }
            }
        }
    }
}
