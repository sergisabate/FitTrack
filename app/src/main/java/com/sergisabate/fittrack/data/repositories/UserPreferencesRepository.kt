package com.sergisabate.fittrack.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
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

private const val IS_IN_DARK_MODE_KEY = "is_in_dark_mode"
private const val NAME_KEY = "name"
private const val BIRTHDATE_KEY = "birthDate"
private const val GENDER_KEY = "gender"
private const val HEIGHT_KEY = "height"
private const val BLUETOOTH_DEVICE_KEY = "bluetooth_device"

private const val DEFAULT_DARK_MODE = 2
private const val DEFAULT_STRING = ""
private const val DEFAULT_INT = 0
private const val DEFAULT_LONG = 0L

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository private constructor(context: Context) {

    private object PreferencesKeys {
        val IS_IN_DARK_MODE = intPreferencesKey(IS_IN_DARK_MODE_KEY)
        val NAME = stringPreferencesKey(NAME_KEY)
        val BIRTHDATE = longPreferencesKey(BIRTHDATE_KEY)
        val GENDER = intPreferencesKey(GENDER_KEY)
        val HEIGHT = intPreferencesKey(HEIGHT_KEY)
        val BLUETOOTH_DEVICE = stringPreferencesKey(BLUETOOTH_DEVICE_KEY)
    }

    private val dataStore = context.applicationContext.dataStore

    private val _isInDarkModeFlow = MutableStateFlow(runBlocking { getDarkModePreference() })
    val isInDarkModeFlow: StateFlow<Int> = _isInDarkModeFlow

    private val _nameFlow = MutableStateFlow(runBlocking { getNamePreference() })
    private val _birthDateFlow = MutableStateFlow(runBlocking { getBirthDatePreference() })
    private val _genderFlow = MutableStateFlow(runBlocking { getGenderPreference() })
    private val _heightFlow = MutableStateFlow(runBlocking { getHeightPreference() })
    private val _bluetoothDeviceFlow = MutableStateFlow(runBlocking { getBluetoothDevice() })

    val nameFlow: StateFlow<String> = _nameFlow
    val birthDateFlow: StateFlow<Long> = _birthDateFlow
    val genderFlow: StateFlow<Int> = _genderFlow
    val heightFlow: StateFlow<Int> = _heightFlow
    val bluetoothDeviceFlow: StateFlow<String> = _bluetoothDeviceFlow

    init {
        initParameter(PreferencesKeys.IS_IN_DARK_MODE, DEFAULT_DARK_MODE, _isInDarkModeFlow)
        initParameter(PreferencesKeys.NAME, DEFAULT_STRING, _nameFlow)
        initParameter(PreferencesKeys.BIRTHDATE, DEFAULT_LONG, _birthDateFlow)
        initParameter(PreferencesKeys.GENDER, DEFAULT_INT, _genderFlow)
        initParameter(PreferencesKeys.HEIGHT, DEFAULT_INT, _heightFlow)
        initParameter(PreferencesKeys.BLUETOOTH_DEVICE, DEFAULT_STRING, _bluetoothDeviceFlow)
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

    suspend fun setDarkMode(isInDarkMode: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_IN_DARK_MODE] = isInDarkMode
        }
    }

    suspend fun setName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NAME] = name
        }
    }

    suspend fun setBirthDate(birthDate: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIRTHDATE] = birthDate
        }
    }

    suspend fun setGender(gender: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GENDER] = gender
        }
    }

    suspend fun setHeight(height: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HEIGHT] = height
        }
    }

    suspend fun setBluetoothDevice(bluetoothDevice: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BLUETOOTH_DEVICE] = bluetoothDevice
        }
    }

    private suspend fun getDarkModePreference(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.IS_IN_DARK_MODE] ?: DEFAULT_DARK_MODE
            }
            .first()
    }

    private suspend fun getNamePreference(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.NAME] ?: DEFAULT_STRING
            }
            .first()
    }

    private suspend fun getBirthDatePreference(): Long {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.BIRTHDATE] ?: DEFAULT_LONG
            }
            .first()
    }

    private suspend fun getGenderPreference(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.GENDER] ?: DEFAULT_INT
            }
            .first()
    }

    private suspend fun getHeightPreference(): Int {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.HEIGHT] ?: DEFAULT_INT
            }
            .first()
    }

    private suspend fun getBluetoothDevice(): String {
        return dataStore.data
            .map { preferences ->
                preferences[PreferencesKeys.BLUETOOTH_DEVICE] ?: DEFAULT_STRING
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