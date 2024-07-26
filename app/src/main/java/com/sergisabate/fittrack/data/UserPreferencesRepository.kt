package com.sergisabate.fittrack.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
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

private const val IS_IN_DARK_MODE_KEY = "is_in_dark_mode"
private const val DEFAULT_DARK_MODE = 2

// Extension property to create the DataStore
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val IS_IN_DARK_MODE = intPreferencesKey(IS_IN_DARK_MODE_KEY)
    }

    private val dataStore = context.applicationContext.dataStore

    private val _isInDarkModeFlow = MutableStateFlow(runBlocking { getDarkModePreference() })
    val isInDarkModeFlow: StateFlow<Int> = _isInDarkModeFlow

    init {
        // Collect the datastore changes and update the state flow
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PreferencesKeys.IS_IN_DARK_MODE] ?: DEFAULT_DARK_MODE
            }
            .onEach { darkMode ->
                _isInDarkModeFlow.value = darkMode
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    suspend fun setDarkMode(isInDarkMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_IN_DARK_MODE] = isInDarkMode
        }
    }

    private suspend fun getDarkModePreference(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.IS_IN_DARK_MODE] ?: DEFAULT_DARK_MODE
            }
            .first()
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferencesRepository(context).also { INSTANCE = it }
            }
        }
    }
}
